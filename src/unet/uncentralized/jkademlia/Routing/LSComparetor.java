package unet.uncentralized.jkademlia.Routing;

import java.util.Comparator;

public class LSComparetor implements Comparator<Contact> {

    @Override
    public int compare(Contact a, Contact b){
        if(a.hashCode() == (b.hashCode())){
            return 0;
        }else{
            return (a.getLastSeen() > b.getLastSeen()) ? 1 : -1;
        }
    }
}
