package unet.uncentralized.jkademlia.Node;

import java.math.BigInteger;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;

public class KID {

    private byte[] bid;
    public static final int ID_LENGTH = 512;

    public KID(byte[] bid){
        this.bid = bid;
        if(bid.length != ID_LENGTH/8){
            throw new IllegalArgumentException("Byte id given must be "+(ID_LENGTH/8)+" bytes.");
        }
    }

    public KID(String k)throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        bid = messageDigest.digest(k.getBytes());
    }

    public KID(InetAddress address, int port)throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        bid = messageDigest.digest((address.getHostAddress()+":"+port).getBytes());
    }

    public int getDistance(KID k){
        return ID_LENGTH-xor(k).getFirstSetBitIndex();
    }

    public KID xor(KID k){
        byte[] distance = new byte[ID_LENGTH/8];

        for(int i = 0; i < ID_LENGTH/8; i++){
            distance[i] = (byte) (bid[i]^k.getBytes()[i]);
        }
        return new KID(distance);
    }

    public int getFirstSetBitIndex(){
        int prefixLength = 0;

        for(byte b : bid){
            if(b == 0){
                prefixLength += 8;
            }else{
                int count = 0;
                for(int i = 7; i >= 0; i--){
                    if((b & (1 << i)) == 0){
                        count++;
                    }else{
                        break;
                    }
                }

                prefixLength += count;
                break;
            }
        }
        return prefixLength;
    }

    public KID generateNodeIdByDistance(int distance){
        byte[] result = new byte[ID_LENGTH/8];

        int numByteZeroes = (ID_LENGTH-distance)/8;
        int numBitZeroes = 8-(distance%8);

        for (int i = 0; i < numByteZeroes; i++){
            result[i] = 0;
        }

        BitSet bits = new BitSet(8);
        bits.set(0, 8);

        for(int i = 0; i < numBitZeroes; i++){
            bits.clear(i);
        }
        bits.flip(0, 8);
        result[numByteZeroes] = bits.toByteArray()[0];

        for(int i = numByteZeroes+1; i < result.length; i++){
            result[i] = Byte.MAX_VALUE;
        }

        return xor(new KID(result));
    }

    public byte[] getBytes(){
        return bid;
    }

    public String getBinary(){
        StringBuilder sb = new StringBuilder();
        for(byte b : bid){
            for(int i = 7; i >= 0; i--){
                sb.append(b >>> i & 1);
            }
        }

        return sb.toString();
    }

    public String getHex(){
        return String.format("%0"+(bid.length << 1)+"X", new BigInteger(1, bid));
    }

    public BigInteger getInt(){
        return new BigInteger(1, bid);
    }
}
