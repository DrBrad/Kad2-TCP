package unet.uncentralized.jkademlia.Message;

import unet.uncentralized.jkademlia.Node.Node;
import unet.uncentralized.jkademlia.Routing.RoutingTable;
import unet.uncentralized.jkademlia.Socket.KSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;

public class PingMessage implements Message {

    private RoutingTable r;
    private Node n;

    public PingMessage(RoutingTable r, Node n){
        this.r = r;
        this.n = n;
    }

    @Override
    public void execute(){
        KSocket socket = null;
        try{
            socket = new KSocket(n);

            DataInputStream in = socket.getInputStream();
            DataOutputStream out = socket.getOutputStream();

            out.writeByte(0x00); //VERSION

            out.writeByte(0x00); //PING CODE
            r.getLocal().toStream(out); //SEND MY NODE DETAILS

            switch(in.readByte()){
                case 0x00: //CONFIRMED
                    r.insert(n); //INSERT TO ROUTING TABLE
                    break;

                default:
                    r.remove(n);
            }
        }catch(ConnectException e){
            r.remove(n);
        }catch(IOException e){
            //e.printStackTrace();
        }finally{
            if(socket != null){
                socket.close();
            }
        }
    }
}
