package unet.uncentralized.jkademlia.KademliaNode;

import unet.uncentralized.jkademlia.Node.KID;
import unet.uncentralized.jkademlia.Node.Node;
import unet.uncentralized.jkademlia.Routing.KBucket;
import unet.uncentralized.jkademlia.Socket.KSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class Receiver extends Thread {

    private KademliaNode kad;

    private KSocket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private byte v;

    public Receiver(KademliaNode kad, KSocket socket){
        this.kad = kad;
        this.socket = socket;
    }

    @Override
    public void run(){
        try{
            in = socket.getInputStream();
            out = socket.getOutputStream();

            v = in.readByte();

            switch(in.readByte()){
                case 0x00: //PING
                    ping();
                    break;

                case 0x01: //FIND_NODE
                    findNode2();
                    break;

                case 0x02: //FIND_VALUE
                    findValue();
                    break;

                case 0x03: //STORE
                    store();
                    break;
            }

        }catch(IOException e){
            e.printStackTrace();
        }finally{
            socket.close();
        }
    }

    private void ping()throws IOException {
        if(v == 0x00){
            Node n = new Node(in);
            kad.getRoutingTable().insert(n);

            out.writeByte(0x00); //SEND PONG

            kad.startRefresh(); //INIT BUCKET REFRESH
        }
    }

    private void findNode2()throws IOException {
        if(v == 0x00){
            Node s = new Node(in);

            //READ THE KEY IN QUESTION
            byte[] bid = new byte[KID.ID_LENGTH/8];
            in.read(bid);
            KID k = new KID(bid);

            //NOW FIND THE CLOSEST
            List<Node> nodes = kad.getRoutingTable().findClosestWithLocal(k, KBucket.MAX_BUCKET_SIZE);

            if(nodes.size() < 2){
                out.writeInt(0);

            }else{
                out.writeInt(nodes.size());
                for(Node n : nodes){
                    n.toStream(out);
                }
            }

            kad.getRoutingTable().insert(s);

            kad.startRefresh(); //INIT BUCKET REFRESH
        }
    }

    //OLD METHOD
    private void findNode()throws IOException {
        if(v == 0x00){
            Node s = new Node(in);

            //READ THE KEY IN QUESTION
            byte[] bid = new byte[KID.ID_LENGTH/8];
            in.read(bid);
            KID k = new KID(bid);

            //NOW FIND THE CLOSEST
            List<Node> nodes = kad.getRoutingTable().findClosestWithLocal(k, KBucket.MAX_BUCKET_SIZE);

            out.writeInt(nodes.size());
            for(Node n : nodes){
                n.toStream(out);
            }

            kad.getRoutingTable().insert(s);

            kad.startRefresh(); //INIT BUCKET REFRESH
        }
    }


    private void findValue()throws IOException {
        if(v == 0x00){
            byte[] bid = new byte[KID.ID_LENGTH/8];
            in.read(bid);
            KID k = new KID(bid);

            if(kad.getStorage().has(k)){

                String v = kad.getStorage().get(k);
                //if(v == null){
                //    out.writeByte(0x01);

                //}else{
                    out.writeByte(0x00);
                    out.writeInt(v.getBytes().length);
                    out.write(v.getBytes());
                //}

            }else{
                out.writeByte(0x01);
            }

        }
    }

    private void store()throws IOException {
        if(v == 0x00){
            byte[] bid = new byte[KID.ID_LENGTH/8];
            in.read(bid);
            KID k = new KID(bid);

            byte[] buffer = new byte[in.readInt()];
            in.read(buffer);

            kad.getStorage().put(k, new String(buffer));
        }
    }
}
