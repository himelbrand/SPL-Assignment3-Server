package bgu.spl171.net.api.bidi;

import java.io.FileNotFoundException;

public interface BidiMessagingProtocol<T>  {
    /**
     * starts the protocol for the connection ID
     * @param connectionId the ID of the connection handler
     * @param connections the connections associated with this protocol
     */
    void start(int connectionId, Connections<T> connections);

    /**
     * processes a message
     * @param message the message to be processed
     * @throws FileNotFoundException if file not found
     */
    void process(T message) throws FileNotFoundException;
	
	/**
     * @return true if the connection should be terminated
     */
    boolean shouldTerminate();
}
