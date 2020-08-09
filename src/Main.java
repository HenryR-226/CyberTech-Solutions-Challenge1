import java.math.BigInteger;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;        //64 bits and larger
import java.nio.ByteBuffer;

public class Main {
    static FileWriter outputWriter = null;
    static byte[] input1 = {'a', 'p', 'p', 'l', 'e'};
    static byte[] input2= {(byte) 0xCD, (byte) 0x01, (byte) 0xEF, (byte) 0xD7, (byte) 0x30};
    static boolean first = true;

    static long initialValue = 0x0000000012345678;
    public static void main(String[] args) throws IOException {
//        Challenge 1: LFSR
//        A common technique for obfuscating data is to use exclusive-or (XOR) with some key; it is inexpensive and symmetric. A problem occurs
//        when this is used on file formats like portable executable where there are many null bytes, since XORing nulls with the key ends up writing the key out.
//          A slightly more complex algorithm uses a Linear Feedback Shift Register (LFSR) to produce a key stream, which will be XORed with the data.
//
//        A generic LFSR is:
//        If F is the value which represents the LFSR feedback and S is the current state of the LFSR, the next state of the LFSR is computed as follows:
//        if the lowest bit of S is 0: S = S >> 1
//        if the lowest bit of S is 1: S = (S >> 1) ^ F
//        For this challenge, you'll be using an LFSR with the feedback value 0x87654321. The LFSR is initialized with a value and stepped to produce the key stream.
//        The next key value is read from the LFSR after eight steps, with the actual key being the lowest byte of the current LFSR value.
//        For example, if the initial value of the LFSR is 0xFFFFFFFF, then next value after stepping it eight times will be 0x9243F425, meaning that the first
//        key byte is 0x25. If the first byte of the input is 0x41, the first byte of output will be 0x64.
//
//                Your task is to implement this algorithm (both LFSR and the XOR). We're only interested in algorithm implementation; other code will be discarded.
//        The function should adhere to one of following signatures:
//
//        Java
//        static byte[] Crypt(byte[] data, long initialValue)

//        Example Tests:

//        data “apple”
//        dataLength
//        5
//        initialValue 0x12345678
//        result “\xCD\x01\xEF\xD7\x30”

//        data  “\xCD\x01\xEF\xD7\x30”
//        dataLength
//        5
//        initialValue 0x12345678
//        result “apple”
//        Submit: Source code containing your implementation of the LFSR based encoding routine

        File outputFile = null;
        outputWriter = null;
        try {
            outputFile = new File("BitshiftOutput.txt");
            if (outputFile.createNewFile()) {
                System.out.println("File created: " + outputFile.getName());
            } else {
                System.out.println("File already exists.");
            }
            outputWriter = new FileWriter("BitshiftOutput.txt");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        outputWriter.write("Starting LFSR Method:\n===================================\n");
        outputWriter.write("Params: {");
        StringBuilder sb = new StringBuilder();
        for (byte b : input1) sb.append(String.format("%02X ", b));
        sb.append("}, initialValue: " + String.format("%02X", initialValue) + ", feedbackValue: " + String.format("%02X", feedback));
        outputWriter.write(sb.toString() + "\n\n\n");
        byte[] return1 = Crypt(input1, initialValue);
        for (int ctr = 0; ctr < input1.length; ctr++){
            System.out.print("Byte XOR at position " + ctr + ", " + String.format("%02X", input1[ctr]) + " . " +
                    String.format("%02X", return1[ctr]) + " ;; ");
            input1[ctr] = (byte) (input1[ctr] ^ return1[ctr]);
            outputWriter.write("0x" + String.format("%02X", input1[ctr]) + " ");
        }
        outputWriter.write("Input: 'apple', 0x12345678. Expected output:  “\\xCD\\x01\\xEF\\xD7\\x30” ");
        System.out.print("Actual output: { ");
        sb = new StringBuilder();
        for (byte b : return1) sb.append(String.format("%02X ", b));
        outputWriter.write(sb.toString() + "}");

        outputWriter.write("\n\n============================================\n\nSecond run through:\n\n");

        outputWriter.write("Params: {");
        sb = new StringBuilder();
        for (byte b : input2) sb.append(String.format("%02X ", b));
        sb.append("}, initialValue: " + String.format("%02X", initialValue) + ", feedbackValue: " + String.format("%02X", feedback));
        outputWriter.write(sb.toString() + "\n\n\n");

        byte[] return2 = Crypt(input2, initialValue);

        for (int ctr = 0; ctr < input2.length; ctr++){
            System.out.print("Byte XOR at position " + ctr + ", " + String.format("%02X", input1[ctr]) + " . " +
                    String.format("%02X", return2[ctr]) + " ;; ");
            input1[ctr] = (byte) (input2[ctr] ^ return2[ctr]);
            outputWriter.write(String.format("0x" + "%02X", input2[ctr]) + " ");
        }
        outputWriter.write("Input: “\\xCD\\x01\\xEF\\xD7\\x30”, 0x12345678. Expected output: 'apple' (0x61, 0x70, 0x70, 0x6C, 0x65)\n");
        System.out.print("Actual output: { ");
        sb = new StringBuilder();
            for (byte b : return2) sb.append("0x" + String.format("%02X", b) + " ");
        outputWriter.write(sb.toString() + "}");
        outputWriter.close();
    }

    /**
     *  LFSR implementation for CyberTech Solutions.
     *  Goes through each byte of data in input array, use bitwise exclusive or ( ^ operator) to hash data to and from
     *  garbled state.
     *
     * @param data, array of raw bytes to process
     * @param initialValue, initial 64 bit data word to process
     * @return data, the processed data from the LFSR crypt
     */
    static final long feedback = 0x87654321;
    static final byte one = 0x1;
    static final byte zero = 0x0;
    static byte[] Crypt(byte[] data, long initialValue) throws IOException {
        byte[] keyStream = new byte[data.length];         //Temporary value to make only one array access
        int keyCounter = 0;
        do {
            for (int ctr = 0; ctr < 8; ctr++) {
                if ((initialValue & (1L)) == 0)             //lowest bit of data was 0
                {
                    String tmp = String.format("%032d", new BigInteger(Long.toBinaryString((long)initialValue)));
                    initialValue = (initialValue >> 1);
                    outputWriter.write(tmp + "\nCtr: " + ctr + " , Bit Zero. New value : 0x" + Long.toHexString(initialValue) + "\n\n");
                } else {                                    //lowest bit of data was 1
                    String tmp = String.format("%032d", new BigInteger(Long.toBinaryString((long)initialValue)));
                    initialValue = ((initialValue >> 1) ^ feedback);
                    outputWriter.write(tmp + "\nCtr: " + ctr + " , Bit One. New value : 0x" + Long.toHexString(initialValue) + "\n\n");
                }
            } //8th value reached, peel off last byte to store as key
            keyStream[keyCounter] = (byte)(initialValue & (byte) 0xFF);     //AND with 0xFF to get lowest byte
            outputWriter.write("8th value reached. Key stored in position " +
                    keyCounter + ": " + String.format("%02X", keyStream[keyCounter]) + "\n");
            outputWriter.write("Init value key gotten from: " + String.format("%02X", initialValue));
            if (first) outputWriter.write("\nValue when XOR'd with value of data array: " + String.format("%02X", keyStream[keyCounter] ^ input1[keyCounter]));
            else outputWriter.write("\nValue when XOR'd with value of data array: " + String.format("%02X", keyStream[keyCounter] ^ input2[keyCounter]));
            outputWriter.write("\n\n===================================================\n\n");
            keyCounter++;
        } while (keyCounter < data.length);
        first = false;
        return keyStream;
    }
//    static byte peelOffLastByte(long input) {
//
//    }
}

/**
 * Utility class, stores a long as 8 byte array with helper methods to make implementation simpler
 * Constructor takes long and creates byte[8]
 * toString generates string of "0xB1 0xB2 0xB3 ..." etc
 * getLowest returns lowest byte
 * XOR computes XOR of entire array to long, or one byte to the lowest
 */
class byteArray {
    byte[] me = new byte[8];
    static StringBuilder builder = new StringBuilder();

    /**
     * Constructor, converts long to byte array of 8
     * @param input, long (64 bit) to convert to 8 bytes
     */
    byteArray(long input){
        for (int ctr = 0; ctr <8; ctr++) {
            me[ctr] = (byte) (input >> ((64 - ctr+1)*8) & 0xff);    //Shift input over by 64 - (ctr +1) * 8. eg: at 1, this is 56
        }
    }
    //Utility for getting lowest byte
    byte getLowest(){
        return me[0];
    }
    //Does last bit end with 1?
    boolean endsWithOne(){
        return ((getLowest() & 1L) != 0);
    }

    @Override
    public String toString() {
        for (byte b : me)
            builder.append("0x" + String.format("%02X",b) + ", ");
        return builder.toString();
    }
    //Utility to take XOR conveniently of entire array with given argument array
    public byte[] XOR(byte[] arg2) throws ArrayIndexOutOfBoundsException {
        if (arg2.length!=me.length) throw new ArrayIndexOutOfBoundsException("XOR called on arrays with Diff Length");
        byte[] returnVal = new byte[arg2.length];
        for (int ctr = 0; ctr<arg2.length; ctr++){
            returnVal[ctr] = (byte) (me[ctr] ^ arg2[ctr]);
        }
        return returnVal;
    }
    //Take input byte and XOR it with own last byte
    public byte XOR (byte arg2) {
        return (byte) (getLowest() ^ arg2);
    }
}