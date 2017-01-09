package bgu.spl171.net.srv.msg;

/**
 * Created by himelbrand on 1/9/17.
 */
public class Disconnect extends Message{
    public Disconnect() {
        super((short) 10);
        this.packetSize=2;
    }
}
