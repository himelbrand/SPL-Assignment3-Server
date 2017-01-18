package bgu.spl171.net.impl.TFTPreactor;

import bgu.spl171.net.impl.TFTP.BidiMessagingProtocolImpl;
import bgu.spl171.net.impl.TFTP.MessageEncoderDecoderImpl;
import bgu.spl171.net.srv.Server;

/**
 * Created by himelbrand on 1/9/17.
 */
public class ReactorMain {
    public static void main(String[] args) {
        System.out.println("hello!");
                Server.reactor(
                Runtime.getRuntime().availableProcessors(),
                Integer.parseInt(args[0]), //port
                        () -> new BidiMessagingProtocolImpl(){}, //protocol factory
                MessageEncoderDecoderImpl::new //message encoder decoder factory
        ).serve();

    }
}
