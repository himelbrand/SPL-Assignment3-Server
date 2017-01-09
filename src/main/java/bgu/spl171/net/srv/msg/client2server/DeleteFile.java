package bgu.spl171.net.srv.msg.client2server;

import bgu.spl171.net.srv.msg.Message;

/**
 * Created by himelbrand on 1/9/17.
 */
public class DeleteFile extends Message {
    private String filename;
    public DeleteFile(String filename) {
        super((short) 8);
        this.filename = filename;
        this.packetSize=3+filename.getBytes().length;
    }

    public String getFilename() {
        return filename;
    }
}