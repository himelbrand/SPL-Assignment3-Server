/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl171.net.srv.bidi;

import java.io.Closeable;
import java.io.IOException;

/**
 * This is the interface implemented by {@link bgu.spl171.net.srv.reactorSrv.NonBlockingConnectionHandler} and {@link bgu.spl171.net.srv.baseServerSrv.BlockingConnectionHandler}
 * @author Omri Himelbrand
 * @author Shahar Nussbaum
 */
public interface ConnectionHandler<T> extends Closeable{
    /**
     * sends a given message
     * @param msg the given message
     */
    void send(T msg) ;

}
