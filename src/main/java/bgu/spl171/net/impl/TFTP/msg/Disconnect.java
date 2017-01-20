package bgu.spl171.net.impl.TFTP.msg;
/**
 * Disconnect Message
 * @author Omri Himelbrand
 * @author Shahar Nussbaum
 */
public class Disconnect extends Message {
    /**
     * Constructor
     */
    public Disconnect() {
        super((short) 10);
        this.packetSize=2;
    }
}