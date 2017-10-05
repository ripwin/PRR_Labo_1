package ch.heigvd.prr.slave;

import ch.heigvd.prr.common.Protocol;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mathieu
 */
public class Slave implements Runnable {

   private long systemTime;
   private long offset;
   private long delay;
   private long synchronizedTime;

   private MulticastSocket socket;
   private InetAddress group;

   public Slave(String IPGroup) throws IOException {
      System.setProperty("java.net.preferIPv4Stack", "true");
      System.out.println("Creating socket...");
      socket = new MulticastSocket(12000);
      System.out.println("Joining group " + IPGroup);
      group = InetAddress.getByName(IPGroup);
      socket.joinGroup(group);
      System.out.println("Set up completed");
   }

   @Override
   public void run() {

      try {
         DatagramPacket packet;
         while (true) {
            try {
               byte[] buf = new byte[256];
               packet = new DatagramPacket(buf, buf.length);
               System.out.println("Waiting for UDP packet");
               socket.receive(packet);

               /*
                        TODO
                        Si on reçoit un SYNC enregistrer une pair 
                        entre l'heure de réception, et l'ID du message. 
                        
                        Si c'est la première fois, enregistrer l'adresse IP et le
                        port du Master. Commencer également le thread qui se 
                        chargera de calculer le delay.
                    
                        
                        Si on reçoit un Follow up, Calculer l'écart entre 
                        notre réception du SYNC et l'heure fournie par le 
                        Master. Mettre à jour notre écart.
                */
               byte[] byteCode = Arrays.copyOfRange(packet.getData(), 0, 2);
               switch (Protocol.getEnum(byteCode)) {
                  case SYNC:

                     break;

                  case FOLLOW_UP:

                     break;

                  default:

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
