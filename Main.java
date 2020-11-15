import Rover.Router;

public class Main {
    public static void main(String[] args) {
        int argLength = args.length;

        if (argLength == 1) {
            byte roverId = Byte.parseByte(args[0]);
            Router router = new Router(roverId);
        }
        else if (argLength == 3) {
            byte roverId = Byte.parseByte(args[0]);
            String filename = args[1];
            String destinationRoverIP = args[2];

            Router router = new Router(roverId, filename, destinationRoverIP);
        }
        else {
            System.out.println("Invalid arguments.");
        }
    }
}
