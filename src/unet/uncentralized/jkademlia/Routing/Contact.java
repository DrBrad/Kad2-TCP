package unet.uncentralized.jkademlia.Routing;

import unet.uncentralized.jkademlia.Node.Node;

import java.util.Date;

public class Contact/* implements Comparable<Contact>*/ {

    private Node n;
    private int s;
    private long t;

    public Contact(Node n){
        this.n = n;
        t = new Date().getTime();
    }

    public Node getNode(){
        return n;
    }

    public void setSeenNow(){
        this.t = new Date().getTime();
    }

    public long getLastSeen(){
        return t;
    }

    public void markStale(){
        s++;
    }

    public void resetStale(){
        s = 0;
    }

    public int getStaleCount(){
        return s;
    }

    public boolean isQueried(long now){
        if(t+3600000 < now){
            return true;
        }

        return false;
    }

    /*
    @Override
    public int compareTo(Contact c){
        if(n.hash().equals(c.hash())){
            return 0;
        }

        return (t > c.getLastSeen()) ? 1 : -1;
    }
    */

    public String hash(){
        return n.hash();
    }

    public boolean equals(Object o){
        if(o instanceof Contact){
            return n.hash().equals(((Contact) o).hash());
        }

        return false;
    }
}
