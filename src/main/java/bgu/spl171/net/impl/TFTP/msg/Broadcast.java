package bgu.spl171.net.impl.TFTP.msg;
/**
 * Broadcast Message
 * @author Omri Himelbrand
 * @author Shahar Nussbaum
 */
public class Broadcast extends Message {
    private byte isAdded;
    private String filename;

    /**
     * Constructor
     * @param isAdded was the file added? 1 for true , 0 for false
     * @param filename the filename associated with this broadcast
     */
    public Broadcast(byte isAdded,String filename) {
        super((short)9);
        this.isAdded=isAdded;
        this.filename=filename;
        this.packetSize=4+filename.getBytes().length;
    }

    /**
     * @return was the file that triggered the broadcast was added, boolean
     */
    public byte getIsAdded() {
        return isAdded;
    }

    /**
     * @return the file name of the file that was added/deleted
     */
    public String getFilename() {
        return filename;
    }
}
