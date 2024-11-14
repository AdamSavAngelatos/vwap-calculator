package calculator;

import javafx.util.Pair;
import org.junit.jupiter.api.Test;
import org.vwap.calculator.VWAPCalculator;
import org.vwap.model.Trade;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import static org.junit.jupiter.api.Assertions.*;

public class VWAPCalculatorTest {

    @Test
    void givenTrades_whenConstructorIsCalled_thenTradesAreGroupedByCurrencyPair() {
        // Given
        List<Trade> trades = generateTestTrades();

        // When
        VWAPCalculator calculator = new VWAPCalculator(trades);
        ConcurrentMap<String, List<Trade>> categorisedTades = calculator.getCategorisedTrades();

        // Then
        assertEquals(2, categorisedTades.get("USD/JPY").size());
        assertEquals(1, categorisedTades.get("AUD/USD").size());
        assertEquals(2, categorisedTades.get("NZD/GBP").size());
    }

    @Test
    void givenInputStream_whenCalculateHourlyVWAPs_thenHourlyVWAPsCalculated() {
        // Given
        List<Trade> trades = generateTestTrades();

        // When
        VWAPCalculator calculator = new VWAPCalculator(trades);
        Map<String, List<Pair<Instant, Double>>> hourlyVWAPs = calculator.calculateHourlyVWAPs();

        // Then
        assertEquals(139.42494749734252, hourlyVWAPs.get("USD/JPY").get(0).getValue());
        assertEquals(0.6899, hourlyVWAPs.get("AUD/USD").get(0).getValue());
        assertEquals(0.4725046592104985, hourlyVWAPs.get("NZD/GBP").get(0).getValue());
    }

    @Test
    void givenVolume0_whenCalculateHourlyVWAPs_exceptionCaughtAndOtherCalculationsProceed() {
        // Given
        List<Trade> trades = new ArrayList<>(1);
        trades.add(new Trade(Instant.parse("2024-10-24T10:15:30.00Z"), "USD/JPY", 142.497, 0));
        trades.add(new Trade(Instant.parse("2024-10-24T10:15:31.00Z"), "USD/JPY", 139.392, 0));
        trades.add(new Trade(Instant.parse("2024-10-24T10:15:32.00Z"), "AUD/USD", 0.6899, 444134));
        trades.add(new Trade(Instant.parse("2024-10-24T10:15:33.00Z"), "NZD/GBP", 0.4731, 64380));
        trades.add(new Trade(Instant.parse("2024-10-24T10:15:34.00Z"), "NZD/GBP", 0.4725, 8226295));

        // When
        VWAPCalculator calculator = new VWAPCalculator(trades);
        Map<String, List<Pair<Instant, Double>>> hourlyVWAPs = calculator.calculateHourlyVWAPs();

        // Then
        assertNull(hourlyVWAPs.get("USD/JPY").get(0).getValue()); // The average should be null
        assertEquals(hourlyVWAPs.get("USD/JPY").get(0).getKey(), Instant.parse("2024-10-24T10:00:00Z")); // But we should still log the timestamp
        assertEquals(0.6899, hourlyVWAPs.get("AUD/USD").get(0).getValue());
        assertEquals(0.4725046592104985, hourlyVWAPs.get("NZD/GBP").get(0).getValue());
    }

    @Test
    void givenUnsortedTrades_whenCalculateHourlyVWAPs_resultsAreInChronologicalOrder() {
        // Given
        List<Trade> trades = new ArrayList<>();

        trades.add(new Trade(Instant.parse("2024-10-24T10:15:30.00Z"), "USD/JPY", 142.497, 30995));
        trades.add(new Trade(Instant.parse("2024-10-24T10:15:31.00Z"), "USD/JPY", 139.392, 2890000));
        trades.add(new Trade(Instant.parse("2024-10-24T10:15:32.00Z"), "AUD/USD", 0.6899, 444134));
        trades.add(new Trade(Instant.parse("2024-10-24T10:15:33.00Z"), "NZD/GBP", 0.4731, 64380));
        trades.add(new Trade(Instant.parse("2024-10-24T10:15:34.00Z"), "NZD/GBP", 0.4725, 8226295));
        trades.add(new Trade(Instant.parse("2024-10-21T10:15:34.00Z"), "AUD/USD", 0.4725, 8226295));
        trades.add(new Trade(Instant.parse("2024-10-22T10:15:34.00Z"), "AUD/USD", 0.4725, 8226295));
        trades.add(new Trade(Instant.parse("2024-10-24T11:15:34.00Z"), "NZD/GBP", 0.4725, 8226295));
        trades.add(new Trade(Instant.parse("2024-10-24T12:15:34.00Z"), "NZD/GBP", 0.4725, 8226295));
        trades.add(new Trade(Instant.parse("2024-10-24T02:15:34.00Z"), "USD/JPY", 0.4725, 8226295));
        trades.add(new Trade(Instant.parse("2024-10-24T02:16:34.00Z"), "USD/JPY", 0.4725, 8226295));
        trades.add(new Trade(Instant.parse("2024-10-24T02:17:34.00Z"), "USD/JPY", 0.4725, 8226295));

        // When
        VWAPCalculator calculator = new VWAPCalculator(trades);
        Map<String, List<Pair<Instant, Double>>> hourlyVWAPs = calculator.calculateHourlyVWAPs();

        // Then
        assertEquals(Instant.parse("2024-10-24T02:00:00.00Z"), hourlyVWAPs.get("USD/JPY").get(0).getKey()); // First
        assertEquals(Instant.parse("2024-10-24T10:00:00.00Z"), hourlyVWAPs.get("USD/JPY").get(1).getKey()); // Last

        assertEquals(Instant.parse("2024-10-24T10:00:00.00Z"), hourlyVWAPs.get("NZD/GBP").get(0).getKey());
        assertEquals(Instant.parse("2024-10-24T12:00:00.00Z"), hourlyVWAPs.get("NZD/GBP").get(2).getKey());

        assertEquals(Instant.parse("2024-10-21T10:00:00.00Z"), hourlyVWAPs.get("AUD/USD").get(0).getKey());
        assertEquals(Instant.parse("2024-10-24T10:00:00.00Z"), hourlyVWAPs.get("AUD/USD").get(2).getKey());
    }

    private List<Trade> generateTestTrades() {
        List<Trade> trades = new ArrayList<>(5);

        trades.add(new Trade(Instant.parse("2024-10-24T10:15:30.00Z"), "USD/JPY", 142.497, 30995));
        trades.add(new Trade(Instant.parse("2024-10-24T10:15:31.00Z"), "USD/JPY", 139.392, 2890000));
        trades.add(new Trade(Instant.parse("2024-10-24T10:15:32.00Z"), "AUD/USD", 0.6899, 444134));
        trades.add(new Trade(Instant.parse("2024-10-24T10:15:33.00Z"), "NZD/GBP", 0.4731, 64380));
        trades.add(new Trade(Instant.parse("2024-10-24T10:15:34.00Z"), "NZD/GBP", 0.4725, 8226295));

        return trades;
    }
}
