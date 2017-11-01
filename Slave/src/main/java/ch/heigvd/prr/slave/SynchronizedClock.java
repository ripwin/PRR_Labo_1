/**
 * file:        SynchronizedClock.java
 * created:     12.10.2017
 */
package ch.heigvd.prr.slave;

import ch.heigvd.prr.common.Protocol;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The SynchronizedClock class implements the slave part of the Precision Time
 * Protocol. Its purpose is to provide a synchronized time, computed by
 * calculating an offset and a delay to the Master clock.
 *
 * The SynchronizedClock calculates the offset (SYNC and FOLLOW_UP messages) and
 * uses a DelaySynchronizer instance to manage the communication delay.
 *
 * Some methods use java lock mechanisms to manage thread concurrency.
 */
public class SynchronizedClock implements Runnable {

    // Multicast socket to communicate with master
    private MulticastSocket socket;

    // UDP multicast group
    private InetAddress group;

    // master IP address
    private InetAddress masterAddress;

    // Time of SYNC reception
    private long lastSyncTime;

    // Last received SYNC ID
    private int lastSyncID;

    // DelaySynchronizer to manage the communication delay
    private DelaySynchronizer delaySynchronizer;

    // Last calculated offset
    private long lastOffset;

    // Variable to keep track of process quit requests
    private boolean quitProcess = false;

    /**
     * Constructor, establishes a connection to the multicast group designed by
     * the given address and port.
     *
     * @param address the multicast address
     * @param port the port
     * @throws IOException
     */
    public SynchronizedClock(InetAddress address, int port) throws IOException {
        // Create multicast socket and join the group
        socket = new MulticastSocket(port);
        group = address;
        socket.joinGroup(address);
    }

    /**
     * Returns the time in ms. The time is based on the
     * System.currentTimeMillis() method and synchronized with the Master
     * application.
     *
     * @return the time in ms.
     */
    public synchronized long getSynchronizedTime() {
        long time = System.currentTimeMillis() + lastOffset;

        if (delaySynchronizer != null) {
            time += delaySynchronizer.getDelay();
        }

        return time;
    }

    /**
     * Get the last calculated offset.
     *
     * @return the offset
     */
    protected synchronized long getOffset() {
        return lastOffset;
    }

    /**
     * Set a new offset.
     *
     * @param offset the new offset value.
     */
    protected synchronized void setOffset(long offset) {
        this.lastOffset = offset;
    }
    
    /**
     * Set the delaySynchronizer instance.
     * @param delaySynchronizer the new delaySynchronizer instance
     */
    private synchronized void setDelaySynchronizer(DelaySynchronizer delaySynchronizer) {
        this.delaySynchronizer = delaySynchronizer;
    }

    /**
     * Get the Master application IP address.
     *
     * @return the Master application IP Address.
     */
    public InetAddress getMasterAddress() {
        return masterAddress;
    }

    @Override
    public void run() {
        try {
            // Loop to manage Master messages
            while (true) {
                // Read the message
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                ByteBuffer buffer = ByteBuffer.wrap(packet.getData());

                // Switch actions according to received message
                switch (Protocol.getEnum(buffer.get(0))) {
                    case SYNC:

                        // Retrieve the message ID
                        lastSyncID = buffer.getInt(1);

                        // Save local reception time
                        lastSyncTime = System.currentTimeMillis();
                        break;

                    case FOLLOW_UP:

                        // Retrieve the message ID
                        int id = buffer.getInt(9);

                        // If the last received SYNC and the FOLLOW_UP IDs match
                        if (id == this.lastSyncID) {
                            // Retrieve master time
                            long masterTime = buffer.getLong(1);

                            // Set new offset value
                            setOffset(masterTime - lastSyncTime);

                            /*
                              Start a new DelaySynchrizer instance if it's the 
                              first received FOLLOW_UP message
                             */
                            if (delaySynchronizer == null) {
                                // Retrieve master IP address
                                masterAddress = packet.getAddress();

                                // Start a new DelaySynchronizer
                                setDelaySynchronizer(new DelaySynchronizer(this));
                                new Thread(delaySynchronizer).start();
                            }
                        }
                        break;

                    default:
                        break;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(SynchronizedClock.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
