import Rover.Rover;

/**
 * Main.java
 *
 * Entry class for the rover
 *
 * @author gautamgadipudi
 * @version 1.0
 * @since 11/13/2020
 */
public class Main {
    public static void main(String[] args) {
        int argLength = args.length;

        // Create a rover that wants to receive data
        if (argLength == 1) {
            byte roverId = Byte.parseByte(args[0]);
            new Rover(roverId);
        }
        // Create a rover that not only wants to receive data, but also wants to send data
        else if (argLength == 3) {
            byte roverId = Byte.parseByte(args[0]);
            String filename = args[1];
            String destinationRoverIP = args[2];

            new Rover(roverId, filename, destinationRoverIP);
        }
        else {
            System.out.println("Invalid arguments.");
        }
    }
}
