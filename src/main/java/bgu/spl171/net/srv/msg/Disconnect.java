package bgu.spl171.net.srv.msg;

import bgu.spl171.net.srv.msg.Message;

/**
 * Created by himelbrand on 1/9/17.
 */
public class Disconnect extends Message {
    public Disconnect() {
        super((short) 10);
        this.packetSize=2;
    }
}