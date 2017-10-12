package ch.heigvd.prr.slave;

import ch.heigvd.prr.common.Protocol;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mathieu
 */
public class Slave {

    private SynchronizedClock clock;

    private Slave() throws UnknownHostException, IOException {
        clock = new SynchronizedClock(Protocol.getMulticastAddress(), Protocol.MULTICAST_PORT);
    }

    private void startSynchronisation() {
        // Start the clock
        new Thread(clock).start();
    }

    public static void main(String... args) {

        try {

            Slave slave = new Slave();
            slave.startSynchronisation();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Slave.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Slave.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
