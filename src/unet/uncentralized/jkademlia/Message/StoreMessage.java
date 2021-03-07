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
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class StoreMessage implements Message {

    private KademliaNode kad;
    private KID k;
    private String v;

    public StoreMessage(KademliaNode kad, String v)throws NoSuchAlgorithmException {
        this.kad = kad;
        this.v = v;
        k = new KID(v);
    }

    @Override
    public void execute(){
        List<Node> nodes = kad.getRoutingTable().findClosestWithLocal(k, KBucket.MAX_BUCKET_SIZE);

        if(!nodes.isEmpty()){
            if(nodes.get(0).equals(kad.getRoutingTable().getLocal())){
                System.out.println("I AM THE KNOWN");

            }else{
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
                //SEND STORE
                connect(n);
            }

            System.out.println("SAVED");
        }else{
            System.out.println("FAILED TO SAVE");
        }


    }

    private void connect(Node n){
        KSocket socket = null;
        try{
            socket = new KSocket(n);

            DataInputStream in = socket.getInputStream();
            DataOutputStream out = socket.getOutputStream();

            out.writeByte(0x00); //VERSION
            out.writeByte(0x03); //STORE CODE

            out.write(k.getBytes());

            out.writeInt(v.getBytes().length);
            out.write(v.getBytes());

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
    }
}
