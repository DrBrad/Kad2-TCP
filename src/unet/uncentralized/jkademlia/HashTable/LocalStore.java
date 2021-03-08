package unet.uncentralized.jkademlia.HashTable;

import java.util.Date;

public class LocalStore {

    private long time;
    private String data;

    public LocalStore(String data){
        this.data = data;
        time = new Date().getTime()+86400000;
    }

    public String getData(){
        return data;
    }

    public long getTime(){
        return time;
    }

    public void setTimeNow(){
        time = new Date().getTime()+86400000;
    }
}
