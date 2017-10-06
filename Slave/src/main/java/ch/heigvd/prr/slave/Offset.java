package ch.heigvd.prr.slave;

import ch.heigvd.prr.common.Protocol;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Offset implements Runnable {
   
   private int id;
   private long synchronizedTime;

   private long offset;
   
   private MulticastSocket socket;
   private InetAddress group;

   public Offset(String address, int port) throws IOException {
      System.setProperty("java.net.preferIPv4Stack", "true");
      
      System.out.println("Creating socket...");
      socket = new MulticastSocket(port);
      
      System.out.println("Joining group " + address);
      group = InetAddress.getByName(address);
      socket.joinGroup(group);
      
      System.out.println("Set up completed");
   }
   
   @Override
   public void run() {
      try {
         while (true) {
            try {
               
               // Récupère le message
               byte[] buf = new byte[256];
               DatagramPacket packet = new DatagramPacket(buf, buf.length);
               System.out.println("Waiting for UDP packet");
               socket.receive(packet);

               ByteBuffer buffer = ByteBuffer.wrap(packet.getData());
               
               switch (Protocol.getEnum(buffer.get(0))) {
                  case SYNC:
                        System.out.println("SYNC");
                        
                        // Récupère l'id
                        id = buffer.getInt(1);
                        
                        // Sauvegarde du temps local
                        synchronizedTime = System.currentTimeMillis();
                     break;

                  case FOLLOW_UP:
                        System.out.println("FOLLOW_UP");

                        // Récupère l'id
                        int id = buffer.getInt(9);

                        if(id == this.id) {
                           // Récupère le temps master
                           long masterTime = buffer.getLong(1);
                           
                           // Calcul de l'écart
                           offset = masterTime - synchronizedTime;
                           
                           // Démarre le thread
                        }
                     break;

                  default:
                     System.out.println("HAHAHAHAHAHA");
                     // TODO : Exception
                     break;
               }

               String received = new String(packet.getData());
               System.out.println("Data received: " + received);
            } catch (IOException ex) {
               Logger.getLogger(Slave.class.getName()).log(Level.SEVERE, null, ex);
               break;
            }
         }

         socket.leaveGroup(group);
         socket.close();
      } catch (IOException ex) {
         Logger.getLogger(Slave.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
}
