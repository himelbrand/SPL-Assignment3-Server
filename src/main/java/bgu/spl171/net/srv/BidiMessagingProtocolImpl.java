package bgu.spl171.net.srv;

import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.srv.msg.DataMessage;
import bgu.spl171.net.srv.msg.Message;

import javax.xml.crypto.Data;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Shahar on 11/01/2017.
 */
public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message> {

    private int connectionId;
    private  Connections<Message> connections;
    private boolean shouldClose = false;

    @Override
    public void start(int connectionId, Connections<Message> connections) {
        this.connectionId = connectionId;
        this.connections = connections;
    }

    @Override
    public void process(Message message) {



        switch(message.getOpCode())
        {
            case 6: //DIRQ Packets
                File f = new File("/Files/");
                String filesList ="";
                ArrayList<String> names = new ArrayList<String>(Arrays.asList(f.list()));
                for(String fileName:names){
                    filesList +=fileName + "\0";
                }
                byte[] bytes = filesList.getBytes();

                int numberOfMessages;
                if(bytes.length < 512){
                    numberOfMessages = 1;
                }else{
                    numberOfMessages = bytes.length/512 + 1;
                }

                for(int i =0 ; i<numberOfMessages ;i++)
                {
                    DataMessage newData = new DataMessage((short)(6),(short)i,Arrays.copyOfRange(bytes,i *512,(bytes.length < (i+1)*512 ? bytes.length : (i+1)*512 )));
                    connections.send(connectionId,newData);
                }

                break;
        }
    }

    @Override
    public boolean shouldTerminate() {
        //return false;
        return shouldClose;
    }
}
