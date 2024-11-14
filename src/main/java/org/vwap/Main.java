package org.example;


import org.example.model.Trade;

import java.io.*;
import java.util.*;

import static org.example.CSVReader.readFromCSVFile;

public class Main {
    public static void main(String[] args) throws IOException {
        long startTime = System.nanoTime();
        for (String arg : args) {
            VWAPStreamReader vwapStreamReader = new VWAPStreamReader(readFromCSVFile(arg));
            vwapStreamReader.run();
        }

        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println("Total runtime: " + totalTime/1000000 + " milliseconds");
    }
}