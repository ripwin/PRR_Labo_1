package ch.heigvd.prr.master;

public class Main {
   public static void main(String args[]) {
      Master master = new Master(4000, "239.0.0.1", 12000);
      master.run();
   }
}
