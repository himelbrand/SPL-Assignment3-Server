package bgu.spl171.net.srv;

import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.srv.bidi.ConnectionHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Shahar on 09/01/2017.
 */
public class ConnectionsImpl<T> implements Connections<T> {

    private HashMap<Integer,ConnectionHandler<T>> connectionHandlerList = new HashMap<>();
    private AtomicInteger newConnectionId = new AtomicInteger(0);

    private final CopyOnWriteArrayList<String> loggedIn = new CopyOnWriteArrayList<>();


    @Override
    public boolean send(int connectionId, T msg) {
        if(connectionHandlerList.get(connectionId) != null) {
            connectionHandlerList.get(connectionId).send(msg);
            return true;
        }
        //connectionId not exist
        return false;
    }

    @Override
    public void broadcast(T msg) {
        for(ConnectionHandler<T> myConnectionHandler: connectionHandlerList.values()){
            myConnectionHandler.send(msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {
        try {
            connectionHandlerList.get(connectionId).close();
            connectionHandlerList.remove(connectionId);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Socket close throw exception -- needs to figure out");
        }

    }

    public void register(ConnectionHandler<T> handler, BidiMessagingProtocol<T> tempProtocol){
        connectionHandlerList.put(newConnectionId.get(), handler);
        tempProtocol.start(newConnectionId.getAndIncrement(),this);

    }

    public boolean logIn(String userName){
        synchronized (loggedIn) {
            if (loggedIn.contains(userName)) {
                return false;
            } else {
                loggedIn.add(userName);
                return true;
            }
        }
        }
    }

