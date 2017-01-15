package bgu.spl171.net.impl.TFTPtpc;

import bgu.spl171.net.api.MessageEncoderDecoder;
import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.impl.newsfeed.NewsFeed;
import bgu.spl171.net.impl.rci.ObjectEncoderDecoder;
import bgu.spl171.net.impl.rci.RemoteCommandInvocationProtocol;
import bgu.spl171.net.srv.BidiMessagingProtocolImpl;
import bgu.spl171.net.srv.MessageEncoderDecoderImpl;
import bgu.spl171.net.srv.Server;
import bgu.spl171.net.srv.msg.Message;


/**
 * Created by himelbrand on 1/9/17.
 */
public class TPCMain {

    public static void main(String[] args) {


        Server.threadPerClient(
                Integer.parseInt(args[0]), //port
                BidiMessagingProtocolImpl::new, //protocol factory
                MessageEncoderDecoderImpl::new //message encoder decoder factory
        ).serve();

    }
}