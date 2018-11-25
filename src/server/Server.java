package server;

import javax.xml.ws.Endpoint;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.rmi.AlreadyBoundException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class Server {
    private static Logger logs;
    private static FileHandler fileHandler;

    public static void main(String[] args) {

        int compPort = 6789, soenPort = 6791, insePort = 6793;

        int port = 0;
        String servername = "";

        // Check for the department and port number
        switch (args[0]) {
            case "1":
                servername = "COMP";
                port = compPort;
                break;
            case "2":
                servername = "SOEN";
                port = soenPort;
                break;
            case "3":
                servername = "INSE";
                port = insePort;
                break;
        }


        // set up the logging mechanism
        logs = Logger.getLogger(servername + " Server");
        try {
            fileHandler = new FileHandler(servername + ".log", true);
            logs.addHandler(fileHandler);
        } catch (IOException ioe) {
            logs.warning("Failed to create handler for log file.\n Message: " + ioe.getMessage());
        }

        ServerOperations serveroperations = new ServerOperations(servername, logs);

        //Webservice code goes here
        Endpoint.publish("http://localhost:" + port + "/" + servername, serveroperations);

        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(port);
            byte[] buffer = new byte[1000];
            logs.info("The UDP server for " + servername + " is up and running on port " + (port));
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);

                UdpServerProc udpServerProc = new UdpServerProc(socket, request, serveroperations);
                udpServerProc.start();
            }
        } catch (Exception e) {
            logs.warning("Exception thrown while server was running/trying to start.\\nMessage: " + e.getMessage());
        }
    }
}
