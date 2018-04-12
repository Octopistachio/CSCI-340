/*
 * ReadBytes.java
 *
 * This progeram illustrates reading bytes from a file.
 * In our world, all files are made up of bytes.  Any interpretation of
 * these bytes to be ints, chars, Strings, or other more advanced data
 * types is strictly up to the program.
 *
 * @author  Stuart Hansen
 * @version February 2106
 */

import java.io.*; 

public class ReadBytes {
    
    public static void main(String[] args) {

       try {
           RandomAccessFile file = new RandomAccessFile("binaryfile.bin", "r");
           
           while (true)
           {
               int i = (file.readByte());

               // bytes ahve the range -128-127, so we convert it to be
               // positive and then write out its integer and character values.
               // 128 - 255.               
               if (i < 0) i += 256;

               System.out.print ("Integer Value = " + i + " ");
               System.out.println ("Character Value = '" + (char) i + "'");
           }
       }
       catch (EOFException eof)
       {
           System.out.println ("End of File reached.");
       }
       catch (Exception e)
       {
           System.out.println("An Exception occurred. " + e);
       }
    }
    
}
