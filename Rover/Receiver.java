package Rover;

import Rover.GGP.Header;
import Rover.GGP.Packet;

import java.io.IOException;
import java.net.*;

public class Receiver extends Thread {
    DatagramSocket socket;
    Router router;

    public Receiver(Router router) {
        this.router = router;

        try {
            this.socket = new DatagramSocket(router.routerConfig.fileExchangeReceiverPort);
        } catch (SocketException e) {
            System.out.println("RECEIVER: Unable to create socket.");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            DatagramPacket receiveDatagramPacket = new DatagramPacket(new byte[10000], 10000);
            try {
                socket.receive(receiveDatagramPacket);
            } catch (IOException e) {
                System.out.println("RECEIVER: Unable to receive.");
                e.printStackTrace();
            }

            byte[] data = receiveDatagramPacket.getData();
            Packet Packet = new Packet(data);
            processPacket(Packet, receiveDatagramPacket.getAddress());

            if (Packet.getHeader().isLast()) {
                System.out.println("FULL FILE RECEIVED!");
                socket.close();
                break;
            }
        }

        new Receiver(router).start();
    }

    public void processPacket(Packet packet, InetAddress senderAddress) {
        byte[] data = packet.getData();
        Header header = packet.getHeader();
        String message = new String(data);
        System.out.println("Received SEQ NUMBER: " + header.getIdentifier());
        System.out.println(message);
        System.out.println("******************************************");

        sendAck(senderAddress, header.getIdentifier(), header.isLast());
    }

    public void sendAck(InetAddress destinationIP, long ackNum, boolean isLast) {
        Header header = new Header(router.routerConfig.Address, ackNum, isLast, (byte) 1);

        byte[] ackPacket = new Packet(header).getByteArray();

        try {
            DatagramPacket datagramPacket = new DatagramPacket(ackPacket, ackPacket.length, destinationIP, router.routerConfig.fileExchangeSenderPort);
            this.socket.send(datagramPacket);
        } catch (UnknownHostException e) {
            System.out.println("RECEIVER: Cannot resolve " + destinationIP);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("RECEIVER: Cannot send datagram packet");
            e.printStackTrace();
        }
    }
}
