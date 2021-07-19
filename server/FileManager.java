package server;

import java.io.*;
import java.nio.file.Files;
import java.util.Optional;

public class FileManager {
    private String PATH = System.getProperty("user.dir") +
            File.separator + "src" + File.separator + "server" + File.separator + "data" + File.separator;//"server/data/";
    private final Map<Integer, String> fileMap;

    public FileManager() {
        new File(PATH).mkdirs();
        fileMap = new ConcurrentHashMap<>();
        setFileMap();
    }

    public Optional<String> getFileContent(String Fname) {
        var file = new File(PATH + Fname);
        if (!file.exists()) {
            return Optional.empty();
        }
        return getFileContent(file);
    }

    public boolean fileExists(String fileName) {
        return new File(PATH + fileName).exists();
    }

    private Optional<String> getFileContent(File file) {
        try {
            return Optional.ofNullable(Files.readString(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public boolean addFile(String Fname, String Fcontent) {
        var file = new File(PATH + Fname);
        if (file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return addContentToFile(file, Fcontent);
    }

    private boolean addContentToFile(File file, String fcontent) {
        try (PrintWriter printWriter = new PrintWriter(file)) {
            printWriter.append(fcontent);
        } catch (FileNotFoundException e) {
            return false;
        }
        return true;
    }

    public boolean deleteFile(String Fname) {
        var file = new File(PATH + Fname);
        return file.exists() && file.delete();
    }
}
