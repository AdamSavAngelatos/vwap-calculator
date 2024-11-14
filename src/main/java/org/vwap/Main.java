package org.vwap;


import javafx.util.Pair;
import org.vwap.model.Trade;
import org.vwap.calculator.VWAPCalculator;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.vwap.util.FileReader.readFromCSVFile;

public class Main {
    public static void main(String[] args) throws IOException {

        boolean debug = Arrays.asList(args).contains("-debug");

        Map<String, List<Pair<Instant, Double>>> categorisedHourlyVWAPs = new HashMap<>();
        for (String arg : args) {
            if (!arg.equals("-debug")) {
                List<Trade> trades = readFromCSVFile(arg, ",", true);

                long startTime = System.nanoTime();
                VWAPCalculator vwapCalculator = new VWAPCalculator(trades);
                categorisedHourlyVWAPs = vwapCalculator.calculateHourlyVWAPs();
                long endTime = System.nanoTime();
                long totalTime = endTime - startTime;

                // TODO: Use a logging library e.g. log4j
                System.out.println("Calculating VWAP of " + trades.size() + " records took: " + totalTime / 1000000 + " milliseconds");
            }

            // TODO: Use logging profiles
            if (debug) {
                // Log the results sequentially
                categorisedHourlyVWAPs.forEach((currencyPair, hourlyAverages) ->
                        hourlyAverages.forEach(hourlyAverage -> {
                            if (hourlyAverage.getValue() != null) {
                                System.out.println(hourlyAverage.getKey() +
                                        " VWAP(" + currencyPair + ")" + " = " + hourlyAverage.getValue());
                            } else {
                                System.err.println(hourlyAverage.getKey() + " VWAP(" + currencyPair + ")" + " = null");
                            }
                        })
                );
            }

        }


    }
}