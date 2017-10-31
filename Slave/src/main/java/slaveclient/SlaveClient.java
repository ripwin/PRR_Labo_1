/**
 * file:        SlaveClient.java
 * created:     31.10.2017
 */

package slaveclient;

import ch.heigvd.prr.slave.Slave;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements a client TCP interface that other applications 
 * can use to ask the Slave application for the synchronized time.
 * 
 * This class opens a TCP socket on port 14000 on which other applications
 * can request time by sending a simple byte of value 1 (GET_TIME).
 * 
 * The client then responds with the synchronized time sending a long value, or 
 * -1 if the request was something else than 1 (GET_TIME).
 * 
 * The time that is sent back is based on the java method 
 * System.currentTimeMillis(). The resulting time is therefore the difference, 
 * measured in milliseconds, between the current time and midnight, 
 * January 1, 1970 UTC, with some added offset and delay, due to the 
 * synchronization with the Master application.
 */
public class SlaveClient implements Runnable {
    
    // Port on which to open the Slave client
    public final static int SLAVE_CLIENT_PORT = 14000;
    
    // Get time command
    public final static byte GET_TIME = 1;
    
    // ServerSocket to receive client connections
    private final ServerSocket server;
    
    // Parent slave to get time from
    private final Slave parent;
    
    /**
     * Constructor that creates the TCP ServerSocket.
     * @param parent the Slave parent instance to get time from
     * @throws IOException if an errors occurs opening the Socket
     */
    public SlaveClient(Slave parent) throws IOException {
        this.parent = parent;
        
        // Create the ServerSocket
        server = new ServerSocket(SLAVE_CLIENT_PORT);
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Accept client connections
                Socket socket = server.accept();
                
                System.out.println("Accepted new client connection...");
                
                // Get input and output stream to write simple data
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                
                // Read the request
                byte request = in.readByte();
                
                System.out.println("Request was: " + request);
                
                if (request == GET_TIME) {
                    // If they asked for time, send time
                    long time = parent.getSynchronizedTime();
                    System.out.println("TIME ============== " + time);
                    out.writeLong(time);
                } else {
                    // Else send error
                    out.writeLong(-1);
                }
                
                // Close the socket
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(SlaveClient.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
        }
    }

}
