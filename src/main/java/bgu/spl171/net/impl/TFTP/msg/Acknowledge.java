package bgu.spl171.net.impl.TFTP.msg;

/**
 * Acknowledge Message
 * @author Omri Himelbrand
 * @author Shahar Nussbaum
 */
public class Acknowledge extends Message {
    private short blockNum;

    /**
     * Constructor
     * @param blockNum the block number being acknowledged
     */
    public Acknowledge(short blockNum) {
        super((short) 4);
        this.blockNum = blockNum;
        this.packetSize=4;
    }

    /**
     * @return the block number being acknowledged
     */
    public short getBlockNum() {
        return blockNum;
    }

}
