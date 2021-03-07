package unet.uncentralized.jkademlia.Message;

import unet.uncentralized.jkademlia.KademliaNode.KademliaNode;
import unet.uncentralized.jkademlia.Node.KID;
import unet.uncentralized.jkademlia.Node.Node;
import unet.uncentralized.jkademlia.Routing.KBucket;
import unet.uncentralized.jkademlia.Socket.KSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.util.List;

public class ValueLookupMessage implements Message {

    private KademliaNode kad;
    private KID k;
    private String v;

    public ValueLookupMessage(KademliaNode kad, KID k){
        this.kad = kad;
        this.k = k;
    }

    @Override
    public void execute(){
        List<Node> nodes = kad.getRoutingTable().findClosestWithLocal(k, KBucket.MAX_BUCKET_SIZE);

        if(kad.getStorage().has(k)){
            v = kad.getStorage().get(k);
            return;
        }

        if(!nodes.isEmpty()){
            if(!nodes.get(0).equals(kad.getRoutingTable().getLocal())){
            //    System.out.println("I AM THE KNOWN");
            //}else{
                NodeLookupMessage nm = new NodeLookupMessage(kad.getRoutingTable(), nodes, k);
                nm.execute();

                nodes = nm.get();

                if(nodes.isEmpty()){
                    return;
                }
            }

            if(nodes.contains(kad.getRoutingTable().getLocal())){
                nodes.remove(kad.getRoutingTable().getLocal());
            }

            kad.getStorage().putLocal(k, v);

            for(Node n : nodes){
                if(connect(n)){
                    break;
                }
            }

        }
    }

    public String get(){
        return v;
    }

    private boolean connect(Node n){
        KSocket socket = null;
        try{
            socket = new KSocket(n);

            DataInputStream in = socket.getInputStream();
            DataOutputStream out = socket.getOutputStream();

            out.writeByte(0x00); //VERSION
            out.writeByte(0x02); //STORE CODE

            out.write(k.getBytes());

            switch(in.readByte()){
                case 0x00:
                    byte[] buffer = new byte[in.readInt()];
                    in.read(buffer);
                    v = new String(buffer);

                    socket.close();
                    return true;

                default:
                    break;
            }


            //if(in.readByte() == 0x00){

            //}

            //r.insert(n);

        }catch(ConnectException e){
            //r.remove(n);
        }catch(IOException e){
            //e.printStackTrace();
        }finally{
            if(socket != null){
                socket.close();
            }
        }

        return false;
    }
}
