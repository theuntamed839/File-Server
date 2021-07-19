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

    public void sendString(String str) throws IOException {
        outputStr.writeUTF(str);
    }

    public void sendByte(byte[] arr) throws IOException {
        outputStr.writeInt(arr.length);
        outputStr.write(arr);
    }

    public String readString() throws IOException {
        return inputStr.readUTF();
    }

    public byte[] readBytes(byte[] arr) throws IOException {
        inputStr.readFully(arr, 0, arr.length);
        return arr;
    }

    public int readInt() throws IOException {
        return inputStr.readInt();
    }

    public void close() throws IOException {
        socket.close();
        inputStr.close();
        outputStr.close();
    }
}
