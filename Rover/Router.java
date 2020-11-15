package Rover;

import java.io.*;
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

        Receiver r = new Receiver(this);
        r.start();
    }

    public Router(byte roverId, String filename, String destinationRouterIP) {
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

        Receiver r = new Receiver(this);
        r.start();

        File file = new File(filename);
        try {
            InputStream fileStream = new FileInputStream(file);
            byte[] fileByteArray = new byte[(int)file.length()];
            fileStream.read(fileByteArray);

            Sender s = new Sender(this, destinationRouterIP, fileByteArray);
            s.start();
        } catch (FileNotFoundException e) {
            System.out.println("Cannot find file: " + filename);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error reading file: " + filename);
            e.printStackTrace();
        }
    }
}
