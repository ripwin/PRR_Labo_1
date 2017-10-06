package ch.heigvd.prr.slave;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Slave implements Runnable {
   
   private Offset offset;

   public Slave(String address, int port) {
      try {
         offset = new Offset(address, port);
      } catch (IOException ex) {
         Logger.getLogger(Slave.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   @Override
   public void run() {
      offset.run();
   }
}
