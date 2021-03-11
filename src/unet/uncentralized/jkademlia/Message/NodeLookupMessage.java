package unet.uncentralized.jkademlia.Message;

import unet.uncentralized.jkademlia.Node.KID;
import unet.uncentralized.jkademlia.Node.Node;
import unet.uncentralized.jkademlia.Routing.RoutingTable;
import unet.uncentralized.jkademlia.Socket.KSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class NodeLookupMessage implements Message {

    private RoutingTable r;
    private List<Node> nodes;
    private KID k;

    public NodeLookupMessage(RoutingTable r, List<Node> nodes, KID k){
        this.r = r;
        this.nodes = nodes;
        this.k = k;
    }

    @Override
    public void execute(){
        ArrayList<Node> blacklist = new ArrayList<>();
        blacklist.add(r.getLocal());

        for(int i = 0; i < nodes.size(); i++){
            Node n = nodes.get(i);//iterator.next();

            if(!blacklist.contains(n)){ //NO NEED TO LOOKUP OUR OWN ROUTING TABLE...
                NodeResponse nr = connect(n);

                if(nr.hasConnected()){
                    if(nr.getNodes().isEmpty()){
                        break;

                    }else if(n.equals(nr.getNodes().get(0))){
                        for(Node m : nr.getNodes()){
                            if(!blacklist.contains(n)){ //NO NEED TO PING OURSELVES...
                                new PingMessage(r, m).execute();
                            }
                        }
                        break;
                    }

                    nodes = nr.getNodes();
                    i = -1;

                }else{
                    blacklist.add(n);
                    nodes.remove(n);
                }
            }
        }
    }

    public List<Node> get(){
        return nodes;
    }

    private NodeResponse connect(Node n){
        NodeResponse nr = new NodeResponse();

        KSocket socket = null;
        try{
            socket = new KSocket(n);

            DataInputStream in = socket.getInputStream();
            DataOutputStream out = socket.getOutputStream();

            out.writeByte(0x00); //VERSION

            out.writeByte(0x01); //NODE LOOKUP CODE
            r.getLocal().toStream(out); //SEND MY NODE DETAILS
            out.write(k.getBytes()); //SEND KID TO LOOK FOR

            int t = in.readInt();
            if(t > 0){
                for(int j = 0; j < t; j++){
                    Node m = new Node(in);
                    if(!m.equals(r.getLocal())){
                        nr.addNode(m);
                    }
                }
            }

            nr.setConnected(true);

            r.insert(n);

        }catch(ConnectException e){
            r.remove(n);
        }catch(IOException | NoSuchAlgorithmException e){
            //e.printStackTrace();
        }finally{
            if(socket != null){
                socket.close();
            }
        }

        return nr;
    }
}

class NodeResponse {

    private boolean connected;
    private ArrayList<Node> nodes = new ArrayList<>();

    public void setConnected(boolean connected){
        this.connected = connected;
    }

    public boolean hasConnected(){
        return connected;
    }

    public ArrayList<Node> getNodes(){
        return nodes;
    }

    public void addNode(Node n){
        nodes.add(n);
    }

    public void removeNode(Node n){
        if(nodes.contains(n)){
            nodes.remove(n);
        }
    }
}
