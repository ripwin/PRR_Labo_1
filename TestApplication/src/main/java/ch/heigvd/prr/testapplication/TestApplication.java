/**
 * file:        TestApplication.java
 * created:     31.10.2017
 */
package ch.heigvd.prr.testapplication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The TestApplication class is a simple program that attemps to read time from
 * the Slave application using the TCP interface that it provides.
 *
 * It reads the time N times and displays the time that it received, then exits.
 * 
 * It display it using the rule : 
 * 
 * <system time> | <received time> | <system time> - <received time>
 */
public class TestApplication {

    public static void main(String args[]) {
        
        // Number of time to read the time
        final int N = 10;
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < N; ++i) {

                    try {
                        // Socket to get time
                        Socket socket = new Socket("localhost", 14000);
                        
                        // Get input / output streams
                        DataInputStream in = new DataInputStream(socket.getInputStream());
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        
                        // Save time as we would have used it if we needed it
                        long systemTime = System.currentTimeMillis();
                        
                        // Ask for time
                        out.writeByte(1);
                        long time = in.readLong();
                        
                        // Print differences
                        System.out.println(
                                systemTime + " | " + time + " | " + (systemTime - time)
                        );
                        
                        // Sleep for a while
                        Thread.sleep(1000);

                    } catch (IOException ex) {
                        Logger.getLogger(TestApplication.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TestApplication.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        }).start();

    }
}
