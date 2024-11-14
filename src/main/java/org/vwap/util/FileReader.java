package org.vwap.util;

import org.vwap.model.Trade;

import java.io.*;
import java.nio.file.Files;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class provides static methods to map trade data from different file types into Trade objects
 */
public class FileReader {

    private FileReader() {
        throw new IllegalStateException("Utility class");
    }

    public static List<Trade> readFromCSVFile(String filepath, String lineSeparator, boolean skipFirstLine) {
        long startTime = System.nanoTime();
        if (filepath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be empty");
        }
        if (lineSeparator.isEmpty()) {
            throw new IllegalArgumentException("Line separator cannot be empty");
        }

        List<Trade> trades = Collections.synchronizedList(new ArrayList<>());

        try {
            File initialFile = new File(filepath);
            InputStream inputStream = Files.newInputStream(initialFile.toPath());

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            if (skipFirstLine) {
                br.readLine();
            }
            br.lines().parallel().forEach(line -> {
                String[] csvValues = line.split(lineSeparator);

                if (csvValues.length != 4) {
                    System.err.println("Invalid CSV entry, should contain 4 data points: " + line);
                } else {
                    Instant timestamp = null;
                    String currencyPair;
                    float price = 0;
                    int volume = 0;

                    try {
                        timestamp = Instant.parse(csvValues[0]);
                    } catch (DateTimeParseException e) {
                        System.err.println("Invalid timestamp: " + csvValues[0]);
                    }

                    currencyPair = csvValues[1];

                    try {
                        price = Float.parseFloat(csvValues[2]);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid price: " + csvValues[2]);
                    }
                    try {
                        volume = Integer.parseInt(csvValues[3]);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid volume: " + csvValues[3]);
                    }

                    trades.add(new Trade(timestamp, currencyPair, price, volume));
                }
            });
        } catch (IOException e) {
            System.err.println("Error reading file: " + filepath);
        }
        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println("Successfully read from " + filepath + " Total runtime: " + totalTime / 1000000 + " milliseconds");
        return trades;
    }
}
