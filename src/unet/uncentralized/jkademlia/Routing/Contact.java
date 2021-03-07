package unet.uncentralized.jkademlia.Routing;

import unet.uncentralized.jkademlia.Node.Node;

public class Contact/* implements Comparable<Contact>*/ {

    private Node n;
    private int s;
    private long t;

    public Contact(Node n){
        this.n = n;
        t = System.currentTimeMillis()/1000l;
    }

    public Node getNode(){
        return n;
    }

    public void setSeenNow(){
        this.t = System.currentTimeMillis()/1000l;
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

    public boolean isQueried(){
        if(t-3600000000l < 0){
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
