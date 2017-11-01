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

public class Lol implements Runnable {
   
   private int port;
   
   public Lol(int port) {
      this.port = port;
   }
   
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
               System.out.println("Waiting for UDP packet");
               socket.receive(packet);

               // Get sender info
               address = packet.getAddress();
               port = packet.getPort();

               ByteBuffer buffer = ByteBuffer.wrap(packet.getData());
               
               if(Protocol.getEnum(buffer.get(0)) != Protocol.Code.DELAY_REQUEST) {
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
               System.out.println("Envoie du DELAY_RESPONSE a " + address + " " + port);
            }
         }     
         
      } catch (SocketException ex) {
         Logger.getLogger(Lol.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
         Logger.getLogger(Lol.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
}
