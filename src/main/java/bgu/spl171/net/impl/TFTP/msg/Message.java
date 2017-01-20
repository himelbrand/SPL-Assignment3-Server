package bgu.spl171.net.impl.TFTP.msg;

public class Message {
    private short opCode;
    protected int packetSize;

    /**
     * Constructor of a general message
     * @param opCode the opCode of the child message
     */
    public Message(short opCode){
        this.opCode=opCode;
    }

    /**
     * @return the opCode of the message
     */
    public short getOpCode(){
        return opCode;
    }

    /**
     * @return the size of the entire message packet
     */
    public int getPacketSize(){
        return packetSize;
    }

}
