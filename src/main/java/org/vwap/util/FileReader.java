package org.vwap.util;

import org.vwap.model.Trade;

import java.io.*;
import java.nio.file.Files;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Provides static methods to map trade data from different file types into Trade objects
 */
public class FileReader {

    private FileReader() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Converts trade data from a CSV file into Trade objects
     * <p>
     * Expects CSV entries to follow the format TIMESTAMP,CURRENCY_PAIR,PRICE,VOLUME
     * </p
     * <p>
     * NOTE: File read is multithreaded to improve performance - the returned list will not be sequential
     * </p>
     *
     * @param filepath        Path to the csv file to read
     * @param recordSeparator Character that separates entries in the provided csv file
     * @param containsHeader  Whether the csv file contains a header
     * @return A list of Trade objects
     * @throws IOException If unable to create an InputStream from filepath
     */
    public static List<Trade> readFromCSVFile(String filepath, String recordSeparator, boolean containsHeader)
            throws IOException {
        long startTime = System.nanoTime();
        if (filepath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be empty");
        }
        if (recordSeparator.isEmpty()) {
            throw new IllegalArgumentException("Line separator cannot be empty");
        }

        // Need to create a synchronized list to handle parallel writes
        List<Trade> trades = Collections.synchronizedList(new ArrayList<>());

        System.out.println("Reading from: " + filepath);
        File initialFile = new File(filepath);
        InputStream inputStream = Files.newInputStream(initialFile.toPath());

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        if (containsHeader) {
            // Skip the header
            br.readLine();
        }
        AtomicInteger totalLines = new AtomicInteger();
        br.lines().parallel().forEach(line -> {
            totalLines.getAndIncrement();
            String[] csvValues = line.split(recordSeparator);

            if (csvValues.length != 4) {
                System.err.println("Invalid CSV entry, should contain 4 data points: " + line);
            } else {
                Instant timestamp = null;
                String currencyPair;
                double price = 0;
                int volume = 0;

                try {
                    timestamp = Instant.parse(csvValues[0]);
                } catch (DateTimeParseException e) {
                    System.err.println("Invalid timestamp: " + csvValues[0]);
                }

                currencyPair = csvValues[1];

                try {
                    price = Double.parseDouble(csvValues[2]);
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

        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println("Successfully converted " + totalLines + " csv entries into " + trades.size() +
                " Trade objects from " + filepath + " Total runtime: " + totalTime / 1000000 + " milliseconds");
        return trades;
    }

    /**
     * Converts trade data from a JSON file into Trade objects
     * <p>
     * NOTE: Method implementation still in progress
     * </p>
     *
     * @param filepath Path to the JSON file to read
     * @return A list of Trade objects
     */
    public static List<Trade> readFromJSONFile(String filepath) {
        List<Trade> result = new ArrayList<>();

        // TODO: Implement logic

        return result;
    }
}
