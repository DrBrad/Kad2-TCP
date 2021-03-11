package unet.uncentralized.jkademlia.HashTable;

import unet.uncentralized.jkademlia.Node.KID;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Storage {

    private HashMap<String, Store> storage = new HashMap<>();
    private HashMap<String, LocalStore> localStorage = new HashMap<>();

    public synchronized void put(KID k, String v){
        if(storage.containsKey(k.getHex())){
            storage.get(k.getHex()).setTimeNow();

        }else{
            storage.put(k.getHex(), new Store(v));
        }
    }

    public synchronized void putLocal(KID k, String v){
        if(localStorage.containsKey(k.getHex())){
            localStorage.get(k.getHex()).setTimeNow();

        }else{
            localStorage.put(k.getHex(), new LocalStore(v));
        }
    }

    public synchronized boolean has(KID k){
        return (localStorage.containsKey(k.getHex()) || storage.containsKey(k.getHex())) ? true : false;
    }

    public synchronized String get(KID k){
        if(storage.containsKey(k.getHex())){
            return storage.get(k.getHex()).getData();

        }else if(localStorage.containsKey(k.getHex())){
            return storage.get(k.getHex()).getData();
        }
        return null;
    }

    /*
    public synchronized void handover(KID k){
        for(String kd : storage.keySet()){
            if(k.getDistance(storage.get(kd).getKID()) < /*MY ID*./){

            }
        }
    }
    */

    public synchronized void evict(){
        long now = new Date().getTime();

        for(String k : storage.keySet()){
            if(storage.get(k).getTime() < now){
                storage.remove(k);
            }
        }
    }

    public synchronized List<String> getRenewal(){
        ArrayList<String> ls = new ArrayList<>();
        if(localStorage.isEmpty()){
            return ls;
        }

        long now = new Date().getTime();

        for(String k : localStorage.keySet()){
            if(localStorage.get(k).getTime() < now){
                ls.add(localStorage.get(k).getData());
            }
        }

        return ls;
    }
}
