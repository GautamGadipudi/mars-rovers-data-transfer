package Rover.Router;

import Rover.Router.GGP.Packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Receiver extends Thread {
    DatagramSocket socket;
    Router router;

    public Receiver(Router router) {
        this.router = router;

        try {
            this.socket = new DatagramSocket(router.routerConfig.fileExchangePort);
        } catch (SocketException e) {
            System.out.println("RECEIVER: Unable to create socket.");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            DatagramPacket receiveDatagramPacket = new DatagramPacket(new byte[1024], 1024);
            try {
                socket.receive(receiveDatagramPacket);
            } catch (IOException e) {
                System.out.println("RECEIVER: Unable to receive.");
                e.printStackTrace();
            }

            byte[] data = receiveDatagramPacket.getData();
            Packet Packet = new Packet(data);
            processPacket(Packet);
        }
    }

    public void processPacket(Packet packet) {

    }
}
