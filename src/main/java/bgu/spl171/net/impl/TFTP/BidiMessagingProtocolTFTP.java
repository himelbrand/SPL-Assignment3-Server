package bgu.spl171.net.impl.TFTP;

import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.impl.TFTP.msg.DataMessage;
import bgu.spl171.net.impl.TFTP.msg.Message;
import bgu.spl171.net.impl.TFTP.msg.DeleteFile;
import bgu.spl171.net.impl.TFTP.msg.Login;
import bgu.spl171.net.impl.TFTP.msg.ReadWrite;
import bgu.spl171.net.impl.TFTP.msg.Acknowledge;
import bgu.spl171.net.impl.TFTP.msg.Broadcast;
import bgu.spl171.net.impl.TFTP.msg.Error;
import bgu.spl171.net.srv.ConnectionsImpl;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is the implementation of {@link BidiMessagingProtocol} using {@link Message}
 * a bi-directional TFTP protocol
 * @author Omri Himelbrand
 * @author Shahar Nussbaum
 */
public class BidiMessagingProtocolTFTP implements BidiMessagingProtocol<Message> {

    private int connectionId;
    private ConnectionsImpl<Message> connections;
    private boolean shouldClose = false;
    static private final ConcurrentHashMap<Integer, String> loggedInUsers = new ConcurrentHashMap<>();
    static private final ConcurrentHashMap<String, AtomicInteger> filesInUse = new ConcurrentHashMap<>();
    private boolean loggedIn = false;
    private short lastOp;
    private File file;
    private byte[] bytes;
    private byte[] blob = new byte[512];
    private int blobLen;
    private FileInputStream is = null;
    private FileOutputStream os = null;
    private long dataBlocksNeeded = 0;


    @Override
    public void start(int connectionId, Connections<Message> connections) {
        this.connectionId = connectionId;
        this.connections = (ConnectionsImpl<Message>) connections;
    }

    @Override
    public void process(Message message) throws FileNotFoundException {
        short blockNum;
        DataMessage newData;
        short currentOpcode = message.getOpCode();
        if (loggedIn || currentOpcode == 7) {

            switch (currentOpcode) {
                case 1://RRQ
                    lastOp = 1;
                    String path = "Files/" + ((ReadWrite) message).getFilename();
                    file = new File(path);
                    if (file.exists() && !((ReadWrite) message).getFilename().equals("TempFiles")) {
                        dataBlocksNeeded = file.length() / 512 + 1;
                        String fileToRead = file.getName();
                        if (filesInUse.containsKey(fileToRead)) {
                            filesInUse.put(fileToRead, new AtomicInteger(filesInUse.get(fileToRead).incrementAndGet()));
                        } else {
                            filesInUse.put(fileToRead, new AtomicInteger(1));
                        }
                        is = new FileInputStream(file);
                        try {
                            if ((blobLen = is.read(blob)) != -1)
                                connections.send(connectionId, new DataMessage((short) blobLen, (short) 1, Arrays.copyOfRange(blob, 0, blobLen)));
                        } catch (IOException e) {
                            AtomicInteger uses = filesInUse.get(file.getName());
                            filesInUse.put(file.getName(), new AtomicInteger(uses.decrementAndGet()));
                            if (filesInUse.get(file.getName()).get() <= 0)
                                filesInUse.remove(fileToRead);
                            connections.send(connectionId, new Error((short) 2));//Access violation
                        }
                    } else {
                        connections.send(connectionId, new Error((short) 1));//file not found
                    }
                    break;
                case 2://WRQ
                    lastOp = 2;
                    file = new File("Files/" + ((ReadWrite) message).getFilename());
                    if (file.exists()) {//ERROR
                        connections.send(connectionId, new Error((short) 5));//file already exists
                    } else {
                        file = new File("Files/TempFiles/" + ((ReadWrite) message).getFilename());
                        try {
                            if (!file.exists()) {
                                file.createNewFile();
                                os = new FileOutputStream(file);
                                connections.send(connectionId, new Acknowledge((short) 0));
                            } else
                                connections.send(connectionId, new Error((short) 5));//file already exists
                        } catch (IOException e) {
                            connections.send(connectionId, new Error((short) 2));//Access violation
                        }
                    }
                    break;
                case 3://DATA from client
                    short dataSize = ((DataMessage) message).getDataSize();
                    byte[] data = ((DataMessage) message).getData();
                    blockNum = ((DataMessage) message).getBlockNum();
                    try {
                        os.write(data);
                        os.flush();
                        connections.send(connectionId, new Acknowledge(blockNum));
                    } catch (IOException e) {
                        connections.send(connectionId, new Error((short) 2));//Access violation
                    } finally {
                        try {
                            if (dataSize < 512) {
                                Files.move(file.toPath(), new File("Files/" + file.getName()).toPath());
                                broadcast(new Broadcast((byte) 1, file.getName()));
                                os.close();
                            }
                        } catch (IOException e) {
                            connections.send(connectionId, new Error((short) 2));//Access violation
                        }
                    }
                    break;
                case 4: // ACK
                    blockNum = ((Acknowledge) message).getBlockNum();
                    switch (lastOp) {
                        case 1://RRQ
                            if (blockNum < dataBlocksNeeded) {
                                try {
                                    if ((blobLen = is.read(blob)) != -1) {
                                        byte[] tempBlob = Arrays.copyOfRange(blob, 0, blobLen);
                                        connections.send(connectionId, new DataMessage((short) tempBlob.length, (short) (blockNum + 1), tempBlob));
                                    } else {
                                        if (file.length() % 512 == 0)
                                            connections.send(connectionId, new DataMessage((short) 0, (short) (blockNum + 1), new byte[0]));
                                        is = null;
                                    }
                                } catch (IOException e) {
                                    connections.send(connectionId, new Error((short) 2));
                                }
                            } else {
                                String fileToDelete = file.getName();
                                filesInUse.put(fileToDelete, new AtomicInteger(filesInUse.get(fileToDelete).decrementAndGet()));
                                if (filesInUse.get(fileToDelete).get() <= 0)
                                    filesInUse.remove(fileToDelete);
                                is = null;
                                file = null;
                            }
                            break;
                        case 6: //DIRQ
                            if (blockNum < dataBlocksNeeded) {
                                try {
                                    if (is != null && (blobLen = is.read(blob)) != -1) {
                                        byte[] tempBlob = Arrays.copyOfRange(blob, 0, blobLen);
                                        connections.send(connectionId, new DataMessage((short) tempBlob.length, (short) (blockNum + 1), tempBlob));
                                    } else {
                                        if (file != null && file.length() % 512 == 0)
                                            connections.send(connectionId, new DataMessage((short) 0, (short) (blockNum + 1), new byte[0]));
                                        is = null;
                                    }
                                } catch (IOException e) {
                                    connections.send(connectionId, new Error((short) 2));//Access violation
                                } catch (Exception e) {
                                    connections.send(connectionId, new Error((short) 0));//undefined Error
                                }
                            } else {
                                file.delete();
                                is = null;
                                os = null;
                                file = null;
                            }
                            break;
                    }
                    break;
                case 5://Error - upload/download failed in client side
                    System.out.println("Error received  from Client - error number: " + ((Error) message).getErrorCode() + " error message:" + ((Error) message).getErrorMsg());
                    if (lastOp == 2) {
                        try {
                            if (os != null)
                                os.close();
                            if (file != null && file.exists())
                                file.delete();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    file = null;
                    os = null;
                    is = null;
                    break;
                case 6: //DIRQ Packets req
                    file = new File("Files/TempFiles/" + connectionId + ".DIRQ");
                    os = new FileOutputStream(file);
                    lastOp = 6;
                    try {
                        for (String name : new File("Files/").list()) {
                            if (!name.equals("TempFiles")) {
                                try {
                                    os.write(name.getBytes());
                                    os.write((byte) '\0');
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }catch (Exception e){
                        connections.send(connectionId, new Error((short) 0));//undefined error
                    }
                    is = new FileInputStream(file);
                    bytes = new byte[file.length() > 512 ? 512 : (int) file.length()];
                    try {
                        is.read(bytes);
                    } catch (IOException e) {
                        connections.send(connectionId, new Error((short) 2));//Access violation
                    }
                    dataBlocksNeeded = file.length() / 512 + 1;
                    newData = new DataMessage((short) bytes.length, (short) 1, bytes);
                    connections.send(connectionId, newData);
                    break;
                case 7: //Login
                    if (logIn(((Login) message).getUsername())) {
                        connections.send(connectionId, new Acknowledge((short) 0));//
                    } else {
                        connections.send(connectionId, new Error((short) 7)); //User already logged in
                    }
                    break;
                case 8:
                    file = new File("Files/" + ((DeleteFile) message).getFilename());
                    if (file.exists()) {
                        if (!filesInUse.containsKey(file.getName())) {
                            file.delete();
                            connections.send(connectionId, new Acknowledge((short) 0)); //file deleted
                            broadcast(new Broadcast((byte) (0), ((DeleteFile) message).getFilename()));
                        } else {
                            connections.send(connectionId, new Error((short) 2)); //file in use
                        }
                    } else {
                        connections.send(connectionId, new Error((short) 1)); //file not found
                    }

                    break;
                case 10://DISC
                    connections.send(connectionId, new Acknowledge((short) 0)); //user disconnected
                    logout();
                    connections.disconnect(connectionId);
                    break;
                default:
                    connections.send(connectionId, new Error((short) 4)); //unknown opcode
                    break;
            }
        } else {
            if (currentOpcode < 1 || currentOpcode > 10)
                connections.send(connectionId, new Error((short) 4)); //unknown opcode
            else
                connections.send(connectionId, new Error((short) 6)); //User not logged in
        }
    }
    //logs user in
    private boolean logIn(String userName) {
        synchronized (loggedInUsers) {
            if (loggedInUsers.containsValue(userName)) {
                return false;
            } else {
                loggedInUsers.put(connectionId, userName);
                loggedIn = true;
                return true;
            }
        }
    }
    //logs user out
    private void logout() {
        synchronized (loggedInUsers) {
            loggedInUsers.remove(connectionId);
            loggedIn = false;
            shouldClose = true;
        }
    }
    //sends given message to all logged in clients
    private void broadcast(Broadcast msg) {
        for (Integer id : loggedInUsers.keySet())
            connections.send(id, msg);
    }

    @Override
    public boolean shouldTerminate() {
        return shouldClose;
    }

}
