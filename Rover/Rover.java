package Rover;

import java.io.*;

public class Rover {
    byte id;
    RoverConfig roverConfig;

    public Rover(byte roverId) {
        this.id = roverId;
        this.roverConfig = new RoverConfig(roverId);

        Receiver r = new Receiver(this);
        r.start();
    }

    public Rover(byte roverId, String filename, String destinationRouterIP) {
        this.id = roverId;
        this.roverConfig = new RoverConfig(roverId);

        Receiver r = new Receiver(this);
        r.start();

        File file = new File(filename);
        try {
            InputStream fileStream = new FileInputStream(file);
            byte[] byteArray = new byte[(int)file.length()];
            fileStream.read(byteArray);

            Sender s = new Sender(this, destinationRouterIP, byteArray);
            s.start();
        } catch (FileNotFoundException e) {
            System.out.println("Cannot find file: " + filename);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error reading file: " + filename);
            e.printStackTrace();
        }
    }
}
