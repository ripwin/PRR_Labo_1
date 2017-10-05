package ch.heigvd.prr.common;

public class Protocol {

   public enum Code {
      SYNC, FOLLOW_UP, DELAY_REQUEST, DELAY_RESPONSE
   };

   public static byte getByte(Protocol.Code code) {
      return (byte) code.ordinal();
   }

   public static Code getEnum(byte byteCode) {
      if (byteCode >= Code.values().length) {
         throw new IllegalArgumentException();
      }

      return Code.values()[byteCode];
   }
}
