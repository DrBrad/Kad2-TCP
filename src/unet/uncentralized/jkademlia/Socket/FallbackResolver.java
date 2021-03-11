package unet.uncentralized.jkademlia.Socket;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;
import java.util.Enumeration;

public class FallbackResolver {

    public static InetAddress getLocalIP(){
        try{
            Enumeration<NetworkInterface> b = NetworkInterface.getNetworkInterfaces();
            while(b.hasMoreElements()){
                for(InterfaceAddress f : b.nextElement().getInterfaceAddresses()){
                    if(f.getAddress().isSiteLocalAddress()){
                        return f.getAddress();
                    }
                }
            }
        }catch(SocketException e){
            e.printStackTrace();
        }
        return null;
    }

    public static InetAddress getExternalIP()throws IOException, ParserConfigurationException, SAXException {
        File inputFile = new File(FallbackResolver.class.getResource("/fallback.xml").getFile());

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();

        for(int i = 0; i < doc.getDocumentElement().getElementsByTagName("node").getLength(); i++){
            Element n = (Element) doc.getDocumentElement().getElementsByTagName("node").item(i);
            try{
                InetAddress externalIP = dhtResolve(InetAddress.getByName(n.getAttribute("ip")), Integer.parseInt(n.getAttribute("port")));
                if(externalIP != null){
                    return externalIP;
                }
            }catch(UnknownHostException e){
                e.printStackTrace();
            }
        }

        InetAddress externalIP = siteResolve();
        if(externalIP != null){
            return externalIP;
        }

        throw new UnknownHostException("Couldn't retrieve external IP address.");
    }

    private static InetAddress dhtResolve(InetAddress a, int p){
        KSocket k = null;
        try{
            k = new KSocket(a, p);
            DataInputStream in = k.getInputStream();
            DataOutputStream out = k.getOutputStream();

            out.writeByte(0x00); //WRITE VERSION
            out.writeByte(0x04); //GET MY EXTERNAL IP ADDRESS

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

            InetAddress externalIP = InetAddress.getByAddress(buffer);

            k.close();
            return externalIP;

        }catch(IOException e){
            //e.printStackTrace();
        }finally{
            if(k != null){
                k.close();
            }
        }

        return null;
    }

    private static InetAddress siteResolve(){
        String[] resolve = {
                "https://ipv4.icanhazip.com",
                "http://myexternalip.com/raw",
                "http://ipecho.net/plain",
                "http://checkip.amazonaws.com",
                "http://www.trackip.net/ip",
                "http://bot.whatismyipaddress.com",
                "http://icanhazip.com/",
                "http://myip.dnsomatic.com/"
        };

        for(String u : resolve){
            BufferedReader in = null;
            try{
                URL furl = new URL(u);
                in = new BufferedReader(new InputStreamReader(furl.openStream()));
                InetAddress externalIP = InetAddress.getByName(in.readLine());
                return externalIP;

            }catch(IOException e){
            }finally{
                if(in != null){
                    try{
                        in.close();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }

        return null;
    }
}
