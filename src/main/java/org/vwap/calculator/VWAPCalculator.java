package org.vwap.calculator;

import javafx.util.Pair;
import org.vwap.model.Trade;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Performs calculations on an inputStream of Trade data
 */
public class VWAPStreamReader {
    private final ConcurrentMap<String, List<Trade>> categorisedTrades;

    public VWAPStreamReader(List<Trade> inputStream) {
        // Group trades by currency pair concurrently
        this.categorisedTrades = inputStream.parallelStream().collect(Collectors.groupingByConcurrent(Trade::getCurrencyPair));
    }

    /**
     * Calculates Volume Weighted Average Prices (VWAP) by the hour for each unique currency pair using the inputStream
     * provided in the class constructor
     * @return A list of key-value pairs (HOUR, VWAP) categorised by unique currency pair
     */
    public Map<String, List<Pair<Instant, Float>>> calculateHourlyVWAPs() {
        Map<String, List<Pair<Instant, Float>>> categorisedHourlyVWAPs = new HashMap<>(categorisedTrades.size());

        categorisedTrades.forEach((currencyPair, trades) -> {
            // Group trades by the hour concurrently
            ConcurrentMap<Long, List<Trade>> hourlyTrades = trades.parallelStream().collect(Collectors.groupingByConcurrent(
                    trade -> trade.getTimestamp().toEpochMilli() / 3600000));

            List<Pair<Instant, Float>> hourlyVWAPs = new ArrayList<>(hourlyTrades.size());
            ArrayList<Long> hours = new ArrayList<>(hourlyTrades.keySet());

            // Calculate the hourly averages concurrently
            hours.parallelStream().forEach(hour -> {
                List<Trade> hourTrades = hourlyTrades.get(hour);

                // Create a key-value pair mapping the beginning hour to the VWAP for that hour
                Pair<Instant, Float> hourlyVWAP = new Pair<>(Instant.ofEpochMilli(hour * 3600000), calculateVWAP(hourTrades));

                hourlyVWAPs.add(hourlyVWAP);
            });

            // Sort the final list so we can perform a sequential, chronological read later
            hourlyVWAPs.sort(Comparator.comparing(Pair::getKey));
            categorisedHourlyVWAPs.put(currencyPair, hourlyVWAPs);
        });

        // Log the results sequentially
        categorisedHourlyVWAPs.forEach((currencyPair2, hourlyAverages) ->
                hourlyAverages.forEach(hourlyAverage -> System.out.println(hourlyAverage.getKey() +
                        " VWAP(" + currencyPair2 + ")" + " = " + hourlyAverage.getValue())));

        return categorisedHourlyVWAPs;
    }

    private float calculateVWAP(List<Trade> trades) {
        float volumePrice = 0;
        float totalVolume = 0;

        for (Trade trade : trades) {
            volumePrice += trade.getPrice() * trade.getVolume();
            totalVolume += trade.getVolume();
        }

        if (totalVolume <= 0) {
            throw new ArithmeticException("Erroneous data - total volume of trades is zero");
        } else {
            return volumePrice / totalVolume;
        }
    }
}
