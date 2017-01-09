package bgu.spl171.net.srv.baseServerSrv;

import bgu.spl171.net.api.MessageEncoderDecoder;
import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.srv.ConnectionsImpl;
import bgu.spl171.net.srv.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;

public abstract class BaseServer<T> implements Server<T> {

    private final int port;
    private final Supplier<BidiMessagingProtocol<T>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<T>> encdecFactory;
    private ServerSocket sock;
    private int i =0;

    public BaseServer(
            int port,
            Supplier<BidiMessagingProtocol<T>> protocolFactory,
            Supplier<MessageEncoderDecoder<T>> encdecFactory) {

        this.port = port;
        this.protocolFactory = protocolFactory;
        this.encdecFactory = encdecFactory;
		this.sock = null;
    }

    @Override
    public void serve() {

        try (ServerSocket serverSock = new ServerSocket(port)) {

            this.sock = serverSock; //just to be able to close

            ConnectionsImpl<T> myConnections = new ConnectionsImpl<>();

            while (!Thread.currentThread().isInterrupted()) {

                Socket clientSock = serverSock.accept();

                BidiMessagingProtocol<T> tempProtocol = protocolFactory.get();
                BlockingConnectionHandler<T> handler = new BlockingConnectionHandler<T>(
                        clientSock,
                        encdecFactory.get(),
                        tempProtocol);

                myConnections.register(handler,tempProtocol);
                execute(handler);
            }
        } catch (IOException ex) {

        }

        System.out.println("server closed!!!");
    }

    @Override
    public void close() throws IOException {
		if (sock != null)
			sock.close();
    }

    protected abstract void execute(BlockingConnectionHandler<T>  handler);

}
