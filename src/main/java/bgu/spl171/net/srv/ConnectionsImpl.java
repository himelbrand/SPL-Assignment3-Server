package bgu.spl171.net.srv;

import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.srv.bidi.ConnectionHandler;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Shahar on 09/01/2017.
 */
public class ConnectionsImpl<T> implements Connections<T> {

    private ConcurrentHashMap<Integer,ConnectionHandler<T>> connectionHandlerList = new ConcurrentHashMap<>();
    private AtomicInteger newConnectionId = new AtomicInteger(0);

    @Override
    public boolean send(int connectionId, T msg) {
        if(connectionHandlerList.get(connectionId) != null) {
        //@@    System.out.println("sendddd");
            connectionHandlerList.get(connectionId).send(msg);
            return true;
        }
        System.out.println("not send");
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

    public void register(ConnectionHandler<T> handler){
        connectionHandlerList.put(newConnectionId.getAndIncrement(), handler);
    }
    public int getConnectionsID(){
        return newConnectionId.get();
    }
}

