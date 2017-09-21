package master;

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
         DatagramSocket socket = new DatagramSocket(12000)) 
      {
         
         
         String message = "Allooooooo";
         byte[] tampon = new byte[256];
         
         InetAddress group = InetAddress.getByName("239.0.0.1");
         
         
         tampon = message.getBytes();
         DatagramPacket packet = new DatagramPacket(tampon, tampon.length, group, 12000);

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
