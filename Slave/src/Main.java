
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import slave.Slave;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mathieu
 */
public class Main {
    public static void main(String[] args) {
        try {
            Slave slave = new Slave("239.0.0.1");
            slave.run();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
