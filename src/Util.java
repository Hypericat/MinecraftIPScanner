import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Util {
    public static void print(Object str) {
        System.out.println(str);
    }
    public static File createFile(String filename, File directory) {
        File file = new File(directory.getAbsolutePath() + "\\" + filename);
        if (file.exists()) if (!file.delete()) return null;
        try {
            file.createNewFile();
        } catch (java.io.IOException exception) {
            return null;
        }
        return file;
    }
    public static String getInput() {
        return getInput("");
    }
    public static String getInput(Object string) {
        print(string);
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
    public static boolean isBetween(double num, int lowerBound, int upperBound) {
        if (num > upperBound) return false;
        if (num < lowerBound) return false;
        return true;
    }
    public static boolean isDouble(String dub) {
        if (dub == null) return false;
        boolean first = true;
        int periodCounter = 0;
        if (dub.length() == 0) return false;
        for (char cha : dub.toCharArray()) {
            if (first && String.valueOf(cha).equals(".")) return false;
            if (!"-1234567890.".contains(String.valueOf(cha))) return false;
            if (!first && String.valueOf(cha).equals("-")) return false;
            if (String.valueOf(cha).equals(".")) periodCounter ++;
            first = false;
        }
        return periodCounter <= 1;
    }
    public static boolean isInt(String inte) {
        if (inte == null) return false;
        if (inte.length() == 0) return false;
        for (char cha : inte.toCharArray()) {
            if (!"1234567890".contains(String.valueOf(cha))) return false;
        }
        return true;
    }
    public static void writeToFile(String str, File f1) {
        try {
            FileWriter fw = new FileWriter(f1.getAbsolutePath());
            fw.write(str);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String UpperCamel(String str) {
        char[] chars = str.toCharArray();
        String char1 = String.valueOf(chars[0]).toUpperCase();
        return char1 + str.substring(1);
    }
    public static List<String> readFromFile(File file) {
        List<String> contents = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                contents.add(scanner.nextLine());
            }
            scanner.close();
            return contents;
        } catch (FileNotFoundException exception) {
            return null;
        }
    }
    public static String reverseString(String input) {
        return new StringBuilder(input).reverse().toString();
    }
    public static String capitalize(String input) {
        return input.toUpperCase();
    }
    public static <T> List<T> removeDuplicates(List<T> list) {
        return new ArrayList<>(new LinkedHashSet<>(list));
    }
    public static <T> List<T> filterList(List<T> list, Predicate<T> predicate) {
        return list.stream().filter(predicate).collect(Collectors.toList());
    }
    public static int factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Input must be non-negative");
        }
        return (n == 0 || n == 1) ? 1 : n * factorial(n - 1);
    }
    public static boolean isPrime(int number) {
        if (number <= 1) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }
    public static String getCurrentDateTime() {
        return new Date().toString();
    }
    public static int getRandomNumber(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }
}