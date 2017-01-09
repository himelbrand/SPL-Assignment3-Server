package bgu.spl171.net.srv.msg.client2server;

import bgu.spl171.net.srv.msg.Message;

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
