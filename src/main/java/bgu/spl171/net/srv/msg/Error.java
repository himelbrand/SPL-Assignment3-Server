package bgu.spl171.net.srv.msg;

/**
 * Created by himelbrand on 1/9/17.
 */
public class Error extends Message{
    private short errorCode;
    private String errorMsg;
    public Error(short errorCode) {
        //TODO: change error messages
        super((short) 7);
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
    }

    public short getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
