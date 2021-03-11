package unet.uncentralized.jkademlia.KademliaNode;

import org.xml.sax.SAXException;
import unet.uncentralized.jkademlia.HashTable.Storage;
import unet.uncentralized.jkademlia.Message.*;
import unet.uncentralized.jkademlia.Node.KID;
import unet.uncentralized.jkademlia.Node.Node;
import unet.uncentralized.jkademlia.Routing.Contact;
import unet.uncentralized.jkademlia.Routing.KBucket;
import unet.uncentralized.jkademlia.Routing.RoutingTable;
import unet.uncentralized.jkademlia.Socket.KServerSocket;
import unet.uncentralized.jkademlia.Socket.KSocket;
import unet.uncentralized.jkademlia.Socket.UPnP.UPnP;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static unet.uncentralized.jkademlia.Socket.FallbackResolver.*;

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

    public KademliaNode(int port, boolean local)throws Exception {
        this.port = port;

        if(local){
            routingTable = new RoutingTable(new Node(getLocalIP(), port));

        }else{ //EXTERNAL IP RESOLUTION
            if(!UPnP.isUPnPAvailable()){
                routingTable = new RoutingTable(new Node(UPnP.getExternalIP(), port));

                if(!UPnP.isMappedTCP(port)){
                    UPnP.openPortTCP(port);
                }

            }else{
                try{
                    routingTable = new RoutingTable(new Node(getExternalIP(), port));
                }catch(IOException | ParserConfigurationException | SAXException e){
                    e.printStackTrace();
                }
            }
        }

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

                    storage.evict();

                    final List<String> data = storage.getRenewal();
                    if(!data.isEmpty()){
                        for(final String r : data){
                            exe.submit(new Runnable(){
                                @Override
                                public void run(){
                                    try{
                                        new StoreMessage(KademliaNode.this, r).execute();
                                    }catch(NoSuchAlgorithmException e){
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
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

        return routingTable.getLocal().getPort()+"   "+builder;
    }
}
