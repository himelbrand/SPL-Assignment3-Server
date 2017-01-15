package bgu.spl171.net.impl.TFTP.msg;

/**
 * Created by himelbrand on 1/9/17.
 */
public class Disconnect extends Message {
    public Disconnect() {
        super((short) 10);
        this.packetSize=2;
    }
}