package unet.uncentralized.jkademlia.Socket;

import unet.uncentralized.jkademlia.Node.Node;
import unet.uncentralized.jkademlia.Routing.Contact;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class KSocket extends Socket {

    public KSocket(){
        super();
    }

    public KSocket(Node n)throws IOException {
        super(n.getAddress(), n.getPort());
    }

    public KSocket(Contact c)throws IOException {
        super(c.getNode().getAddress(), c.getNode().getPort());
    }

    public KSocket(InetAddress address, int port)throws IOException {
        super(address, port);
    }

    @Override
    public DataInputStream getInputStream()throws IOException {
        return new DataInputStream(super.getInputStream());
    }

    @Override
    public DataOutputStream getOutputStream()throws IOException {
        return new DataOutputStream(super.getOutputStream());
    }

    @Override
    public void close(){
        try{
            if(!super.isInputShutdown()){
                super.shutdownInput();
            }

            if(!super.isOutputShutdown()){
                super.shutdownOutput();
            }

            super.close();
        }catch(IOException e){
            //e.printStackTrace();
        }
    }
}
