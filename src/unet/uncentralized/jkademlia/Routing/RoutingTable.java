package unet.uncentralized.jkademlia.Routing;

import unet.uncentralized.jkademlia.Node.KComparator;
import unet.uncentralized.jkademlia.Node.KID;
import unet.uncentralized.jkademlia.Node.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class RoutingTable {

    private Contact local;
    private KBucket[] kbuckets;

    public RoutingTable(Node l){
        this.local = new Contact(l);

        kbuckets = new KBucket[KID.ID_LENGTH];
        for(int i = 0; i < KID.ID_LENGTH; i++){
            kbuckets[i] = new KBucket();
        }

        //insert(local); //V1 DOESN'T USE THIS
    }

    public Node getLocal(){
        return local.getNode();
    }

    public synchronized void insert(Contact c){
        if(!c.equals(local)){ //V1 USES USE THIS
            kbuckets[getBucketId(c.getNode().getKID())].insert(c);
        }
    }

    public synchronized void insert(Node n){
        insert(new Contact(n));
    }

    public synchronized void remove(Node n){
        kbuckets[getBucketId(n.getKID())].remove(n);
    }

    //MAYBE MODIFY TO NOT USE TREE_SET
    public synchronized List<Node> findClosest(KID k, int r){
        TreeSet<Node> sortedSet = new TreeSet<>(new KComparator(k));
        sortedSet.addAll(getAllNodes());

        List<Node> closest = new ArrayList<>(r);

        int count = 0;
        for(Node n : sortedSet){
            closest.add(n);
            if(count++ == r){
                break;
            }
        }
        return closest;
    }

    public synchronized List<Node> findClosestWithLocal(KID k, int r){
        TreeSet<Node> sortedSet = new TreeSet<>(new KComparator(k));
        sortedSet.addAll(getAllNodes());
        sortedSet.add(local.getNode());

        List<Node> closest = new ArrayList<>(r);

        int count = 0;
        for(Node n : sortedSet){
            closest.add(n);
            if(count++ == r){
                break;
            }
        }
        return closest;
    }

    public synchronized int getBucketSize(int i){
        return kbuckets[i].size();
    }

    public synchronized int getBucketId(KID k){
        int bid = local.getNode().getKID().getDistance(k)-1;
        return bid < 0 ? 0 : bid;
    }

    public synchronized List<Contact> getAllContacts(){
        ArrayList<Contact> r = new ArrayList<>();
        for(KBucket b : kbuckets){
            r.addAll(b.getContacts());
        }
        return r;
    }

    public synchronized List<Node> getAllNodes(){
        List<Node> nodes = new ArrayList<>();

        for(KBucket b : kbuckets){
            for(Contact c : b.getContacts()){
                nodes.add(c.getNode());
            }
        }

        return nodes;
    }

    //MY CODE
    public synchronized List<Contact> getAllUnqueriedNodes(){
        List<Contact> contacts = new ArrayList<>();

        for(KBucket b : kbuckets){
            contacts.addAll(b.getUnQueriedNodes());
        }

        return contacts;
    }
}
