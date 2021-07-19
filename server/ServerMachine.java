package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class ServerMachine {
    private final FileManager fileManager;
    private final ServerSocket serSocket;
    private Socket socket;
    private DataOutputStream outputStr;
    private DataInputStream inputStr;
    private final String ADDRESS = "127.0.0.1";
    private final int PORT = 23456;

    public ServerMachine(FileManager fileManager) throws IOException {
        this.fileManager = fileManager;
        serSocket = new ServerSocket(PORT, 50, InetAddress.getByName(ADDRESS));
        System.out.println("Server started!");
        socket = serSocket.accept();
        inputStr = new DataInputStream(socket.getInputStream());
        outputStr = new DataOutputStream(socket.getOutputStream());
        run();
    }


    public void run() throws IOException {
        while (true) {
            try {
                if (read()) {
                    break;
                }
            } catch (IOException e) {
                socket = serSocket.accept();
                inputStr = new DataInputStream(socket.getInputStream());
                outputStr = new DataOutputStream(socket.getOutputStream());
            }
        }
        close();
    }

    public boolean read() throws IOException {
        String request = inputStr.readUTF();
        if(request.equals("exit")){
            return true;
        }
        String requestType = getRequestType(request);
        String fileName = getFileName(request);
        switch (Objects.requireNonNull(requestType)) {
            case "PUT":
                int length = inputStr.readInt();
                byte[] arr = new byte[length];
                inputStr.readFully(arr, 0,length);
                if (fileName.isEmpty()) {
                    send(fileManager.addFileAutoName(arr)+"");
                }else {
                    if (!fileManager.fileExists(fileName)) {
                        var value = fileManager.addFile(fileName, arr);
                        if (value < 0) {
                            send("FAILED");
                        }
                        send(value + "");
                    }
                }
                break;
            case "GET":
                var response = fileManager.getFileContent(fileName);
                if (response.isPresent()) {
                    send("200");
                    var file = response.get();
                    outputStr.writeInt(file.length);
                    outputStr.write(file, 0, file.length);
                }else {
                    send("404");
                }
                break;
            case "DELETE":
                String result2 = fileManager.deleteFile(fileName) ? 200 + "" : 404 +"";
                send(result2);
        }
        return false;
    }

    private String getFileName(String request) {
        StringBuilder str = new StringBuilder();
        boolean hasFirstSpaceEncountered = false;
        for (int i = 0; i < request.length(); i++) {
            char a = request.charAt(i);
            if (a == ' ') {
                if (!hasFirstSpaceEncountered) {
                    hasFirstSpaceEncountered = true;
                }
                else return str.toString();
                continue;
            }
            if (hasFirstSpaceEncountered) {
                str.append(a);
            }
        }
        return str.toString();
    }

    private String getFileContent(String request) {
        int spaces = 0;
        int i;
        for (i = 0; i < request.length() && spaces < 2; i++) {
            char a = request.charAt(i);
            if (a == ' ') {
                spaces++;
            }
        }
        return request.substring(i, request.length());
    }

    private String getRequestType(String request) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < request.length(); i++) {
            char a = request.charAt(i);
            if (a == ' ') {
                return str.toString();
            }
            str.append(a);
        }
        return null;
    }

    public void send(String str) throws IOException {
        outputStr.writeUTF(str);
        System.out.println("Sent: " + str);
    }

    public void close() throws IOException {
        serSocket.close();
        socket.close();
        inputStr.close();
        outputStr.close();
    }
}
