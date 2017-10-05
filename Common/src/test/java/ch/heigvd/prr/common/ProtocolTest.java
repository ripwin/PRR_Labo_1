/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.prr.common;

import java.util.Arrays;
import junit.framework.TestCase;

/**
 *
 * @author mathieu
 */
public class ProtocolTest extends TestCase {
   
   public ProtocolTest(String testName) {
      super(testName);
   }
   
   @Override
   protected void setUp() throws Exception {
      super.setUp();
   }
   
   @Override
   protected void tearDown() throws Exception {
      super.tearDown();
   }

   /**
    * Test of getByte method, of class Protocol.
    */
   public void testGetByte() {
      byte[] expResult = new byte[2];
      byte[] result = null;
      
      System.out.println("Protocol.getByte()...");
      
      // Test SYNC
      expResult = new byte[]{0, 0};
      result = Protocol.getByte(Protocol.Code.SYNC);
      assertTrue(Arrays.equals(expResult, result));
      
      // Test FOLLOW_UP
      expResult = new byte[]{0, 1};
      result = Protocol.getByte(Protocol.Code.FOLLOW_UP);
      assertTrue(Arrays.equals(expResult, result));
      
      // Test DELAY_REQUEST
      expResult = new byte[]{1, 0};
      result = Protocol.getByte(Protocol.Code.DELAY_REQUEST);
      assertTrue(Arrays.equals(expResult, result));
      
      // Test DELAY_RESPONSE
      expResult = new byte[]{1, 1};
      result = Protocol.getByte(Protocol.Code.DELAY_RESPONSE);
      assertTrue(Arrays.equals(expResult, result));
   }

   /**
    * Test of getEnum method, of class Protocol.
    */
   public void testGetEnum() {
      Protocol.Code result = null;
      Protocol.Code expCode = null;
      
      System.out.println("Protocol.getEnum()...");
      
      // Test SYNC
      expCode = Protocol.Code.SYNC;
      result = Protocol.getEnum(new byte[]{0, 0});
      assertEquals(expCode, result);
      
      // Test FOLLOW_UP
      expCode = Protocol.Code.FOLLOW_UP;
      result = Protocol.getEnum(new byte[]{0, 1});
      assertEquals(expCode, result);
      
      // Test FOLLOW_UP
      expCode = Protocol.Code.DELAY_REQUEST;
      result = Protocol.getEnum(new byte[]{1, 0});
      assertEquals(expCode, result);
      
      // Test FOLLOW_UP
      expCode = Protocol.Code.DELAY_RESPONSE;
      result = Protocol.getEnum(new byte[]{1, 1});
      assertEquals(expCode, result);
      
      try {
         Protocol.getEnum(new byte[3]);
         fail("The method should throw an exception.");
      } catch (IllegalArgumentException e) {
         
      }
   }
   
}
