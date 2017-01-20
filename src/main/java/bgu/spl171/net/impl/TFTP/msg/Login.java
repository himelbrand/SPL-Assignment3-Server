package bgu.spl171.net.impl.TFTP.msg;
/**
 * Login request Message
 * @author Omri Himelbrand
 * @author Shahar Nussbaum
 */
public class Login extends Message {
    private String username;

    /**
     * Constructor
     * @param username the wanted user name for the login
     */
    public Login(String username) {
        super((short) 7);
        this.username=username;
        this.packetSize=3+username.getBytes().length;
    }

    /**
     * @return the username of this login request
     */
    public String getUsername() {
        return username;
    }
}
