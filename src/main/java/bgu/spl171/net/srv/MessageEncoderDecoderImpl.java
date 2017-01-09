package bgu.spl171.net.srv;

import bgu.spl171.net.api.MessageEncoderDecoder;
import bgu.spl171.net.srv.msg.DataMessage;
import bgu.spl171.net.srv.msg.Message;
import bgu.spl171.net.srv.msg.client2server.DeleteFile;
import bgu.spl171.net.srv.msg.client2server.Login;
import bgu.spl171.net.srv.msg.client2server.ReadWrite;
import bgu.spl171.net.srv.msg.server2client.Acknowledge;
import bgu.spl171.net.srv.msg.server2client.Broadcast;
import bgu.spl171.net.srv.msg.server2client.Error;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Created by himelbrand on 1/9/17.
 */
public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Message> {
    @Override
    public Message decodeNextByte(byte nextByte) {
        return null;
    }

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
        return encodedMessage;
    }

    public Message decode(byte[] message){
        Message decodeMessage;
        byte[] tempArray;
        short opCode =bytesToShort(message);
        switch(opCode){
            case 1:
            case 2:
                tempArray =  Arrays.copyOfRange(message, 2,message.length -1);
                String fileName = new String(tempArray, StandardCharsets.UTF_8);
                decodeMessage = new ReadWrite(opCode,fileName);
                break;
            case 3:
                tempArray =  Arrays.copyOfRange(message, 6,message.length);
                short dataSize=bytesToShort(Arrays.copyOfRange(tempArray,2,4));
                short blockNum=bytesToShort(Arrays.copyOfRange(tempArray,4,6));
                decodeMessage = new DataMessage(dataSize,blockNum,tempArray);
                break;
            case 7:
                tempArray =  Arrays.copyOfRange(message, 2,message.length);
                String username = new String(tempArray, StandardCharsets.UTF_8);
                decodeMessage = new Login(username);
                break;

            case 8:
                 tempArray =  Arrays.copyOfRange(message, 2,message.length -1);
                String deleteFileName = new String(tempArray, StandardCharsets.UTF_8);
                decodeMessage = new DeleteFile(deleteFileName);
                break;
            case 6:
                tempArray =  Arrays.copyOfRange(message, 2,message.length -1);
                String fileName = new String(tempArray, StandardCharsets.UTF_8);
                decodeMessage = new DeleteFile(fileName);
                break;
            default:

                decodeMessage = null;
                break;

        }
        return decodeMessage;
    }


    public short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }


}
