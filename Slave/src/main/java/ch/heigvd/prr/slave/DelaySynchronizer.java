/**
 * file:        DelaySynchronizer.java 
 * created:     26.10.2017
 */


package ch.heigvd.prr.slave;

import ch.heigvd.prr.common.Protocol;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The DelaySynchronizer class purpose is to calculate the communication delay
 * during the synchronization with the Master (DELAY_REQUEST / DELAY_RESPONSE
 * messages).
 *
 * It communicates directly with the Master using UDP.
 */
public class DelaySynchronizer implements Runnable {

    // Socket to send UDP messages
    private final DatagramSocket socket;

    // Master IP address
    private final InetAddress masterIPAddress;

    // The master listening port
    private final int masterPort;

    // Synchronized parent clock to get the current offset
    private final SynchronizedClock parentClock;

    // Time of last DELAY_REQUEST
    private long lastDelayRequestTime;

    // Last calculated delay
    private long delay;

    // Last delay ID 
    private int delayID;

    /**
     * Constructor, creates the UDP socket.
     *
     * @param parentClock the parent clock that manages this instance
     * @throws SocketException
     */
    public DelaySynchronizer(SynchronizedClock parentClock) 
            throws SocketException {
        this.masterIPAddress = parentClock.getMasterAddress();
        this.masterPort = Protocol.DELAY_COMMUNICATION_PORT;
        this.parentClock = parentClock;

        socket = new DatagramSocket();
    }

    /**
     * Get the last calculated delay
     *
     * @return the last calculated delay
     */
    public synchronized long getDelay() {
        return delay;
    }

    /**
     * Set a new delay value
     *
     * @param delay the new delay value
     */
    private synchronized void setDelay(long delay) {
        this.delay = delay;
    }

    /**
     * Calculates a random waiting time inside the PTP range.
     *
     * @return a random waiting time
     */
    protected static long getRandomWaitingTime() {
        int range = (Protocol.INTERVAL_DELAY_MAX - Protocol.INTERVAL_DELAY_MIN) 
                + 1;
        return (long) (Math.random() * range) + Protocol.INTERVAL_DELAY_MIN;
    }

    @Override
    public void run() {

        while (true) {

            {
                try {
                    // Create the content of the DELAY_REQUEST message
                    ByteBuffer buffer = ByteBuffer.allocate(32);
                    buffer.put(Protocol.getByte(Protocol.Code.DELAY_REQUEST));
                    buffer.putInt(this.delayID);

                    byte[] data = buffer.array();

                    // Create a UDP packet
                    DatagramPacket packet = new DatagramPacket(
                            data,
                            data.length,
                            masterIPAddress,
                            masterPort
                    );

                    // Send the request
                    socket.send(packet);

                    // Save sending time
                    lastDelayRequestTime = System.currentTimeMillis() 
                            + parentClock.getOffset();

                } catch (IOException ex) {
                    Logger.getLogger(DelaySynchronizer.class.getName()).log(
                            Level.SEVERE, null, ex
                    );
                }
            }

            {
                try {

                    // Prepare the container to read the Master response
                    byte[] buf = new byte[32];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);

                    // Wait for the DELAY_RESPONSE message
                    socket.receive(packet);
                    
                    ByteBuffer buffer = ByteBuffer.wrap(packet.getData());

                    // If what has been sent is indeed a DELAY_RESPONSE
                    if (Protocol.getEnum(buffer.get(0)) 
                            == Protocol.Code.DELAY_RESPONSE) {
                        
                        // Get message ID
                        int id = buffer.getInt(1);

                        // If the ID matches the last ID
                        if (id == delayID) {
                            // Get Master time
                            long masterTime = buffer.getLong(5);
                            
                            // Set a new delay
                            setDelay((masterTime - lastDelayRequestTime) / 2);
                            
                            // Increase ID
                            delayID++;
                        }
                    }

                    // Attendre un temps aléatoire 
                    Thread.sleep(getRandomWaitingTime());

                } catch (IOException ex) {
                    Logger.getLogger(DelaySynchronizer.class.getName()).log(
                            Level.SEVERE, null, ex
                    );
                } catch (InterruptedException ex) {
                    Logger.getLogger(DelaySynchronizer.class.getName()).log(
                            Level.SEVERE, null, ex
                    );
                }
            }
        }
    }

}
