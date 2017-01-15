package bgu.spl171.net.impl.TFTP.msg;

/**
 * Created by himelbrand on 1/9/17.
 */
public class DataMessage extends Message {
    private short dataSize;
    private short blockNum;
    private byte[] data;
    public DataMessage(short dataSize,short blockNum,byte[] data) {
        super((short) 3);
        this.dataSize = dataSize;
        this.blockNum = blockNum;
        this.data = data.clone();
        this.packetSize=6+data.length;

    }
    public short getDataSize() {
        return dataSize;
    }
    public short getBlockNum() {
        return blockNum;
    }
    public byte[] getData() {
        return data;
    }
}
