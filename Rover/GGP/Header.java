package Rover.GGP;

/**
 * Header.java
 *
 * Class or blueprint of GGP header
 *
 * @author gautamgadipudi
 * @version 1.0
 * @since 11/13/2020
 */
public class Header {
    String sourceIP;
    long identifier;
    boolean isLast;
    byte destinationRouterId;

    // In bytes
    public final static int size = 8;

    /**
     * Used at source when creating a header
     *
     * @param sourceIP IP of sender
     * @param id Number(seq number at sender and ack number at receiver) associated with packet.
     * @param isLast Is this the last packet?
     * @param destinationRouterId Destination of the router.
     */
    public Header(String sourceIP, long id, boolean isLast, byte destinationRouterId) {
        this.sourceIP = sourceIP;
        this.identifier = id;
        this.isLast = isLast;
        this.destinationRouterId = destinationRouterId;
    }

    /**
     * Used at receiver to convert byte array to header object
     *
     * @param byteArray GG header data as a byte array.
     */
    public Header(byte[] byteArray) {
        this.sourceIP = byteArray[0] + "." + byteArray[1] + "." + byteArray[2] + "." + byteArray[3];
        this.identifier = (byteArray[4] & 0xff << 8) + (byteArray[5] & 0xff);
        this.isLast = (byteArray[6] == 1) ? true : false;
        this.destinationRouterId = byteArray[7];
    }

    /**
     *  Create GG header as a byte array as below:
     *
     *        0                                                                     8 bytes
     *       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     *       |  Source IP (4)  | Identifier (2) | Last Segment (1) | Dest. Router Id (1) |
     *       +-----------------+-----------------+------------------+--------------------+
     * @return
     */
    public byte[] getByteArray() {
        byte[] headerByteArray = new byte[size];

        String[] sourceIPArray = this.sourceIP.split("\\.");

        headerByteArray[0] = Byte.parseByte(sourceIPArray[0]);
        headerByteArray[1] = Byte.parseByte(sourceIPArray[1]);
        headerByteArray[2] = Byte.parseByte(sourceIPArray[2]);
        headerByteArray[3] = Byte.parseByte(sourceIPArray[3]);

        headerByteArray[4] = (byte)(this.identifier >> 8);
        headerByteArray[5] = (byte) this.identifier;

        headerByteArray[6] = this.isLast ? (byte)1 : (byte)0;

        headerByteArray[7] = this.destinationRouterId;

        return headerByteArray;
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public long getIdentifier() {
        return identifier;
    }

    public boolean isLast() {
        return isLast;
    }
}
