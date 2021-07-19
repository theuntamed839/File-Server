package server;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class FileManager {
    private String PATH = System.getProperty("user.dir") +
            File.separator + "src" + File.separator + "server" + File.separator + "data" + File.separator;//"server/data/";
    private final String TOKEN_FILE = PATH + "TOKEN_FILE";
    private int idCount;
    private final Map<Integer, String> fileMap;

    public FileManager() throws FileNotFoundException {
        new File(PATH).mkdirs();
        fileMap = new ConcurrentHashMap<>();
        setFileMap();
    }

    private void setFileMap() throws FileNotFoundException {
        var file = new File(TOKEN_FILE);
        if (!file.exists()) {
            idCount = 1;
            return;
        }
        Scanner scan = new Scanner(file);
        int max = 0;
        while(scan.hasNext()) {
            String[] parts = scan.nextLine().trim().split(":");
            int number = Integer.parseInt(parts[0]);
            fileMap.put(number, parts[1]);
            if (number > max) {
                max = number;
            }
        }
        idCount = max + 1;
    }

    public Optional<byte[]> getFileContent(String Fname) {
        String fileName;
        if (Fname.contains("id")) {
            var name = LocalDateTime.now()
                    .toString().replace(".", "_").replace(":", "_");
            fileName = fileMap.getOrDefault(Integer.valueOf(Fname.split("=")[1]), name);
        }else {
            fileName = Fname.split("=")[1];
        }
        var file = new File(PATH + fileName);
        if (!file.exists()) {
            return Optional.empty();
        }
        return getFileContent(file);
    }

    public boolean fileExists(String fileName) {
        return new File(PATH + fileName).exists();
    }

    private Optional<byte[]> getFileContent(File file) {
        try {
            return Optional.of(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public int addFile(String Fname, byte[] Fcontent) {
        var file = new File(PATH + Fname);
        if (file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return addContentToFile(file, Fcontent);
    }

    private int addContentToFile(File file, byte[] fcontent) {
        try (var out = new FileOutputStream(file)) {
            out.write(fcontent);
        } catch (FileNotFoundException e) {
            return -1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileMap.put(idCount++, file.getName());
        return idCount - 1;
    }

    public boolean deleteFile(String Fname) {
        String name;
        if (Fname.contains("id")) {
            name = fileMap.getOrDefault(Integer.valueOf(Fname.split("=")[1]), "null");
            if (name == null) {
                return false;
            }
        }
        else {
            name = Fname.split("=")[1];
        }
        var file = new File(PATH + name);
        if (!file.exists()) {
            return false;
        }
        return file.delete();
    }

    public void close() throws IOException {
        PrintWriter pr = new PrintWriter(new FileWriter(TOKEN_FILE));
        for (Integer i: fileMap.keySet()) {
            pr.write(i + ":" + fileMap.get(i) + "\n");
        }
        pr.close();
    }

    public int addFileAutoName(byte[] arr) throws IOException {
        var name = LocalDateTime.now()
                .toString().replace(".", "_").replace(":", "_");
        var outStream = new FileOutputStream(PATH + name);
        outStream.write(arr);
        outStream.close();
        fileMap.put(idCount++, name);
        return idCount - 1;
    }
}
