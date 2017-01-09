package bgu.spl171.net.srv.msg;

/**
 * Created by himelbrand on 1/9/17.
 */
public class DataMessage extends Message {
    private short packetSize;
    private short blockNum;
    private byte[] data;
    public DataMessage(short packetSize,short blockNum,byte[] data) {
        super((short) 3);
        this.packetSize = packetSize;
        this.blockNum = blockNum;
        this.data = data.clone();
    }
    public short getPacketSize() {
        return packetSize;
    }
    public short getBlockNum() {
        return blockNum;
    }
    public byte[] getData() {
        return data;
    }
}
