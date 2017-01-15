package bgu.spl171.net.impl.TFTP.msg;

/**
 * Created by himelbrand on 1/9/17.
 */
public class Broadcast extends Message {
    private byte isAdded;
    private String filename;
    public Broadcast(byte isAdded,String filename) {
        super((short)9);
        this.isAdded=isAdded;
        this.filename=filename;
        this.packetSize=4+filename.getBytes().length;
    }

    public byte getIsAdded() {
        return isAdded;
    }

    public String getFilename() {
        return filename;
    }
}
