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
    
    private DatagramSocket socket;
    
    // L'adresse IP du master
    private InetAddress masterIPAddress;
    
    private int masterPort;
    
    // Le dernier délai calculé pour communiquer avec le master
    private long delay;
    
    // l'ID de la dernière requête d'ID envoyée
    private int delayID;
    
    public DelaySynchronizer(InetAddress masterIPAddress, int port) throws SocketException {
        this.masterIPAddress = masterIPAddress;
        this.masterPort = port;
        
        socket = new DatagramSocket(port);
    }
    
    public synchronized long getDelay() {
        return delay;
    }
    
    private synchronized void setDelay(long delay) {
        this.delay = delay;
    }

    @Override
    public void run() {
        while (true) {
            
            {
                try {
                    // Send SYNC
                    ByteBuffer buffer = ByteBuffer.allocate(32);
                    buffer.put(Protocol.getByte(Protocol.Code.DELAY_REQUEST));
                    buffer.putInt(this.delayID++);
                    
                    byte[] data = buffer.array();
                    
                    // Creates a packet
                    DatagramPacket packet = new DatagramPacket(
                            data,
                            data.length,
                            masterIPAddress,
                            masterPort
                    );
                    socket.send(packet);
                } catch (IOException ex) {
                    Logger.getLogger(DelaySynchronizer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            {
                try {

                    // Attendre la réponse du serveur
                    byte[] buf = new byte[32];
                    DatagramPacket packet = new DatagramPacket(buf, delayID);

                    socket.receive(packet);

                    ByteBuffer buffer = ByteBuffer.wrap(packet.getData());


                } catch (IOException ex) {
                    Logger.getLogger(DelaySynchronizer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
}
