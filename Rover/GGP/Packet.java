package Rover.GGP;

import java.util.Arrays;

/**
 * Packet.java
 *
 * Class/blueprint of a GGP(Gautam Gadipudi Protocol) packet
 *
 * @author gautamgadipudi
 * @version 1.0
 * @since 11/10/2020
 */
public class Packet {
    Header header;
    byte[] data;

    // In bytes
    public static final int maximumSize = 10000;

    /**
     * Used at sender when creating GGP packet
     *
     * @param Header
     * @param data
     */
    public Packet(Header Header, byte[] data) throws Exception {
        this.header = Header;

        if (data.length > maximumSize - Header.size) {
            throw new Exception("Data cannot be more than " + (maximumSize - Header.size) + " bytes");
        }
        else
            this.data = data;
    }

    public Packet(Header Header) {
        this.header = Header;
        this.data = new byte[0];
    }


    /**
     * Used at receiver to create GGPPacket object from byte array
     *
     * @param packet byte array
     */
    public Packet(byte[] packet) {
        byte[] headerByteArray = Arrays.copyOfRange(packet, 0, Header.size);

        this.header = new Header(headerByteArray);
        this.data = Arrays.copyOfRange(packet, Header.size, packet.length);
    }

    /**
     * Create GG packet as a byte array as below:
     *
     *       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     *       |                              Header (8 bytes)                           |
     *       +-------------------------------------------------------------------------+
     *       |                                                                         |
     *       ~                              Data (upto 1016 bytes)                     |
     *       |                                                                         |
     *       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     */
    public byte[] getByteArray() {
        byte[] packetByteArray = new byte[Header.size + data.length];
        byte[] headerByteArray = this.header.getByteArray();

        for (int i=0; i < headerByteArray.length; i++) {
            packetByteArray[i] = headerByteArray[i];
        }

        for (int i=0; i < data.length; i++) {
            packetByteArray[i + headerByteArray.length] = data[i];
        }

        return packetByteArray;
    }

    public byte[] getData() {
        return data;
    }

    public Header getHeader() {
        return header;
    }
}
