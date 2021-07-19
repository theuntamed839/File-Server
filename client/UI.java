package client;

import java.io.IOException;
import java.util.Scanner;

public class UI {
    private Scanner scan;
    private ClientMachine clientMachine;

    public UI (ClientMachine clientMachine) throws IOException {
        this.clientMachine = clientMachine;
        scan = new Scanner(System.in);
        run();
    }

    private void run() throws IOException {
        System.out.println(
                "Enter action (1 - get a file, 2 - create a file, 3 - delete a file): ");
        var input = scan.nextLine();
        if (input.equals("exit")){
            clientMachine.send("exit");
            return;
        }
        var ans = Integer.parseInt(input);
        switch (ans) {
            case 1:
                getFileRequest();
                break;
            case 2:
                createFileRequest();
                break;
            case 3:
                deleteFileRequest();
        }
    }

    private void deleteFileRequest() throws IOException {
        String fileName = getFileName();
        clientMachine.send("DELETE " + fileName);
        if (clientMachine.read().equals("200")) {
            System.out.println("The response says that the file was successfully deleted!");
        }else {
            System.out.println("The response says that the file was not found!");
        }
    }

    private void createFileRequest() throws IOException {
        String fileName = getFileName();
        String content = getFileContent();
        String request = String.format("PUT %s %s", fileName, content);
        clientMachine.send(request);
        if (clientMachine.read().equals("200")) {
            System.out.println("The response says that the file was created!");
        }
        else {
            System.out.println("The response says that creating the file was forbidden!");
        }
    }

    private void getFileRequest() throws IOException {
        String fileName = getFileName();
        String request = String.format("GET %s", fileName);
        clientMachine.send(request);
        System.out.println("The request was sent.");
        String response = clientMachine.read();
        if (response.equals("404")) {
            System.out.println("The response says that the file was not found!");
        }else {
            System.out.println("The content of the file is: " + response.substring(4, response.length()));
        }
    }

    private String getFileName() {
        System.out.println("Enter filename: ");
        return scan.nextLine().trim();
    }

    private String getFileContent() {
        System.out.println("Enter file content: ");
        return scan.nextLine();
    }
}
