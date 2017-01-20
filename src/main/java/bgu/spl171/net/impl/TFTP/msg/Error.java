package bgu.spl171.net.impl.TFTP.msg;
/**
 * Error Message
 * @author Omri Himelbrand
 * @author Shahar Nussbaum
 */
public class Error extends Message {
    private short errorCode;
    private String errorMsg;

    /**
     * Constructor
     * @param errorCode the code number of the needed error
     */
    public Error(short errorCode) {
        super((short) 5);
        this.errorCode = errorCode;
        switch (errorCode){
            case 0:
                this.errorMsg = "undefined Error.";
                break;
            case 1:
                this.errorMsg = "Requested File not found, could not complete operation.";
                break;
            case 2:
                this.errorMsg = "Access violation – File cannot be written, read or deleted.";
                break;
            case 3:
                this.errorMsg = "Disk full or allocation exceeded – No room in disk.";
                break;
            case 4:
                this.errorMsg = "Illegal TFTP operation – Unknown Opcode.";
                break;
            case 5:
                this.errorMsg = "File already exists – File name exists on WRQ.";
                break;
            case 6:
                this.errorMsg = "User not logged in – Any opcode received before Login completes.";
                break;
            case 7:
                this.errorMsg = "User already logged in – Login username already connected.";
                break;
        }
        this.packetSize=5+errorMsg.getBytes().length;
    }

    /**
     * @return the error code of this error
     */
    public short getErrorCode() {
        return errorCode;
    }
    /**
     * @return the error message of this error
     */
    public String getErrorMsg() {
        return errorMsg;
    }
}
