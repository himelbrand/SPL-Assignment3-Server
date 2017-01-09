package bgu.spl171.net.srv;

import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.srv.bidi.ConnectionHandler;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Shahar on 09/01/2017.
 */
public class ConnectionsImpl<T> implements Connections<T> {

    ArrayList<ConnectionHandler> connectionHandelerList = new ArrayList<>();

    @Override
    public boolean send(int connectionId, T msg) {

        return false;
    }

    @Override
    public void broadcast(T msg) {

    }

    @Override
    public void disconnect(int connectionId) {

    }
}
