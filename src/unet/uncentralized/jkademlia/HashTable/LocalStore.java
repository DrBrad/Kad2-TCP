package unet.uncentralized.jkademlia.HashTable;

public class LocalStore {

    private long time;
    private String data;

    public LocalStore(String data){
        this.data = data;
        time = System.currentTimeMillis()/1000l;
    }

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
