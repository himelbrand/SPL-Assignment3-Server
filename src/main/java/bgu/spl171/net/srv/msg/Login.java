package bgu.spl171.net.srv.msg;

/**
 * Created by himelbrand on 1/9/17.
 */
public class Login extends Message {
    private String username;
    public Login(String username) {
        super((short) 7);
        this.username=username;
    }
    public String getUsername() {
        return username;
    }
}
