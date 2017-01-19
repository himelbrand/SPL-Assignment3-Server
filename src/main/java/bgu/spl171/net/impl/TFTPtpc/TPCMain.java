package bgu.spl171.net.impl.TFTPtpc;

import bgu.spl171.net.impl.TFTP.BidiMessagingProtocolImpl;
import bgu.spl171.net.impl.TFTP.MessageEncoderDecoderImpl;
import bgu.spl171.net.srv.Server;

import java.io.File;


/**
 * Created by himelbrand on 1/9/17.
 */
public class TPCMain {

    public static void main(String[] args) {

        new File("Files/TempFiles").mkdirs();
        Server.threadPerClient(
                Integer.parseInt(args[0]), //port
                BidiMessagingProtocolImpl::new, //protocol factory
                MessageEncoderDecoderImpl::new //message encoder decoder factory
        ).serve();



    }
}