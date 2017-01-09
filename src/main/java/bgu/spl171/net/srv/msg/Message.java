package bgu.spl171.net.srv.msg;

/**
 * Created by himelbrand on 1/9/17.
 */
public class Message {
    protected short opCode;
    protected int packetSize;
    public Message(short opCode){
        this.opCode=opCode;
    }
    public short getOpCode(){
        return opCode;
    }
    public int getPacketSize(){
        return packetSize;
    }

}
