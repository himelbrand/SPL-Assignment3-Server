package bgu.spl171.net.srv;

import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.srv.bidi.ConnectionHandler;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionsImpl<T> implements Connections<T> {

    private ConcurrentHashMap<Integer,ConnectionHandler<T>> connectionHandlerList = new ConcurrentHashMap<>();
    private AtomicInteger newConnectionId = new AtomicInteger(0);

    @Override
    public boolean send(int connectionId, T msg) {
        if(connectionHandlerList.get(connectionId) != null) {
            connectionHandlerList.get(connectionId).send(msg);
            return true;
        }
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
        }

    }

    /**
     * Register a new handler to connections, puts in hash map with unique ID
     * @param handler the handler to be registered
     */
    public void register(ConnectionHandler<T> handler){
        connectionHandlerList.put(newConnectionId.getAndIncrement(), handler);
    }

    /**
     * @return {@link ConnectionsImpl#newConnectionId} value of type int
     */
    public int getConnectionsID(){
        return newConnectionId.get();
    }
}

