package bgu.spl171.net.impl.TFTP.msg;
/**
 * Data Message
 * @author Omri Himelbrand
 * @author Shahar Nussbaum
 */
public class DataMessage extends Message {
    private short dataSize;
    private short blockNum;
    private byte[] data;

    /**
     * Constructor
     * @param dataSize the size of the data that needs to be read
     * @param blockNum the block number of this data packet
     * @param data the byte array containing the data
     */
    public DataMessage(short dataSize,short blockNum,byte[] data) {
        super((short) 3);
        this.dataSize = dataSize;
        this.blockNum = blockNum;
        this.data = data.clone();
        this.packetSize=6+data.length;

    }

    /**
     * @return the size of the data sent in this data packet
     */
    public short getDataSize() {
        return dataSize;
    }

    /**
     * @return the block number of the data being sent in this data packet
     */
    public short getBlockNum() {
        return blockNum;
    }

    /**
     * @return the bytes array containing the data itself
     */
    public byte[] getData() {
        return data;
    }
}
