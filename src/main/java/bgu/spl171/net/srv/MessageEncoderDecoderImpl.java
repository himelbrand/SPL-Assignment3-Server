package bgu.spl171.net.srv;

import bgu.spl171.net.api.MessageEncoderDecoder;
import bgu.spl171.net.srv.msg.Message;
import bgu.spl171.net.srv.msg.server2client.Broadcast;

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
        byte[] encodedMessage;
        switch(message.getOpCode()){
            case 9: //broadcast
                int fileNameByteSize = ((Broadcast)message).getFilename().getBytes().length;
                encodedMessage = new byte[fileNameByteSize + 4];
                for(int i =0; i<fileNameByteSize ;i++){
                    encodedMessage[i + 2]
                }
                encodedMessage[0] = shortToBytes(message.getOpCode())[0];
                encodedMessage[1] = shortToBytes(message.getOpCode())[1];
                encodedMessage[encodedMessage.length - 1] = 0;
                break;
            case 4:
                break;
            case 7:
                break;
            case 3:
                break;

        }
        return null;
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
