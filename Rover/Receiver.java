package Rover;

import Rover.GGP.Header;
import Rover.GGP.Packet;

import java.io.*;
import java.net.*;
import java.nio.file.Files;

/**
 * Receiver.java
 *
 * Receiver class that is a thread. It is started for every rover.
 *
 * @author gautamgadipudi
 * @version 1.0
 * @since 11/13/2020
 */
public class Receiver extends Thread {
    DatagramSocket socket;
    Rover rover;

    File receivedFile;

    public Receiver(Rover rover) {
        this.rover = rover;

        try {
            this.socket = new DatagramSocket(rover.roverConfig.fileExchangeReceiverPort);
        } catch (SocketException e) {
            System.out.println("RECEIVER: Unable to create socket.");
            e.printStackTrace();
        }


        try {
            receivedFile = new File("received.txt");
            Files.deleteIfExists(receivedFile.toPath());
            receivedFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            DatagramPacket receiveDatagramPacket = new DatagramPacket(new byte[Packet.maximumSize], Packet.maximumSize);
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
                System.out.println("File: received.txt");
                socket.close();
                break;
            }
        }
    }

    /**
     * Process the GGP packet received.
     *
     * @param packet
     * @param senderAddress
     */
    public void processPacket(Packet packet, InetAddress senderAddress) {
        byte[] data = packet.getData();
        Header header = packet.getHeader();
        String message = new String(data);
        System.out.println("Received seq #" + header.getIdentifier());

        try(FileWriter fw = new FileWriter(receivedFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendAck(senderAddress, header.getIdentifier(), header.isLast());
    }

    /**
     * Send back acknowledgment to the sender
     *
     * @param destinationIP
     * @param ackNum
     * @param isLast
     */
    public void sendAck(InetAddress destinationIP, long ackNum, boolean isLast) {
        Header header = new Header(rover.roverConfig.Address, ackNum, isLast, (byte) 1);

        byte[] ackPacket = new Packet(header).getByteArray();

        try {
            DatagramPacket datagramPacket = new DatagramPacket(ackPacket, ackPacket.length, destinationIP, rover.roverConfig.fileExchangeSenderPort);
            this.socket.send(datagramPacket);
            System.out.println("Sent ack for seq. #" + ackNum);
        } catch (UnknownHostException e) {
            System.out.println("RECEIVER: Cannot resolve " + destinationIP);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("RECEIVER: Cannot send datagram packet");
            e.printStackTrace();
        }
    }
}
