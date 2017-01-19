package bgu.spl171.net.impl.TFTP;

import bgu.spl171.net.api.MessageEncoderDecoder;
import bgu.spl171.net.impl.TFTP.msg.DataMessage;
import bgu.spl171.net.impl.TFTP.msg.Message;
import bgu.spl171.net.impl.TFTP.msg.DeleteFile;
import bgu.spl171.net.impl.TFTP.msg.Login;
import bgu.spl171.net.impl.TFTP.msg.ReadWrite;
import bgu.spl171.net.impl.TFTP.msg.DirRequest;
import bgu.spl171.net.impl.TFTP.msg.Disconnect;
import bgu.spl171.net.impl.TFTP.msg.Acknowledge;
import bgu.spl171.net.impl.TFTP.msg.Broadcast;
import bgu.spl171.net.impl.TFTP.msg.Error;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Created by himelbrand on 1/9/17.
 */
public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Message> {

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private short opCode =0;
    private int datePacketSize =0;

    @Override
    public byte[] encode(Message message) {

        byte[] encodedMessage = new byte[message.getPacketSize()];
        byte[] tempBytes;
        encodedMessage[0] = shortToBytes(message.getOpCode())[0];
        encodedMessage[1] = shortToBytes(message.getOpCode())[1];
        switch(message.getOpCode()){
            case 9: //broadcast
                encodedMessage[2] = ((Broadcast)message).getIsAdded();
                tempBytes = ((Broadcast)message).getFilename().getBytes();
                for(int i =0; i<tempBytes.length ;i++){
                    encodedMessage[i + 3] = tempBytes[i];
                }
                encodedMessage[encodedMessage.length - 1] = 0;

                if(((Broadcast)message).getFilename().contains("/0"))
                    encodedMessage=null;
                break;
            case 4:
                encodedMessage[2] = shortToBytes(((Acknowledge)message).getBlockNum())[0];
                encodedMessage[3] = shortToBytes(((Acknowledge)message).getBlockNum())[1];
                break;
            case 5:
                encodedMessage[2] = shortToBytes(((Error)message).getErrorCode())[0];
                encodedMessage[3] = shortToBytes(((Error)message).getErrorCode())[1];
                tempBytes = ((Error)message).getErrorMsg().getBytes();
                for(int i =0; i<tempBytes.length ;i++){
                    encodedMessage[i + 4] = tempBytes[i];
                }
                encodedMessage[encodedMessage.length - 1] = 0;

                if(((Error)message).getErrorMsg().contains("/0"))
                    encodedMessage=null;
                break;
            case 3:
                encodedMessage[2] = shortToBytes(((DataMessage)message).getDataSize())[0];
                encodedMessage[3] = shortToBytes(((DataMessage)message).getDataSize())[1];
                encodedMessage[4] = shortToBytes(((DataMessage)message).getBlockNum())[0];
                encodedMessage[5] = shortToBytes(((DataMessage)message).getBlockNum())[1];
                for(int i =0; i<((DataMessage) message).getData().length ;i++){
                    encodedMessage[i + 6] = ((DataMessage) message).getData()[i];
                }
                break;
        }

        for(int i=0;i<message.getPacketSize();
            i++)
        {
            System.out.println(encodedMessage[i]);
        }

        return encodedMessage;
    }

    public Message decode(byte[] message){
        Message decodeMessage;
        byte[] tempArray;
        int index;
        short opCode =bytesToShort(message);
       //@@ System.out.println("opcode :"+opCode);
        switch(opCode){
            case 1:
            case 2:

                 index = new String(Arrays.copyOfRange(message, 2,message.length)).indexOf('\0');

                tempArray =  Arrays.copyOfRange(message, 2,index+2);
                String fileName = new String(tempArray, StandardCharsets.UTF_8);
                decodeMessage = new ReadWrite(opCode,fileName);
                break;
            case 3:


                short dataSize=bytesToShort(Arrays.copyOfRange(message,2,4));
                short blockNum=bytesToShort(Arrays.copyOfRange(message,4,6));
                tempArray =  Arrays.copyOfRange(message, 6,dataSize+6);
                decodeMessage = new DataMessage(dataSize,blockNum,tempArray);
              //@@  System.out.println("Data size : "+dataSize);
                datePacketSize =0;
                break;
            case 4:
                decodeMessage = new Acknowledge(bytesToShort(Arrays.copyOfRange(message,2,4)));
                break;
            case 7:
                 index = new String(Arrays.copyOfRange(message, 2,message.length)).indexOf('\0');
                tempArray =  Arrays.copyOfRange(message, 2,index + 2);
                String username = new String(tempArray, StandardCharsets.UTF_8);
                decodeMessage = new Login(username);
                break;

            case 8:
                index = new String(Arrays.copyOfRange(message, 2,message.length)).indexOf('\0');
                tempArray =  Arrays.copyOfRange(message, 2,index + 2);
                String deleteFileName = new String(tempArray, StandardCharsets.UTF_8);
                decodeMessage = new DeleteFile(deleteFileName);
                break;
            case 6:
                decodeMessage = new DirRequest();
                break;
            case 10:
                decodeMessage = new Disconnect();
                break;
            default:
                decodeMessage = new Error((short) 4);
                break;

        }
        len =0;
        bytes = new byte[1 << 10];
        this.opCode =0;
        return decodeMessage;
    }


    private short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    private byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }



    @Override
    public Message decodeNextByte(byte nextByte) {
        //notice that the top 128 ascii characters have the same representation as their utf-8 counterparts
        //this allow us to do the following comparison
        System.out.println("nextbyte: "+nextByte);
        System.out.println("len: "+len);
        pushByte(nextByte);
        if(len == 2) {
            opCode = bytesToShort(bytes);
      //@@     System.out.println("opcode: "+opCode );
        }
        if(opCode != 0) {
            switch (opCode) {
                case 1:
                case 2:
                case 7:
                case 8:
                    if (nextByte == '\0') {
                       //@@ System.out.println(bytes.length +"length of bytes for opCode:"+opCode);
                        return decode(bytes);
                    }
                    break;
                case 3:
                    if (len == 4) {
                        datePacketSize = bytesToShort(Arrays.copyOfRange(bytes, 2, 4)) + 6;
                    //@@    System.out.println("data packet size : "+datePacketSize);
                    }
                    if (datePacketSize == len)
                        return decode(bytes);
                    if(len>datePacketSize)
                        new Error((short) 0);
                    break;
                case 4:
                    if (len == 4)
                        return decode(bytes);
                    break;
                case 6:
                case 10:
                 //@@   System.out.println("case 6/10");
                    return decode(bytes);
                default:
                    return decode(bytes);
            }
        }

        return null; //not a line yet
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }



}
