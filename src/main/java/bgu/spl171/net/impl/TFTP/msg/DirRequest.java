package bgu.spl171.net.impl.TFTP.msg;

public class DirRequest extends Message {
    /**
     * Constructor
     */
    public DirRequest() {
        super((short) 6);
        this.packetSize=2;
    }
}
