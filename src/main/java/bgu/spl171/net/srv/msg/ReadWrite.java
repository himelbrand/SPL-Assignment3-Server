package bgu.spl171.net.srv.msg;

/**
 * Created by himelbrand on 1/9/17.
 */
public class ReadWrite extends Message{
    private String filename;
    public ReadWrite(short opCode,String filename) {
        super(opCode);
        this.filename = filename;
    }
    public String getFilename() {
        return filename;
    }
}
