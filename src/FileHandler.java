import java.io.*;
import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileHandler {
    public static File createAndTestDir(File file) {
        if (file.isDirectory()) return file;
        if (!file.mkdir()) {
            throw new RuntimeException("Failed to create directory at : " + file.getAbsolutePath());
        }
        return file;
    }
    public static File addFileToDirectory(File directory, String file) {
        return new File(directory.getAbsolutePath() + "\\" + file);
    }
    public static File createDir(File directory, String directoryName) {
        return createAndTestDir(new File(directory.getAbsolutePath() + "\\" + directoryName));
    }
    public static void appendToFile(File file, List<String> str) {
        appendToFile(file, str, true);
    }
    public static void appendToFile(File file, List<String> str, boolean nextLine) {
        createAndTestFile(file);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            for (String string : str) {
                writer.append(string);
                if (nextLine) writer.append("\n");
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static File lookForFile(File directory, String fileName) {
        createAndTestDir(directory);
        for (File file : directory.listFiles()) {
            if (file.getName().equalsIgnoreCase(fileName)) return file;
        }
        return null;
    }
    public static void wipeFile(File file) {
        try {
            new FileWriter(file, false).close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void overWriteToFile(File file, List<String> str) {
        overWriteToFile(file, str, true);
    }
    public static void overWriteToFileFile(File file, List<File> str, boolean nextLine) {
        createAndTestFile(file);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
            for (File string : str) {
                writer.append(string.getAbsolutePath());
                if (nextLine) writer.append("\n");
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void overWriteToFile(File file, List<String> str, boolean nextLine) {
        createAndTestFile(file);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
            for (String string : str) {
                writer.append(string);
                if (nextLine) writer.append("\n");
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void overWriteToFile(File file, String str) {
        overWriteToFile(file, str, true);
    }
    public static void overWriteToFile(File file, String str, boolean nextLine) {
        createAndTestFile(file);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
            writer.append(str);
            if (nextLine) writer.append("\n");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void appendToFile(File file, String str) {
        appendToFile(file, str, true);
    }
    public static void appendToFile(File file, String str, boolean nextLine) {
        createAndTestFile(file);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.append(str);
            if (nextLine) writer.append("\n");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static File replaceFileLine(File file, String str, int line) {
        createAndTestFile(file);
        List<String> content = readFromFile(file);
        if (content.size() - 1 < line) {
            content.add("please ignore need to fix later");
            System.out.println("DONT FORGET TO FIX THIS LINE 100 FILE HANDLER CLASS, IGNORE THIS OTHERWISE IF YOU ARE NOT ME");
        }
        content.set(line, str);
        wipeFile(file);
        overWriteToFile(file, str);
        return file;
    }
    public static List<String> readFromFile(File file) {
        createAndTestFile(file);
        List<String> strs = new ArrayList<>();
        try {
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                strs.add(reader.nextLine());
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return strs;
    }
    public static File createAndTestFile(File file) {
        if (file.exists()) return file;
        return createFile(file.getParent(), file.getName());
    }
    public static File createFile(String directory, String name) {
        File file = new File(directory + "\\" + name);
        if (file.exists()) return file;
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }
    public static File createFile(File directory, String name) {
        return createFile(createAndTestDir(directory).getAbsolutePath(), name);
    }
}