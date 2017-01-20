package bgu.spl171.net.impl.TFTP.msg;

public class ReadWrite extends Message {
    private String filename;

    /**
     * Constructor
     * @param opCode or opCode 1 for RRQ or opCode 2 for WRQ
     * @param filename the requested filename for this operation
     */
    public ReadWrite(short opCode,String filename) {
        super(opCode);
        this.filename = filename;
        this.packetSize=3+filename.getBytes().length;
    }

    /**
     * @return the file name of the operation
     */
    public String getFilename() {
        return filename;
    }
}
