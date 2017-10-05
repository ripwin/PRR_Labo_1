package ch.heigvd.prr.master;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.heigvd.prr.common.Protocol;

public class Synchronization implements Runnable {
   
   private final String address;
   private final int port;
   private final int interval;
   
   private int id;
   
   private boolean running;
   
   public Synchronization(String address, int port, int interval) {
      this.address = address;
      this.port = port;
      this.interval = interval;
      
      this.id = 0;
   }
   
   @Override
   public void run() {
      
      running = true;
      
      try (
         MulticastSocket socket = new MulticastSocket()) 
      {
         InetAddress group = InetAddress.getByName(address);
         
         
         while(running) {
            
            // Incrémente l'identifiant
            this.id++;
            long time;
         
            // SYNC
            {
               // Message SYNC
               ByteBuffer buffer = ByteBuffer.allocate(32);
               buffer.put(Protocol.getByte(Protocol.Code.SYNC));
               buffer.putInt(id);

               byte[] data = buffer.array();

               // Création du paquet
               DatagramPacket packet = new DatagramPacket(
                  data, 
                  data.length, 
                  group, 
                  port
               );

               socket.send(packet);
               time = System.currentTimeMillis();
               System.out.println("Envoie du SYNC");
            }
            
            // FOLLOW_UP
            {
               // Message FOLLOW_UP
               ByteBuffer buffer = ByteBuffer.allocate(32);
               buffer.put(Protocol.getByte(Protocol.Code.FOLLOW_UP));
               buffer.putLong(time);
               buffer.putInt(id);

               byte[] data = buffer.array();
               
               System.out.println(buffer.get(0));

               // Création du paquet
               DatagramPacket packet = new DatagramPacket(
                  data, 
                  data.length, 
                  group, 
                  port
               );

               socket.send(packet);
               System.out.println("Envoie du FOLLOW_UP");
            }
            
            // Intervale entre les envoies
            try {
               Thread.sleep(interval);
            } catch (InterruptedException ex) {
               Logger.getLogger(Master.class.getName()).log(Level.SEVERE, null, ex);
            }
         }
         
      } catch (IOException ex) {
         Logger.getLogger(Master.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
}
