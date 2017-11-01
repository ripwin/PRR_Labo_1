package ch.heigvd.prr.master;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.heigvd.prr.common.Protocol;
import java.net.UnknownHostException;


/**
 * <h1>Synchronization (Master)</h1>
 * 
 * This is the first part of the PTP protocol. This class sends from the 
 * master to the Slave Sync and Follow up request every k seconds. It allows 
 * to determine the offset between the master and the slave.
 */
public class Synchronization implements Runnable {

   private final InetAddress group;
   private final int port;
   private final int interval;
   
   private int id;

   /**
    * Default constructor
    * 
    * @param address The multicast address where to send synchronization
    * @param port The port to use to communicate with the slaves
    * @param interval The interval between each sychronization
    * @throws UnknownHostException 
    */
   public Synchronization(InetAddress address, int port, int interval)
      throws UnknownHostException 
   {
      this.group = address;
      this.port = port;
      this.interval = interval;

      this.id = 0;
   }

   @Override
   public void run() {
      try (MulticastSocket socket = new MulticastSocket()) 
      {
         // Main loop
         while (true) {

            // Increment id
            this.id++;
            long time;

            // SYNC REQUEST
            {
               // Send SYNC
               ByteBuffer buffer = ByteBuffer.allocate(32);
               buffer.put(Protocol.getByte(Protocol.Code.SYNC));
               buffer.putInt(this.id);

               byte[] data = buffer.array();

               // Creates a packet
               DatagramPacket packet = new DatagramPacket(
                       data,
                       data.length,
                       group,
                       port
               );

               socket.send(packet);
               time = System.currentTimeMillis();
            }

            // FOLLOW_UP REQUEST
            {
               // Send FOLLOW_UP
               ByteBuffer buffer = ByteBuffer.allocate(32);
               buffer.put(Protocol.getByte(Protocol.Code.FOLLOW_UP));
               buffer.putLong(time);
               buffer.putInt(this.id);

               byte[] data = buffer.array();

               // Creates a packet
               DatagramPacket packet = new DatagramPacket(
                       data,
                       data.length,
                       group,
                       port
               );

               socket.send(packet);
            }

            // Time between each sychronization
            try {
               Thread.sleep(interval);
            } catch (InterruptedException ex) {
               Logger.getLogger(
                  Synchronization.class.getName()).log(Level.SEVERE, null, ex
               );
            }
         }
      } catch (IOException ex) {
         Logger.getLogger(
            Synchronization.class.getName()).log(Level.SEVERE, null, ex
         );
      }
   }
}
