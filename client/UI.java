package client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class UI {
    private Scanner scan;
    private ClientMachine clientMachine;
    private String PATH = System.getProperty("user.dir") +
            File.separator + "src" + File.separator + "client" + File.separator + "data" + File.separator;
    public UI (ClientMachine clientMac) throws IOException {
        new File(PATH).mkdirs();
        clientMachine = clientMac;
        scan = new Scanner(System.in);
        run();
    }

    private void run() throws IOException {
        System.out.println(
                "Enter action (1 - get a file, 2 - save a file, 3 - delete a file): ");
        var input = scan.nextLine();
        if (input.equals("exit")){
            clientMachine.sendString("exit");
            return;
        }
        var ans = Integer.parseInt(input);
        switch (ans) {
            case 1:
                getFileRequest();
                break;
            case 2:
                saveFileRequest();
                break;
            case 3:
                deleteFileRequest();
        }
    }

    private void deleteFileRequest() throws IOException {
        clientMachine.sendString("DELETE " + getReq());
        if (clientMachine.readString().equals("200")) {
            System.out.println("The response says that this file was deleted successfully");
        }else {
            System.out.println("The response says that the file was not found!");
        }
    }

    private void saveFileRequest() throws IOException {
        String fileName = getFileName();
        String saveFileName = getNameForSave();
        String request = String.format("PUT %s", saveFileName);
        clientMachine.sendString(request);
        clientMachine.sendByte(Files.readAllBytes(Path.of(PATH + fileName)));
        try {
            var id = clientMachine.readString();
            if(!id.equals("FAILED")) {
                System.out.println("Response says that file is saved! ID = " + id);
            }
            else throw new Exception();
        }catch (Exception e) {
            System.out.println("The response says that creating the file was forbidden!");
        }
    }

    private void getFileRequest() throws IOException {
        String request = String.format("GET %s", getReq());
        clientMachine.sendString(request);
        System.out.println("The request was sent.");
        String response = clientMachine.readString();
        if (response.equals("200")) {
            int size = clientMachine.readInt();
            byte[] arr = clientMachine.readBytes(new byte[size]);
            System.out.println("The file was downloaded! Specify a name for it:");
            var outStream = new FileOutputStream(PATH + scan.nextLine().trim());
            outStream.write(arr);
            outStream.close();
            System.out.println("File saved on the hard drive");
        }else {
            System.out.println("The response says that this file is not found!");
        }
    }

    private String getReq() {
        System.out.println("Do you want to get the file by name or by id (1 - name, 2 - id):");
        boolean byId = scan.nextLine().equals("2");
        var fileIdentity = byId ? "id=" : "name=";
        System.out.println(byId ? "Enter the Id: " : "Enter the name");

        return fileIdentity + scan.nextLine().trim();
    }

    private String getFileName() {
        System.out.println("Enter name of the file: ");
        return scan.nextLine();
    }

    private String getNameForSave() {
        System.out.println("Enter name of the file to be saved on server: ");
        String saveFileName = scan.nextLine();
        if (saveFileName.isEmpty() || saveFileName.equals("\n")) {
            return "";
        }
        return saveFileName;
    }
}
