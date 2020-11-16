package Rover;

import Rover.GGP.Header;
import Rover.GGP.Packet;

import java.io.IOException;
import java.net.*;

/**
 * Sender.java
 *
 * Sender class that is a thread. It is started for a rover that wants to send data.
 *
 * @author gautamgadipudi
 * @version 1.0
 * @since 11/13/2020
 */
public class Sender extends Thread {
    DatagramSocket socket;
    Rover rover;
    String destinationRouterIP;
    InetAddress destinationAddress;
    byte[] fileContent;

    public Sender(Rover rover, String destinationRouterIP, byte[] fileContent) {
        this.rover = rover;
        this.destinationRouterIP = destinationRouterIP;
        this.fileContent = fileContent;

        try {
            this.destinationAddress = InetAddress.getByName(this.destinationRouterIP);
        } catch (UnknownHostException e) {
            System.out.println("SENDER: Unable to resolve IP: " + this.destinationRouterIP);
            e.printStackTrace();
        }

        try {
            this.socket = new DatagramSocket(rover.roverConfig.fileExchangeSenderPort);
        } catch (SocketException e) {
            System.out.println("SENDER: Unable to create socket.");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        long seqNum = 0;
        long ackNum = 0;

        boolean isLast = false;
        int maxDataLength = Packet.maximumSize - Header.size;
        for (int i = 0; i < this.fileContent.length; i += maxDataLength) {
            seqNum += 1;

            seqNum = seqNum % 256;

            // Check if last packet
            if (i + maxDataLength >= fileContent.length)
                isLast = true;

            // Create data byte array accordingly (if last segment or not)
            byte[] data = new byte[maxDataLength];
            if (isLast) {
                for (int j = 0; j < fileContent.length - i; j++) {
                    data[j] = fileContent[i + j];
                }
            }
            else {
                for (int j = 0; j < maxDataLength; j++) {
                    data[j] = fileContent[i + j];
                }
            }

            // Create GGP packet to send
            Header Header = new Header(rover.roverConfig.Address, seqNum, isLast, (byte)1);
            Packet packet = null;
            try {
                packet = new Packet(Header, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
            byte[] packetByteArray = packet.getByteArray();
            DatagramPacket sendDatagramPacket = new DatagramPacket(packetByteArray, packetByteArray.length, destinationAddress, rover.roverConfig.fileExchangeReceiverPort);

            try {
                System.out.println("Sending packet with seq. #" + seqNum);
                socket.send(sendDatagramPacket);
            } catch (IOException e) {
                System.out.println("SENDER: Unable to send!");
                e.printStackTrace();
            }

            boolean ackReceived = false;

            // Wait for ack corresponding to current seq. number to be received
            while (!ackReceived) {
                byte[] buffer = new byte[8];
                DatagramPacket ackPacket = new DatagramPacket(buffer, buffer.length);

                try {
                    socket.setSoTimeout(2000);
                    socket.receive(ackPacket);
                    Header ackHeader = new Header(buffer);

                    ackNum = ackHeader.getIdentifier();

                    // Received correct ack
                    if (ackNum == seqNum) {
                        System.out.println("Received ack for seq. #" + seqNum);
                        ackReceived = true;
                        break;
                    }
                }
                catch (SocketTimeoutException e) {
                    System.out.println("Did not receive ack for seq. #" + seqNum);
                    System.out.println("Resending packet with seq. # = "+ seqNum);
                    try {
                        socket.send(sendDatagramPacket);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Sent entire file sucessfully!");
        socket.close();
    }
}
