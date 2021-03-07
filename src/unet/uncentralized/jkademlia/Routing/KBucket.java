package unet.uncentralized.jkademlia.Routing;

import unet.uncentralized.jkademlia.Node.Node;

import java.util.ArrayList;
import java.util.List;

public class KBucket {

    private ArrayList<Contact> contacts, cache;
    public static int MAX_BUCKET_SIZE = 5, MAX_STALE = 1, MAX_CACHE_SIZE = 5;

    public KBucket(){
        contacts = new ArrayList<>();
        cache = new ArrayList<>();
    }

    public synchronized void insert(Contact c){
        if(contacts.contains(c)){
            c.setSeenNow();
            c.resetStale();

        }else if(contacts.size() >= MAX_BUCKET_SIZE){
            Contact s = null;

            for(Contact t : contacts){
                if(t.getStaleCount() > MAX_STALE){
                    if(t == null || t.getStaleCount() > s.getStaleCount()){
                        s = t;
                    }
                }
            }

            if(s != null){
                contacts.remove(s);
                contacts.add(c);

            }else{
                insertCache(c);
            }
        }else{
            contacts.add(c);
        }
    }

    public synchronized void insert(Node n){
        insert(new Contact(n));
    }

    private synchronized void insertCache(Contact c){
        if(cache.contains(c)){
            c.setSeenNow();
            c.resetStale();

        }else if(cache.size() >= MAX_CACHE_SIZE){
            cache.remove(cache.size()-1);
            cache.add(c);

        }else{
            cache.add(c);
        }
    }

    public synchronized boolean contains(Contact c){
        return contacts.contains(c);
    }

    public synchronized boolean contains(Node n){
        return contains(new Contact(n));
    }

    public synchronized void remove(Contact c){
        if(!contacts.contains(c)){
            return;
        }

        if(cache.isEmpty()){
            c.markStale();

        }else{
            contacts.remove(c);
            contacts.add(cache.get(0));
            cache.remove(0);
        }
    }

    public synchronized void remove(Node n){
        remove(new Contact(n));
    }

    public synchronized int size(){
        return contacts.size();
    }

    public synchronized int cacheSize(){
        return cache.size();
    }

    public synchronized List<Contact> getContacts(){
        contacts.sort(new LSComparetor());
        return contacts;
    }

    public synchronized List<Contact> getUnQueriedNodes(){
        ArrayList<Contact> q = new ArrayList<>();

        for(Contact c : contacts){
            if(!c.isQueried()){
                q.add(c);
            }
        }

        return q;
    }
}
