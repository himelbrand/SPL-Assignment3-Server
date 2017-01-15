package bgu.spl171.net.impl.TFTP.msg;

/**
 * Created by himelbrand on 1/9/17.
 */
public class Acknowledge extends Message {
    private short blockNum;

    public Acknowledge(short blockNum) {
        super((short) 4);
        this.blockNum = blockNum;
        this.packetSize=4;
    }

    public short getBlockNum() {
        return blockNum;
    }

}
