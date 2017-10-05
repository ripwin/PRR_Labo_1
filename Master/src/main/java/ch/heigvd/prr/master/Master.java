package ch.heigvd.prr.master;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Master implements Runnable {
   
   private final int interval;
   private final String address;
   private final int port;
   
   private boolean running;
   
   public Master(int interval, String address, int port) {
      this.interval = interval;
      this.address = address;
      this.port = port;
   }

   @Override
   public void run() {
      
      running = true;
      
      try (
         MulticastSocket socket = new MulticastSocket(12000)) 
      {
         

         byte[] tampon = new byte[256];
         tampon = "Allooooooo".getBytes();
         
         
         InetAddress group = InetAddress.getByName(address);
         
         
         // Paquet à envoyer
         DatagramPacket packet = new DatagramPacket(
            tampon, 
            tampon.length, 
            group, 
            port
         );

         while(running) {
            
            socket.send(packet);
            System.out.println("Envoie du message" + packet.getData().length);
            
            
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
