JKademlia
========

This is an implementation of Kademlia DHT [Wikipedia Link](http://en.wikipedia.org/wiki/Kademlia) this implementation was refrenced from [Stanford Paper](https://codethechange.stanford.edu/guides/guide_kademlia.html)

Note: This repository is an IntelliJ project.

Features
-----
- [x] PING
- [x] FIND NODE
- [x] FIND VALUE
- [x] STORE
- [X] External IP resolution over fallback nodes, websites and UPnP

Usage
-----
Here is an example for the routing and creation of a couple DHT nodes. [Example](https://github.com/DrBrad/JKademlia/blob/main/src/unet/uncentralized/jkademlia/Samples/Test.java)

**Configuration**
-----
Download [Jar Library Download](https://github.com/DrBrad/JKademlia/blob/main/out/artifacts/JKademlia_jar/JKademlia.jar) and include the library to your project.

**Creating a node**
```Java
int port = 8080;
boolean local = true;
KademliaNode knode = new KademliaNode(port, local);
```

**Joining / Bootstrap to node**
```Java
int port = 8070;
int toPort = 8080;
boolean local = true;
KademliaNode knode = new KademliaNode(port, local);
knode.join(InetAddress.getLocalHost(), toPort);
```

**Storing data on DHT**
```Java
KademliaNode knode = new KademliaNode(port, local);
knode.store("STRING DATA");
```

**Getting data on DHT**
```Java
KademliaNode knode = new KademliaNode(port, local);
knode.get(new KID("STRING DATA"));
```


License
-----------
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR ANYONE DISTRIBUTING THE SOFTWARE BE LIABLE FOR ANY DAMAGES OR OTHER LIABILITY, WHETHER IN CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE
