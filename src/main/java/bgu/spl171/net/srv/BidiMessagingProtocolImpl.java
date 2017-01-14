package bgu.spl171.net.srv;

import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.srv.msg.DataMessage;
import bgu.spl171.net.srv.msg.Message;
import bgu.spl171.net.srv.msg.client2server.DeleteFile;
import bgu.spl171.net.srv.msg.client2server.Login;
import bgu.spl171.net.srv.msg.client2server.ReadWrite;
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
    private File lastFile;
    private FileOutputStream out;

    @Override
    public void start(int connectionId, Connections<Message> connections) {
        this.connectionId = connectionId;
        this.connections = (ConnectionsImpl<Message>)connections;
    }

    @Override
    public void process(Message message) {

        String filesList;
        ArrayList<String> names;
        byte[] bytes;
        DataMessage newData;
        short blockNum;

        if (loggedIn || message.getOpCode() == 7) {

            switch (message.getOpCode()) {
                case 7: //Login
                    lastOp = 7;
                    if (connections.logIn(((Login) message).getUsername())) {
                        loggedIn = true;
                        connections.send(connectionId, new Acknowledge((short) 0));//
                    } else {
                        connections.send(connectionId, new Error((short) 7)); //User already logged in
                    }
                    break;

                case 10: //Disconnect
                    lastOp = 10;
                    connections.send(connectionId, new Acknowledge((short) 0)); //user disconnected
                    connections.disconnect(connectionId);
                    loggedIn = false;
                    break;


                case 8: //Delete
                    lastOp = 8;
                    lastFile = new File("Files/" + ((DeleteFile)message).getFilename());
                    if(lastFile.exists()){
                        lastFile.delete();
                        connections.send(connectionId, new Acknowledge((short) 0)); //file deleted
                        connections.broadcast(new Broadcast((byte)(0),((DeleteFile)message).getFilename()));
                    }else{
                        connections.send(connectionId, new Error((short) 1)); //File not found
                    }
                    lastFile = null;
                    break;

                case 6: //DIRQ Packets
                    lastOp = 6;
                    lastFile = new File("Files/");
                    filesList = "";
                    names = new ArrayList<>(Arrays.asList(lastFile.list()));
                    for (String fileName : names) {
                        filesList += fileName + "\0";
                    }
                    bytes = filesList.getBytes();
                    newData = new DataMessage((short) (6), (short) 0, Arrays.copyOfRange(bytes, 0, (bytes.length < 512 ? bytes.length : 512)));
                    connections.send(connectionId, newData);
                    break;

                case 4: // ACK
                     blockNum = ((Acknowledge) message).getBlockNum();
                    switch (lastOp) {
                        case 6: //DIRQ ACK
                            filesList = "";
                            names = new ArrayList<>(Arrays.asList(lastFile.list()));
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
                            }else{
                                lastOp = 0;
                                lastFile = null;
                            }
                            break;

                        case 1://Read ACK
                            InputStream ins = null;
                            try {
                                ins = new FileInputStream(lastFile);
                            } catch (FileNotFoundException e) {
                                connections.send(connectionId, new Error((short) 1)); //File not found
                            }
                            if(lastFile.exists()) {
                                if (lastFile.length() == blockNum * 512) { //In case we need to send one more packet(empty packet to confirm file is arrived)
                                    newData = new DataMessage((short) 0, (short) (blockNum + 1), new byte[0]);
                                    connections.send(connectionId, newData);
                                } else if (lastFile.length() > (int) blockNum * 512) {
                                    int packetSize = (lastFile.length() < 512 * (blockNum + 1) ? (int) lastFile.length() - (512 * blockNum) : 512);
                                    bytes = new byte[packetSize];
                                    try {
                                        ins.skip(blockNum*512);
                                        ins.read(bytes);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    newData = new DataMessage((short) packetSize, (short) (blockNum + 1), bytes);
                                    connections.send(connectionId, newData);
                                } else {
                                    lastOp = 0;
                                    lastFile = null;
                                }
                            }
                            break;


                    }
                    break;

                case 3:
                    switch(lastOp){
                        case 2:
                            blockNum = ((DataMessage)message).getBlockNum();
                            bytes = ((DataMessage)message).getData();
                            if(bytes.length == 0){
                                connections.send(connectionId, new Acknowledge(blockNum));
                            }else {
                                try {
                                    out.write(bytes);
                                    connections.send(connectionId, new Acknowledge(blockNum));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if(bytes.length <512){
                                    connections.broadcast(new Broadcast((byte)(1),lastFile.getName()));

                                }
                            }

                            break;
                    }
                    break;


                case 1: //Read
                    lastOp=1;
                    lastFile = new File("Files/" + ((ReadWrite)message).getFilename());
                    InputStream ins = null;
                    try {
                        ins = new FileInputStream(lastFile);
                    } catch (FileNotFoundException e) {
                       lastOp=0;
                       lastFile = null;
                        connections.send(connectionId, new Error((short) 1)); //File not found
                    }

                    bytes = new byte[(lastFile.length() < 512 ? (int)lastFile.length() : 512)];
                    try {
                        ins.read(bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    newData = new DataMessage((short) bytes.length, (short)0,bytes);
                    connections.send(connectionId, newData);
                    break;
g
                case 2: //Write
                    lastOp=2;
                     lastFile  = new File("temp/" + ((ReadWrite)message).getFilename());
                     File checkTempFile = new File("Files/" + ((ReadWrite)message).getFilename()); //In case someone else is uploding a file with the same name
                     if(lastFile.exists() || checkTempFile.exists()){
                         lastFile = null;
                         lastOp=0;
                         connections.send(connectionId, new Error((short) 5)); //File already exist
                     }else {
                         try {
                             lastFile.createNewFile(); //double check
                             out = new FileOutputStream(lastFile);
                         } catch (IOException e) {
                             lastFile = null;
                             lastOp=0;
                             connections.send(connectionId, new Error((short) 5)); //File already exist
                         }
                         connections.send(connectionId, new Acknowledge((short) 0));
                     }
                    break;


                    default:
                        connections.send(connectionId, new Error((short) 4)); //unknown opcode
                    break;

            }
        }
        else{
            connections.send(connectionId, new Error((short) 4)); //unknown opcode (User not logged in this case)
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
