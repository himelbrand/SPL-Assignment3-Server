package bgu.spl171.net.impl.TFTP.msg;
/**
 * Delete file request Message
 * @author Omri Himelbrand
 * @author Shahar Nussbaum
 */
public class DeleteFile extends Message {
    private String filename;

    /**
     * Constructor
     * @param filename the requested file to be deleted
     */
    public DeleteFile(String filename) {
        super((short) 8);
        this.filename = filename;
        this.packetSize=3+filename.getBytes().length;
    }

    /**
     * @return the file name that is requested for deletion
     */
    public String getFilename() {
        return filename;
    }
}
