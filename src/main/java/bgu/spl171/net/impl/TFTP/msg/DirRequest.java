package bgu.spl171.net.impl.TFTP.msg;

/**
 * Created by himelbrand on 1/9/17.
 */
public class DirRequest extends Message {
    public DirRequest() {
        super((short) 6);
        this.packetSize=2;
    }
}
