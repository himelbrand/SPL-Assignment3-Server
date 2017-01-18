package bgu.spl171.net.impl.TFTPreactor;

import bgu.spl171.net.impl.TFTP.BidiMessagingProtocolImpl;
import bgu.spl171.net.impl.TFTP.MessageEncoderDecoderImpl;
import bgu.spl171.net.srv.Server;

/**
 * Created by himelbrand on 1/9/17.
 */
public class ReactorMain {
    public static void main(String[] args) {
                Server.reactor(
                Runtime.getRuntime().availableProcessors(),
                Integer.parseInt(args[0]), //port
                        () -> new BidiMessagingProtocolImpl(), //protocol factory
                        () -> new MessageEncoderDecoderImpl() //message encoder decoder factory
        ).serve();

    }
}
