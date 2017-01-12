package bgu.spl171.net.impl.TFTPtpc;

import bgu.spl171.net.impl.newsfeed.NewsFeed;
import bgu.spl171.net.impl.rci.ObjectEncoderDecoder;
import bgu.spl171.net.impl.rci.RemoteCommandInvocationProtocol;
import bgu.spl171.net.srv.Server;

/**
 * Created by himelbrand on 1/9/17.
 */
public class TPCMain {

    public static void main(String[] args) {

//
//        Server.threadPerClient(
//                Runtime.getRuntime().availableProcessors(),
//                7777, //port
//                () ->  new RemoteCommandInvocationProtocol<>(feed), //protocol factory
//                ObjectEncoderDecoder::new //message encoder decoder factory
//        ).serve();

    }
}