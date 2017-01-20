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
 * This is the implementation of {@link MessageEncoderDecoder} using {@link Message},
 * an encoder decoder for our TFTP protocol
 * @author Omri Himelbrand
 * @author Shahar Nussbaum
 */
public class MessageEncoderDecoderTFTP implements MessageEncoderDecoder<Message> {

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
            case 4://Acknowledge
                encodedMessage[2] = shortToBytes(((Acknowledge)message).getBlockNum())[0];
                encodedMessage[3] = shortToBytes(((Acknowledge)message).getBlockNum())[1];
                break;
            case 5://Error
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
            case 3://Data
                encodedMessage[2] = shortToBytes(((DataMessage)message).getDataSize())[0];
                encodedMessage[3] = shortToBytes(((DataMessage)message).getDataSize())[1];
                encodedMessage[4] = shortToBytes(((DataMessage)message).getBlockNum())[0];
                encodedMessage[5] = shortToBytes(((DataMessage)message).getBlockNum())[1];
                for(int i =0; i<((DataMessage) message).getData().length ;i++){
                    encodedMessage[i + 6] = ((DataMessage) message).getData()[i];
                }
                break;
        }
        return encodedMessage;
    }

    private Message decode(byte[] message){
        Message decodeMessage;
        byte[] tempArray;
        int index;
        short opCode =bytesToShort(message);
        switch(opCode){
            case 1://RRQ
            case 2://WRQ
                index = new String(Arrays.copyOfRange(message, 2,message.length)).indexOf('\0');
                tempArray =  Arrays.copyOfRange(message, 2,index+2);
                String fileName = new String(tempArray, StandardCharsets.UTF_8);
                decodeMessage = new ReadWrite(opCode,fileName);
                break;
            case 3://Data
                short dataSize=bytesToShort(Arrays.copyOfRange(message,2,4));
                short blockNum=bytesToShort(Arrays.copyOfRange(message,4,6));
                tempArray =  Arrays.copyOfRange(message, 6,dataSize+6);
                decodeMessage = new DataMessage(dataSize,blockNum,tempArray);
                datePacketSize =0;
                break;
            case 4://Acknowledge
                decodeMessage = new Acknowledge(bytesToShort(Arrays.copyOfRange(message,2,4)));
                break;
            case 7://LOGRQ
                index = new String(Arrays.copyOfRange(message, 2,message.length)).indexOf('\0');
                tempArray =  Arrays.copyOfRange(message, 2,index + 2);
                String username = new String(tempArray, StandardCharsets.UTF_8);
                decodeMessage = new Login(username);
                break;
            case 8://DELRQ
                index = new String(Arrays.copyOfRange(message, 2,message.length)).indexOf('\0');
                tempArray =  Arrays.copyOfRange(message, 2,index + 2);
                String deleteFileName = new String(tempArray, StandardCharsets.UTF_8);
                decodeMessage = new DeleteFile(deleteFileName);
                break;
            case 6://DIRQ
                decodeMessage = new DirRequest();
                break;
            case 10://DISC
                decodeMessage = new Disconnect();
                break;
            default://unknown OpCode
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
        pushByte(nextByte);
        if(len == 2) {
            opCode = bytesToShort(bytes);
        }
        if(opCode != 0) {
            switch (opCode) {
                case 1://RRQ
                case 2://WRQ
                case 7://LOGRQ
                case 8://DELRQ
                    if (nextByte == '\0')
                        return decode(bytes);
                    break;
                case 3://DATA
                    if (len == 4)
                        datePacketSize = bytesToShort(Arrays.copyOfRange(bytes, 2, 4)) + 6;
                    if (datePacketSize == len)
                        return decode(bytes);
                    if(len>datePacketSize)
                        new Error((short) 0);
                    break;
                case 4://Acknowledge
                    if (len == 4)
                        return decode(bytes);
                    break;
                case 6://DIRQ
                case 10://DISC
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
