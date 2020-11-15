package Rover;

import Rover.GGP.Header;
import Rover.GGP.Packet;

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
            this.socket = new DatagramSocket(router.routerConfig.fileExchangeSenderPort);
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

            // Check if last segment
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

//            Header Header = new Header(router.routerConfig.Address, seqNum, isLast, Byte.parseByte(destinationRouterIP.split("\\.")[2]));
            Header Header = new Header(router.routerConfig.Address, seqNum, isLast, (byte)1);
            Packet packet = null;
            try {
                packet = new Packet(Header, data);
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] packetByteArray = packet.getByteArray();
            DatagramPacket senderDatagramPacket = new DatagramPacket(packetByteArray, packetByteArray.length, destinationAddress, router.routerConfig.fileExchangeReceiverPort);
            try {
                System.out.println("Sending packet with seq. #" + seqNum);
                socket.send(senderDatagramPacket);
            } catch (IOException e) {
                System.out.println("SENDER: Unable to send!");
                e.printStackTrace();
            }

            boolean ackVerified = false;

            while (!ackVerified) {
                byte[] buffer = new byte[8];
                DatagramPacket ackPacket = new DatagramPacket(buffer, buffer.length);

                try {
                    socket.setSoTimeout(1000);
                    socket.receive(ackPacket);
                    Header ackHeader = new Header(buffer);

                    ackNum = ackHeader.getIdentifier();

                    if (ackNum == seqNum) {
                        System.out.println("Received ack for seq. #" + seqNum);
                        ackVerified = true;
                        break;
                    }
                }
                catch (SocketTimeoutException e) {
                    System.out.println("ReTransmitting packet with seqNum = "+ seqNum);
                    try {
                        socket.send(senderDatagramPacket);
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
