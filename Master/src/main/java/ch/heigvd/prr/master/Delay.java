/**
 * file:        Delay.java
 * created:     31.10.2017
 */

package ch.heigvd.prr.master;

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
 * <h1>Delay (Master)</h1>
 *  
 * This is the second part of the PTP protocal. It allows to determine the 
 * delay transmission between the master and the salve.
 * 
 * This class waits a DELAY_REQUEST request from the salve then responds with a 
 * DELAY_RESPONSE than contains the time when it received the DELAY_REQUEST 
 * request. 
 */
public class Delay implements Runnable {
   
   private final int port;
   
   /**
    * Default constructor
    * 
    * @param port The port to listen
    */
   public Delay(int port) {
      this.port = port;
   }
   
   @Override
   public void run() {
      try (DatagramSocket socket = new DatagramSocket(port)) {

         // Sender info
         int id;
         int port;
         InetAddress address;
         
         while(true) {
            
            // Waiting for delay request
            {
               byte[] buf = new byte[256];
               DatagramPacket packet = new DatagramPacket(buf, buf.length);
               socket.receive(packet);

               // Get sender info
               address = packet.getAddress();
               port = packet.getPort();

               ByteBuffer buffer = ByteBuffer.wrap(packet.getData());
               
               if(Protocol.getEnum(buffer.get(0)) != 
                  Protocol.Code.DELAY_REQUEST) 
               {
                  continue;
               }
               
               id = buffer.getInt(1);
            }
            
            
            // Send the delay
            {
               ByteBuffer buffer = ByteBuffer.allocate(32);
               buffer.put(Protocol.getByte(Protocol.Code.DELAY_RESPONSE));
               buffer.putInt(id);
               buffer.putLong(System.currentTimeMillis());

               byte[] data = buffer.array();

               // Creates a packet
               DatagramPacket packet = new DatagramPacket(
                  data, 
                  data.length, 
                  address, 
                  port
               );
               
               socket.send(packet);
            }
         }     
         
      } catch (SocketException ex) {
         Logger.getLogger(Delay.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
         Logger.getLogger(Delay.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
}
