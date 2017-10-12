package ch.heigvd.prr.master;

import ch.heigvd.prr.common.Protocol;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
   public static void main(String args[]) {
      try {
         Thread sync = new Thread(
            new Synchronization(
               Protocol.getMulticastAddress(), 
               Protocol.MULTICAST_PORT, 
               4000)
         );
         
         Thread delay = new Thread(new Lol(Protocol.DELAY_COMMUNICATION_PORT));
         
         sync.start();
         delay.start();
         
         sync.join();
         delay.join();
         
         
      } catch (UnknownHostException ex) {
         Logger.getLogger(
            Main.class.getName()).log(Level.SEVERE, null, ex
         );
      } catch (InterruptedException ex) {
         Logger.getLogger(
            Main.class.getName()).log(Level.SEVERE, null, ex
         );
      }
   }
}
