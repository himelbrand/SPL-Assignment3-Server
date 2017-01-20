package bgu.spl171.net.impl.TFTP.msg;
/**
 * Directory request Message
 * @author Omri Himelbrand
 * @author Shahar Nussbaum
 */
public class DirRequest extends Message {
    /**
     * Constructor
     */
    public DirRequest() {
        super((short) 6);
        this.packetSize=2;
    }
}
