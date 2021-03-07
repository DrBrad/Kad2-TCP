package unet.uncentralized.jkademlia.Node;

import java.math.BigInteger;
import java.util.Comparator;

public class KComparator implements Comparator<Node> {

    public final BigInteger key;

    public KComparator(KID key){
        this.key = key.getInt();
    }

    @Override
    public int compare(Node a, Node b){
        BigInteger b1 = a.getKID().getInt();
        BigInteger b2 = b.getKID().getInt();

        b1 = b1.xor(key);
        b2 = b2.xor(key);

        return b1.abs().compareTo(b2.abs());
    }
}
