package bgu.spl171.net.srv.msg;

/**
 * Created by himelbrand on 1/9/17.
 */
public class Acknowledge extends Message{
    private short blockNum;
    public Acknowledge(short blockNum) {
        super((short) 4);
        this.blockNum = blockNum;
    }

    public short getBlockNum() {
        return blockNum;
    }
}
