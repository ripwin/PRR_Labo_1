/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.prr.slave;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mathieu
 */
public class Slave implements Runnable {
    
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
