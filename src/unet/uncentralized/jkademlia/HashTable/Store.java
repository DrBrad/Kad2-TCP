package unet.uncentralized.jkademlia.HashTable;

import unet.uncentralized.jkademlia.Node.KID;

public class Store {

    private long time;
    //private KID kid;
    private String data;

    public Store(String data){
        this.data = data;
        time = System.currentTimeMillis()/1000l;
    }

    /*
    public KID getKID(){
        return kid;
    }
    */

    public String getData(){
        return data;
    }

    public long getTime(){
        return time;
    }

    public void setTimeNow(){
        time = System.currentTimeMillis()/1000l;
    }
}
