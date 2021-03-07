package unet.uncentralized.jkademlia.Socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;

public class KServerSocket extends ServerSocket {

    public KServerSocket(int port)throws IOException {
        super(port);
    }

    @Override
    public KSocket accept()throws IOException {
        if(isClosed()){
            throw new SocketException("Socket is closed");
        }
        if(!isBound()){
            throw new SocketException("Socket is not bound yet");
        }

        KSocket socket = new KSocket();
        implAccept(socket);
        return socket;
    }

    @Override
    public void close(){
        try{
            super.close();
        }catch(Exception e){
            //e.printStackTrace();
        }
    }
}
