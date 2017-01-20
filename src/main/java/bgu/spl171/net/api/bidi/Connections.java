package bgu.spl171.net.api.bidi;



public interface Connections<T> {
    /**
     * sends message to a certain connection by ID
     * @param connectionId the wanted connection ID for this message
     * @param msg the message being sent
     * @return boolean was the send successful
     */
    boolean send(int connectionId, T msg);

    /**
     * sends a message to all connected clients
     * @param msg
     */
    void broadcast(T msg);

    /**
     * @param connectionId the ID that is to be disconnected from connections
     */
    void disconnect(int connectionId);
}
