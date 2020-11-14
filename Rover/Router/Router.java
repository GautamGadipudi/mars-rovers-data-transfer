package Rover.Router;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.StandardSocketOptions;

public class Router {
    byte id;
    RouterConfig routerConfig;
    MulticastSocket multicastSocket;

    public Router(byte roverId) {
        this.id = roverId;
        this.routerConfig = new RouterConfig(roverId);

        try {
            this.multicastSocket = new MulticastSocket(routerConfig.getMulticastPort());
            multicastSocket.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, false);
            multicastSocket.joinGroup(InetAddress.getByName(routerConfig.multicastIP));
        } catch (IOException e) {
            System.out.println("Unable to create multicast socket!");
            e.printStackTrace();
        }
    }
}
