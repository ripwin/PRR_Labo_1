/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.prr.slave;

/**
 *
 * @author mathieu
 */
public class DelaySynchronizer implements Runnable {
    
    // Delay to communicate with the master
    private long delay;
    
    public synchronized long getDelay() {
        return delay;
    }
    
    private synchronized void setDelay(long delay) {
        this.delay = delay;
    }

    @Override
    public void run() {
        
    }
    
}
