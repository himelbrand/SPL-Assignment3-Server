package bgu.spl171.net.srv;

import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.srv.msg.DataMessage;
import bgu.spl171.net.srv.msg.Message;
import bgu.spl171.net.srv.msg.client2server.DeleteFile;
import bgu.spl171.net.srv.msg.client2server.Login;
import bgu.spl171.net.srv.msg.server2client.Acknowledge;
import bgu.spl171.net.srv.msg.server2client.Broadcast;
import bgu.spl171.net.srv.msg.server2client.Error;

import javax.xml.crypto.Data;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Shahar on 11/01/2017.
 */
public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message> {

    private int connectionId;
    private  ConnectionsImpl<Message> connections;
    private boolean shouldClose = false;

    private boolean loggedIn =false;
    private short lastOp;

    @Override
    public void start(int connectionId, Connections<Message> connections) {
        this.connectionId = connectionId;
        this.connections = (ConnectionsImpl<Message>)connections;
    }

    @Override
    public void process(Message message) throws FileNotFoundException {

        File f;
        String filesList;
        ArrayList<String> names;
        byte[] bytes;
        DataMessage newData;


        if (loggedIn || message.getOpCode() == 7) {

            switch (message.getOpCode()) {
                case 7: //Login
                    if (connections.logIn(((Login) message).getUsername())) {
                        loggedIn = true;
                        connections.send(connectionId, new Acknowledge((short) 0));//
                    } else {
                        connections.send(connectionId, new Error((short) 7)); //User already logged in
                    }
                    break;

                case 10:
                    connections.send(connectionId, new Acknowledge((short) 0)); //user disconnected
                    connections.disconnect(connectionId);
                    loggedIn = false;
                    break;


                case 8:
                    File file = new File("Files/" + ((DeleteFile)message).getFilename());
                    if(file.exists()){
                        file.delete();
                        connections.send(connectionId, new Acknowledge((short) 0)); //file deleted
                        connections.broadcast(new Broadcast((byte)(0),((DeleteFile)message).getFilename()));
                    }else{
                        connections.send(connectionId, new Error((short) 1)); //User already logged in
                    }
                    break;
                case 6: //DIRQ Packets
                    lastOp = 6;
                    f = new File("/Files/");
                    filesList = "";
                    names = new ArrayList<>(Arrays.asList(f.list()));
                    for (String fileName : names) {
                        filesList += fileName + "\0";
                    }
                    bytes = filesList.getBytes();
                    newData = new DataMessage((short) (6), (short) 0, Arrays.copyOfRange(bytes, 0, (bytes.length < 512 ? bytes.length : 512)));
                    connections.send(connectionId, newData);

                    break;





                case 4: // ACK
                    switch (lastOp) {
                        case 6: //DIRQ
                            short blockNum = ((Acknowledge) message).getBlockNum();
                            f = new File("/Files/");
                            filesList = "";
                            names = new ArrayList<>(Arrays.asList(f.list()));
                            for (String fileName : names) {
                                filesList += fileName + "\0";
                            }
                            bytes = filesList.getBytes();
                            if (bytes.length == blockNum * 512) {
                                newData = new DataMessage((short) 0, (short) (blockNum + 1), new byte[0]);
                                connections.send(connectionId, newData);
                            } else if (bytes.length > (int) blockNum * 512) {
                                int packetSize = (bytes.length < 512 * (blockNum + 1) ? bytes.length - (512 * blockNum) : 512);
                                newData = new DataMessage((short) packetSize, (short) (blockNum + 1), Arrays.copyOfRange(bytes, 512 * blockNum, (bytes.length < 512 * (blockNum + 1) ? bytes.length : 512 * (blockNum + 1))));
                                connections.send(connectionId, newData);
                            }
                            break;

                    }
                    break;

                default:
                    connections.send(connectionId, new Error((short) 4)); //unknown opcode
                    break;

            }
        }
        else{
            connections.send(connectionId, new Error((short) 4)); //unknown opcode (User not logged in (this case))
        }
    }
    private void continueData(int blockNumber) {

    }


    @Override
    public boolean shouldTerminate() {
        //return false;
        return shouldClose;
    }
}
