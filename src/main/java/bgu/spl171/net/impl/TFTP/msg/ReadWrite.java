package bgu.spl171.net.impl.TFTP.msg;

/**
 * Created by himelbrand on 1/9/17.
 */
public class ReadWrite extends Message {
    private String filename;

    public ReadWrite(short opCode,String filename) {
        super(opCode);
        this.filename = filename;
        this.packetSize=3+filename.getBytes().length;
    }
    public String getFilename() {
        return filename;
    }
}
