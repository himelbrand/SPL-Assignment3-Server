package bgu.spl171.net.srv.msg;

/**
 * Created by himelbrand on 1/9/17.
 */
public class DeleteFile extends Message {
    private String filename;
    public DeleteFile(String filename) {
        super((short) 8);
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}
