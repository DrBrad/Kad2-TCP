package unet.uncentralized.jkademlia.Node;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;

public class Node {

    private KID kid;
    private InetAddress address;
    private int port;

    public Node(KID kid, InetAddress address, int port){
        this.kid = kid;
        this.address = address;
        this.port = port;
    }

    public Node(byte[] k, InetAddress address, int port)throws NoSuchAlgorithmException {
        kid = new KID(k);
        this.address = address;
        this.port = port;
    }

    public Node(InetAddress address, int port)throws NoSuchAlgorithmException {
        this.address = address;
        this.port = port;
        kid = new KID(address, port);
    }

    public Node(DataInputStream in)throws IOException {
        fromStream(in);
    }

    public KID getKID(){
        return kid;
    }

    public InetAddress getAddress(){
        return address;
    }

    public int getPort(){
        return port;
    }

    public void toStream(DataOutputStream out)throws IOException {
        out.write(kid.getBytes());

        if(address instanceof Inet4Address){
            out.writeByte(0x04);

        }else if(address instanceof Inet6Address){
            out.writeByte(0x06);
        }

        out.write(address.getAddress());
        out.writeInt(port);
    }

    public void fromStream(DataInputStream in)throws IOException {
        byte[] bid = new byte[KID.ID_LENGTH/8];
        in.read(bid);

        byte[] buffer = null;

        switch(in.readByte()){
            case 0x04:
                buffer = new byte[4];
                break;

            case 0x06:
                buffer = new byte[16];
                break;
        }

        in.read(buffer);

        kid = new KID(bid);
        address = InetAddress.getByAddress(buffer);
        port = in.readInt();
    }

    public int hashCode(){
        return 0;
    }

    public String hash(){
        return kid.getHex()+"|"+address.getHostName()+":"+port;
    }

    public boolean equals(Object o){
        if(o instanceof Node){
            return hash().equals(((Node) o).hash());
        }

        return false;
    }
}
