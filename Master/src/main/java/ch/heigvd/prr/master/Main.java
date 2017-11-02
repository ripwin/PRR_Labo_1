/**
 * file:        Main.java
 * created:     31.10.2017
 */

package ch.heigvd.prr.master;

import ch.heigvd.prr.common.Protocol;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the master of the PTP Protocal.
 * 
 * It contains two classes : Synchronization and Delay
 * 
 * Synchronization sends every k seconds a Sync and Follow up request to the
 * slave. (Use multicast) It allows to calculate the offset between the master 
 * and the slave.
 * 
 * Delay waits a DELAY_REQUEST from the slave and sends a DELAY_RESPONSE 
 * (use unicast UDP) with the time when a DELAY_REQUEST arrived.
 * 
 * Those classes are launched in two different threads.
 */
public class Main {
   public static void main(String args[]) {
      try {
         Thread sync = new Thread(
            new Synchronization(
               Protocol.getMulticastAddress(), 
               Protocol.MULTICAST_PORT, 
               Protocol.INTERVAL_SYNC
            )
         );
         
         Thread delay = new Thread(
            new Delay(Protocol.DELAY_COMMUNICATION_PORT)
         );
         
         sync.start();
         delay.start();
         
         sync.join();
         delay.join();
      } catch (UnknownHostException | InterruptedException ex) {
         Logger.getLogger(
            Main.class.getName()).log(Level.SEVERE, null, ex
         );
      }
   }
}
