JKademlia
========

This is an implementation of Kademlia DHT (http://en.wikipedia.org/wiki/Kademlia) this implementation was refrenced from (https://codethechange.stanford.edu/guides/guide_kademlia.html)

Note: This repository is an IntelliJ project.

Features
-----
- [x] PING
- [x] FIND NODE
- [x] FIND VALUE
- [x] STORE
- [ ] HANDOVERS - Not in paper but might be a nice thing to add.

Usage
-----
Here is an example for the routing and creation of a couple DHT nodes. (https://github.com/DrBrad/JKademlia/blob/main/src/unet/uncentralized/jkademlia/Samples/Test.java)

**Configuration**
Download (https://github.com/DrBrad/JKademlia/blob/main/out/artifacts/JKademlia_jar/JKademlia.jar) and include the library to your project.

**Creating a node**
```Java
int port = 8080;
KademliaNode knode = new KademliaNode(port);
```

**Joining / Bootstrap to node**
```Java
int port = 8070;
int toPort = 8080;
KademliaNode knode = new KademliaNode(port);
knode.join(InetAddress.getLocalHost(), toPort);
```

**Storing data on DHT**
```Java
KademliaNode knode = new KademliaNode(port);
knode.store("STRING DATA");
```

**Getting data on DHT**
```Java
KademliaNode knode = new KademliaNode(port);
knode.get(new KID("STRING DATA"));
```


License
-----------
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR ANYONE DISTRIBUTING THE SOFTWARE BE LIABLE FOR ANY DAMAGES OR OTHER LIABILITY, WHETHER IN CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE
