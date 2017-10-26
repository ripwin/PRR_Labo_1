/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.prr.slave;

import ch.heigvd.prr.common.Protocol;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mathieu
 */
public class DelaySynchronizer implements Runnable {
    
    // Socket pour envoyer les paquets UDP
    private final DatagramSocket socket;
    
    // L'adresse IP du master
    private final InetAddress masterIPAddress;
    
    // Le port sur lequel envoyer les paquets
    private final int masterPort;
    
    // Synchronized clock to get the current offset
    private SynchronizedClock parentClock;
    
    // Temps de la dernière DELAY_REQUEST
    private long lastDelayRequestTime;
    
    // Le dernier délai calculé pour communiquer avec le master
    private long delay;
    
    // l'ID de la dernière requête d'ID envoyée
    private int delayID;
    
    public DelaySynchronizer(InetAddress masterIPAddress, int port, SynchronizedClock parentClock) throws SocketException {
        this.masterIPAddress = masterIPAddress;
        this.masterPort = port;
        
        socket = new DatagramSocket(port);
        
        this.parentClock = parentClock;
    }
    
    public synchronized long getDelay() {
        return delay;
    }
    
    private synchronized void setDelay(long delay) {
        this.delay = delay;
    }
    
    public static long getRandomWaitingTime() {
        int range = (Protocol.INTERVAL_DELAY_MAX - Protocol.INTERVAL_DELAY_MIN) + 1;
        return (long)(Math.random() * range) + Protocol.INTERVAL_DELAY_MIN;
    }

    @Override
    public void run() {
        while (true) {
            
            {
                try {
                    // Créer le contenu de la requête DELAY_REQUEST
                    ByteBuffer buffer = ByteBuffer.allocate(32);
                    buffer.put(Protocol.getByte(Protocol.Code.DELAY_REQUEST));
                    buffer.putInt(this.delayID++);
                    
                    byte[] data = buffer.array();
                    
                    // Créer un paquet UDP
                    DatagramPacket packet = new DatagramPacket(
                            data,
                            data.length,
                            masterIPAddress,
                            masterPort
                    );
                    
                    // Envoyer la requête
                    socket.send(packet);
                    
                    // Enregistrer le moment de l'envoi
                    lastDelayRequestTime = System.currentTimeMillis() + parentClock.getOffset();
                    
                } catch (IOException ex) {
                    Logger.getLogger(DelaySynchronizer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            {
                try {

                    // Préparer le container la réponse du serveur
                    byte[] buf = new byte[32];
                    DatagramPacket packet = new DatagramPacket(buf, delayID);
                    
                    // Attendre la réponse DELAY_RESPONSE
                    socket.receive(packet);

                    ByteBuffer buffer = ByteBuffer.wrap(packet.getData());
                    
                    // Si la réponse du serveur correspond à un DELAY_RESPONSE
                    if (Protocol.getEnum(buffer.get(0)) == Protocol.Code.DELAY_RESPONSE) {
                        int id = buffer.getInt(1);
                        
                        // Si l'ID de la réponse correspond au dernier DELAY_REQUEST
                        if (id == delayID) {
                            long masterTime = buffer.getLong(5);
                            
                            setDelay((masterTime - lastDelayRequestTime) / 2);
                        }
                    }
                    
                    // Attendre un temps aléatoire 
                    Thread.sleep(getRandomWaitingTime());
                    
                } catch (IOException ex) {
                    Logger.getLogger(DelaySynchronizer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DelaySynchronizer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
}
