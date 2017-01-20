package bgu.spl171.net.impl.TFTP.msg;

public class Disconnect extends Message {
    /**
     * Constructor
     */
    public Disconnect() {
        super((short) 10);
        this.packetSize=2;
    }
}