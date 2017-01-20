package bgu.spl171.net.impl.TFTPreactor;

import bgu.spl171.net.impl.TFTP.BidiMessagingProtocolTFTP;
import bgu.spl171.net.impl.TFTP.MessageEncoderDecoderTFTP;
import bgu.spl171.net.srv.Server;

import java.io.File;

/**
 * Executable class of reactor server
 * @author Omri Himelbrand
 * @author Shahar Nussbaum
 */
public class ReactorMain {
    public static void main(String[] args) {
        new File("Files/TempFiles").mkdirs();//creates folder for temp files
                Server.reactor(
                Runtime.getRuntime().availableProcessors()+1,//plus 1 , so if only one available processor will have at least 2 threads
                Integer.parseInt(args[0]), //port
                        () -> new BidiMessagingProtocolTFTP(), //protocol factory
                        () -> new MessageEncoderDecoderTFTP() //message encoder decoder factory
        ).serve();
        System.out.println("Reactor server started");
    }
}
