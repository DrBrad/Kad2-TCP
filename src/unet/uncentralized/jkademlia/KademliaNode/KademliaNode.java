package unet.uncentralized.jkademlia.KademliaNode;

import unet.uncentralized.jkademlia.HashTable.Storage;
import unet.uncentralized.jkademlia.Message.*;
import unet.uncentralized.jkademlia.Node.KID;
import unet.uncentralized.jkademlia.Node.Node;
import unet.uncentralized.jkademlia.Routing.Contact;
import unet.uncentralized.jkademlia.Routing.KBucket;
import unet.uncentralized.jkademlia.Routing.RoutingTable;
import unet.uncentralized.jkademlia.Socket.KServerSocket;
import unet.uncentralized.jkademlia.Socket.KSocket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KademliaNode {

    public static int THREAD_POOL_SIZE = 3;
    public static long BUCKET_REFRESH_TIME = 3600000;
    private int port;
    private RoutingTable routingTable;
    private Storage storage;
    private KServerSocket server;
    private ExecutorService exe;

    private Timer refreshTimer;
    private TimerTask refreshTimerTask;

    public KademliaNode(int port)throws UnknownHostException, NoSuchAlgorithmException {
        this.port = port;
        routingTable = new RoutingTable(new Node(InetAddress.getLocalHost(), port));
        storage = new Storage();
        exe = Executors.newFixedThreadPool(THREAD_POOL_SIZE); //SHOULD BE AROUND 3 ASYNC TASKS AS PAPER STATES
        bind();
    }

    public void join(Node n){
        new NodeLookupMessage(routingTable, Arrays.asList(n), routingTable.getLocal().getKID()).execute(); //INIT ROUTING TABLE BY JOINING NODE
        startRefresh(); //INIT BUCKET REFRESH
    }

    public void join(InetAddress address, int port)throws NoSuchAlgorithmException {
        join(new Node(address, port));
    }

    public void startRefresh(){
        if(refreshTimer == null && refreshTimerTask == null){
            refreshTimer = new Timer(true);
            refreshTimerTask = new TimerTask(){
                @Override
                public void run(){
                    //ASYNC OUR LOOKUP AND PING FOR REFRESH

                    for(int i = 1; i < KID.ID_LENGTH; i++){
                        if(routingTable.getBucketSize(i) < KBucket.MAX_BUCKET_SIZE){ //IF THE BUCKET IS FULL WHY SEARCH... WE CAN REFILL BY OTHER PEER PINGS AND LOOKUPS...
                            final KID k = routingTable.getLocal().getKID().generateNodeIdByDistance(i);

                            final List<Node> closest = routingTable.findClosest(k, KBucket.MAX_BUCKET_SIZE);
                            if(!closest.isEmpty()){
                                exe.submit(new Runnable(){
                                    @Override
                                    public void run(){
                                        new NodeLookupMessage(routingTable, closest, k).execute();
                                    }
                                });
                            }
                        }
                    }

                    exe.submit(new Runnable(){
                        @Override
                        public void run(){
                            List<Contact> contacts = routingTable.getAllUnqueriedNodes();
                            if(!contacts.isEmpty()){
                                for(Contact c : contacts){
                                    new PingMessage(routingTable, c.getNode()).execute();
                                }
                            }
                        }
                    });
                }
            };

            refreshTimer.schedule(refreshTimerTask, 0, BUCKET_REFRESH_TIME); //MAKE DELAY LONG, HOWEVER PERIOD AROUND 1 HOUR
        }
    }

    private void bind(){
        new Thread(new Runnable(){
            private KSocket socket;

            @Override
            public void run(){
                try{
                    server = new KServerSocket(port);

                    while((socket = server.accept()) != null){
                        new Receiver(KademliaNode.this, socket).start();
                    }
                }catch(IOException e){
                    //e.printStackTrace();
                }finally{
                    server.close();
                }
            }
        }).start();
    }

    public RoutingTable getRoutingTable(){
        return routingTable;
    }

    public Storage getStorage(){
        return storage;
    }

    public void store(String v)throws NoSuchAlgorithmException {
        new StoreMessage(this, v).execute();
    }

    public String get(KID k){
        ValueLookupMessage vl = new ValueLookupMessage(this, k);
        vl.execute();

        return vl.get();
    }

    public void gracefulClose(){
        refreshTimerTask.cancel();
        refreshTimer.cancel();
        refreshTimer.purge();
        exe.shutdown();
        server.close();
    }

    public void close(){
        refreshTimerTask.cancel();
        refreshTimer.cancel();
        refreshTimer.purge();
        exe.shutdownNow();
        server.close();
    }

    public String toString(){
        List<Node> nodes = routingTable.getAllNodes();
        String builder = "";
        for(Node n : nodes){
            builder += "   |   "+routingTable.getBucketId(n.getKID())+" : "+n.getPort();
        }

        /*
        builder += "           ";

        for(Node n : nodes){
            if(builder.split("\\| "+n.getPort()).length > 2){
                builder += " | M "+n.getPort();
            }
        }
        */

        return routingTable.getLocal().getPort()+"   "+builder;
    }
}
