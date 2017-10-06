package ch.heigvd.prr.master;

import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Master implements Runnable {
   
   private Synchronization sync;
   
   public Master(int interval, String address, int port) {
      try {
         this.sync = new Synchronization(address, port, interval);
      } catch (UnknownHostException ex) {
         Logger.getLogger(Master.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   @Override
   public void run() {
      sync.run();
   }
}
