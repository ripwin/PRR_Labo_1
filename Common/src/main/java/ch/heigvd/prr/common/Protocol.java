package ch.heigvd.prr.common;

class Protocol {
   public enum Code { SYNC, FOLLOW_UP, DELAY_REQUEST, DELAY_RESPONSE };
   
   public static byte[] getByte(Protocol.Code code) {
      
      byte[] tampon = new byte[2];
      
      switch(code) {
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
}

