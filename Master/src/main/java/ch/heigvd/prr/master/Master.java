package ch.heigvd.prr.master;

public class Master implements Runnable {
   
   private Synchronization sync;
   
   public Master(int interval, String address, int port) {
      this.sync = new Synchronization(address, port, interval);
   }

   @Override
   public void run() {
      sync.run();
   }
}
