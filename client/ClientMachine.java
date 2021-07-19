package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ClientMachine {
    private final Socket socket;
    private final DataInputStream inputStr;
    private final DataOutputStream outputStr;
    private final String ADDRESS = "127.0.0.1";
    private final int PORT = 23456;

    public ClientMachine() throws IOException {
        socket = new Socket(InetAddress.getByName(ADDRESS), PORT);
        System.out.println("Client started");
        inputStr = new DataInputStream(socket.getInputStream());
        outputStr = new DataOutputStream(socket.getOutputStream());
    }

    public void send(String str) throws IOException {
        outputStr.writeUTF(str);
//        System.out.println("Sent: " + str);
    }

    public String read() throws IOException {
        return inputStr.readUTF();
//        var str = inputStr.readUTF();
//        System.out.println("Received: " + str);
    }

    public void close() throws IOException {
        socket.close();
        inputStr.close();
        outputStr.close();
    }
}
