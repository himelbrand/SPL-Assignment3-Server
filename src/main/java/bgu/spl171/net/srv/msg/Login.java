package bgu.spl171.net.srv.msg;

import bgu.spl171.net.srv.msg.Message;

/**
 * Created by himelbrand on 1/9/17.
 */
public class Login extends Message {
    private String username;
    public Login(String username) {
        super((short) 7);
        this.username=username;
        this.packetSize=3+username.getBytes().length;
    }
    public String getUsername() {
        return username;
    }
}
