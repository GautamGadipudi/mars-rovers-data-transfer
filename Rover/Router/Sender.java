package Rover.Router;

import Rover.Router.GGP.Header;
import Rover.Router.GGP.Packet;

import java.io.IOException;
import java.net.*;

public class Sender extends Thread {
    DatagramSocket socket;
    Router router;
    String destinationRouterIP;
    InetAddress destinationAddress;
    byte[] fileContent;

    public Sender(Router router, String destinationRouterIP, byte[] fileContent) {
        this.router = router;
        this.destinationRouterIP = destinationRouterIP;
        this.fileContent = fileContent;

        try {
            this.destinationAddress = InetAddress.getByName(this.destinationRouterIP);
        } catch (UnknownHostException e) {
            System.out.println("SENDER: Unable to resolve IP: " + this.destinationRouterIP);
            e.printStackTrace();
        }

        try {
            this.socket = new DatagramSocket(router.routerConfig.fileExchangePort);
        } catch (SocketException e) {
            System.out.println("SENDER: Unable to create socket.");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte seqNum = 0;
        int ackNum = 0;

        boolean isLast = false;
        for (int i = 0; i < this.fileContent.length; i += 1016) {
            seqNum += 1;

            // Check if last segment
            if (i + 1016 >= fileContent.length)
                isLast = true;

            // Create data byte array accordingly (if last segment or not)
            byte[] data = new byte[1016];
            if (isLast) {
                for (int j = 0; j < fileContent.length - i; j++) {
                    data[j] = fileContent[i + j];
                }
            }
            else {
                for (int j = 0; j < 1016; j++) {
                    data[j] = fileContent[i + j];
                }
            }

            Header Header = new Header(router.routerConfig.Address, seqNum, isLast, Byte.parseByte(destinationRouterIP.split("\\.")[2]));
            Packet packet = null;
            try {
                packet = new Packet(Header, data);
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] packetByteArray = packet.getByteArray();
            DatagramPacket senderDatagramPacket = new DatagramPacket(packetByteArray, packetByteArray.length, destinationAddress, router.routerConfig.fileExchangePort);
            try {
                socket.send(senderDatagramPacket);
            } catch (IOException e) {
                System.out.println("SENDER: Unable to send!");
                e.printStackTrace();
            }
        }
    }
}
