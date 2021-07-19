package server;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        var fileManager = new FileManager();
        new ServerMachine(fileManager);
        fileManager.close();
    }
}
