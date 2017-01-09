package bgu.spl171.net.srv;

import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.srv.bidi.ConnectionHandler;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Shahar on 09/01/2017.
 */
public class ConnectionsImpl<T> implements Connections<T> {

    private HashMap<Integer,ConnectionHandler<T>> connectionHandelerList = new HashMap<>();

    @Override
    public boolean send(int connectionId, T msg) {
        if(connectionHandelerList.get(connectionId) != null) {
            connectionHandelerList.get(connectionId).send(msg);
            return true;
        }
        //connectionId not exist
        return false;
    }

    @Override
    public void broadcast(T msg) {
        for(ConnectionHandler<T> myConnectionHandler: connectionHandelerList.values()){
            myConnectionHandler.send(msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {
        try {
            connectionHandelerList.get(connectionId).close();
            connectionHandelerList.remove(connectionId);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Socket close throw exception -- needs to figure out");
        }

    }
}
