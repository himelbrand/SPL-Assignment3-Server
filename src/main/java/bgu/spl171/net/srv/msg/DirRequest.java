package bgu.spl171.net.srv.msg;

import bgu.spl171.net.srv.msg.Message;

/**
 * Created by himelbrand on 1/9/17.
 */
public class DirRequest extends Message {
    public DirRequest() {
        super((short) 6);
        this.packetSize=2;
    }
}
