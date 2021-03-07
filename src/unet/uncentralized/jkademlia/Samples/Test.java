package unet.uncentralized.jkademlia.Samples;

import unet.uncentralized.jkademlia.KademliaNode.KademliaNode;

public class Test {

    public static void main(String[] args){
        try{
            //CREATE THE INITIAL NODE
            KademliaNode s = new KademliaNode(8080);

            Thread.sleep(1000); //WE NEED TO DELAY TO LET THE INITIAL NODE START

            //NOW WE CREATE THE SECOND NODE AND ALLOW MAKE IT JOIN/BOOTSTRAP TO THE SECOND NODE
            KademliaNode a = new KademliaNode(8070);
            a.join(s.getRoutingTable().getLocal());

            KademliaNode b = new KademliaNode(8060);
            b.join(s.getRoutingTable().getLocal());

            //SLEEP AGAIN TO ENSURE ALL NODES ARE CONNECTED
            Thread.sleep(1000);

            //SEE THE ROUTES
            System.out.println("S: "+s.toString());
            System.out.println("A: "+a.toString());
            System.out.println("B: "+b.toString());

            //KILL ALL OF THE NODES
            s.gracefulClose();
            a.gracefulClose();
            b.gracefulClose();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
