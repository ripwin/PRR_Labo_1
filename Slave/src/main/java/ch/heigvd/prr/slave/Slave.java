/**
 * file:        Slave.java
 * created:     5.10.2017
 */

package ch.heigvd.prr.slave;

import ch.heigvd.prr.common.Protocol;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import slaveclient.SlaveClient;

/**
 * The Slave class is the Slave application main class which purpose
 * is to provide a clock that approximately synchronize itself to a Master 
 * application using the Precision Time Protocol. 
 * 
 * The application uses two classes to accomplish this work. 
 * 
 * A SynchronizedClock instance, which goal is to synchronize with the Master
 * and a SlaveClient instance, which purpose is to offer an entry point for
 * other application to query the synchronized time. 
 * Both are run in seperate threads.
 */
public class Slave {
    
    // The SynchronizedClock instance that will synchronize itself with the Master
    private SynchronizedClock clock;
    
    // The SlaveClient to communicate with other applications
    private SlaveClient client;
    
    /**
     * Constructor that instanciate a new SynchronizedClock and a new SlaveClient
     * @throws UnknownHostException
     * @throws IOException 
     */
    private Slave() throws UnknownHostException, IOException {
        clock = new SynchronizedClock(Protocol.getMulticastAddress(), Protocol.MULTICAST_PORT);
        client = new SlaveClient(this);
    }
    
    /**
     * This method will run the clock and client in two separate threads.
     */
    private void startSynchronization() {
        // Start the clock
        new Thread(clock).start();
        new Thread(client).start();
    }
    
    /**
     * Get the time synchronized with the Master application
     * @return 
     */
    public long getSynchronizedTime() {
        return clock.getSynchronizedTime();
    }
    
    /**
     * Main Slave application program. Creates a new Slave instance and starts
     * the synchronization.
     * @param args 
     */
    public static void main(String... args) {
        try {
            Slave slave = new Slave();
            slave.startSynchronization();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Slave.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Slave.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
