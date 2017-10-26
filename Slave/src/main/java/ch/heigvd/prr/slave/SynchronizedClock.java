/**
 *
 * @author Mathieu monteverde
 */
package ch.heigvd.prr.slave;

import ch.heigvd.prr.common.Protocol;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class SynchronizedClock implements Runnable {
    
    // Multicast socket pour synchroniser le temps
    private MulticastSocket socket;
    
    // Group à rejoindre
    private InetAddress group;
    
    // Temps synchronisé
    private long currentTime;
    
    // Objet chargé de synchroniser le délai
    private DelaySynchronizer delaySynchronizer;
    
    // SYNC ID reçu le plus récemment
    private int syncID;
    
    // Dernier Offset calculé
    private long offset;
    
    private boolean quitProcess = false;
    
    private InetAddress serverAddress;
    private int serverPort;
    
    /**
     * Etablit une connexion au groupe multicast fourni en paramètre
     * en utilisant le port fourni en paramètre.
     * @param address l'adresse multicast
     * @param port le port sur lequel établir la connexion
     * @throws IOException 
     */
    public SynchronizedClock(InetAddress address, int port) throws IOException {
      
      System.out.println("Creating socket...");
      socket = new MulticastSocket(port);
      
      System.out.println("Joining group " + address);
      group = address;
      socket.joinGroup(address);
      
      System.out.println("Set up completed");
    }
    
    /**
     * Retourne l'heure en millisecondes, synchronisé au mieux par le Precision
     * Time Protocol.
     * @return l'heure en ms 
     */
    public synchronized long getSynchronizedTime() {
        return offset + delaySynchronizer.getDelay();
    }
    
    public synchronized long getOffset() {
        return offset;
    }
    
    public synchronized void quitProcess() {
        quitProcess = true;
    }
    
    public synchronized boolean reuqestedQuitProcess() {
        return quitProcess;
    }

    @Override
    public void run() {
        
        try {
            
            // Loop to manage Master messages
            while (true) {
                // Récupère le message
               byte[] buf = new byte[256];
               DatagramPacket packet = new DatagramPacket(buf, buf.length);
               System.out.println("Waiting for UDP packet");
               socket.receive(packet);

               ByteBuffer buffer = ByteBuffer.wrap(packet.getData());
               
               switch (Protocol.getEnum(buffer.get(0))) {
                  case SYNC:
                        System.out.println("SYNC");
                        
                        // Récupère l'id
                        syncID = buffer.getInt(1);
                        
                        // Sauvegarde du temps local
                        currentTime = System.currentTimeMillis();
                     break;

                  case FOLLOW_UP:
                        System.out.println("FOLLOW_UP");

                        // Récupère l'id
                        int id = buffer.getInt(9);

                        if(id == this.syncID) {
                           // Récupère le temps master
                           long masterTime = buffer.getLong(1);
                           
                           // Calcul de l'écart
                           offset = masterTime - currentTime;
                           
                           // Démarre le thread
                           if(delaySynchronizer == null) {
                              serverAddress = packet.getAddress();
                              serverPort = packet.getPort();

                           }
                           
                        }
                     break;

                  default:
                     continue;
               }
               
               if (reuqestedQuitProcess()) {
                   break;
               }
            }
            
            // When the communication is over, leave the group and close the socket
            socket.leaveGroup(group);
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(SynchronizedClock.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public InetAddress getServerAddress() {
       return serverAddress;
    }
    
    public int getServerPort() {
       return serverPort;
    }
    
}
