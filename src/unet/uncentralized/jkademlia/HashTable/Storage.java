package unet.uncentralized.jkademlia.HashTable;

import unet.uncentralized.jkademlia.Node.KID;

import java.util.HashMap;
import java.util.Map;

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
        for(String k : storage.keySet()){
            if(storage.get(k).getTime()-86400000000l > 0){
                storage.remove(k);
            }
        }
    }

    public synchronized Map<String, LocalStore> getRenewal(){
        if(localStorage.isEmpty()){
            return localStorage;
        }

        HashMap<String, LocalStore> ls = new HashMap<>();

        for(String k : localStorage.keySet()){
            if(localStorage.get(k).getTime()-86400000000l > 0){
                ls.put(k, localStorage.get(k));
            }
        }

        return ls;
    }
}
