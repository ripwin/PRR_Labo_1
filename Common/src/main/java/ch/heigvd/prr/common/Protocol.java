package ch.heigvd.prr.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Protocol {

   public enum Code {
      SYNC, FOLLOW_UP, DELAY_REQUEST, DELAY_RESPONSE
   };
   
   // Le port sur lequel joindre le groupe multicast
   public static final int MULTICAST_PORT = 12000;
   
   // Le port sur lequel effectuer les requêtes de delai
   public static final int DELAY_COMMUNICATION_PORT = 13000;
   

   public static byte getByte(Protocol.Code code) {
      return (byte) code.ordinal();
   }

   public static Code getEnum(byte byteCode) {
      if (byteCode >= Code.values().length) {
         throw new IllegalArgumentException();
      }

      return Code.values()[byteCode];
   }
   
   public static InetAddress getMulticastAddress() throws UnknownHostException {
       System.setProperty("java.net.preferIPv4Stack", "true");
       return InetAddress.getByName("239.0.0.1");
   }
}
