package cz.majlon.bsc.payment.utils;

import java.io.*;

public class FileIO {

    public static BufferedReader getFileReader(String fileName) throws FileNotFoundException {
        FileReader fileReader = new FileReader(getCompletePath(fileName));
        return new BufferedReader(fileReader);
    }

    public static BufferedWriter getFileWriter(String fileName) throws IOException {
        FileWriter writer = new FileWriter(getCompletePath(fileName));
        return new BufferedWriter(writer);
    }

    private static String getCompletePath(String fileName) {
        String currentDir = System.getProperty("user.dir");
        return currentDir + "\\" + fileName.trim();
    }
}
