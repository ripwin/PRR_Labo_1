package ch.heigvd.prr.common;

public class Protocol {

   public enum Code {
      SYNC, FOLLOW_UP, DELAY_REQUEST, DELAY_RESPONSE
   };

   public static byte[] getByte(Protocol.Code code) {

      byte[] tampon = new byte[2];

      switch (code) {
         case SYNC:
            tampon[0] = 0;
            tampon[1] = 0;
            break;

         case FOLLOW_UP:
            tampon[0] = 0;
            tampon[1] = 1;
            break;

         case DELAY_REQUEST:
            tampon[0] = 1;
            tampon[1] = 0;
            break;

         case DELAY_RESPONSE:
            tampon[0] = 1;
            tampon[1] = 1;
            break;
      }

      return tampon;
   }

   public static Code getEnum(byte[] byteCode) {
      
      if (byteCode.length != 2) {
         throw new IllegalArgumentException();
      }
      
      Code code;

      if (byteCode[0] == 0 && byteCode[1] == 0) {
         code = Code.SYNC;
      } else if (byteCode[0] == 0 && byteCode[1] == 1) {
         code = Code.FOLLOW_UP;
      } else if (byteCode[0] == 1 && byteCode[1] == 0) {
         code = Code.DELAY_REQUEST;
      } else {
         code = Code.DELAY_RESPONSE;
      }

      return code;
   }
}
