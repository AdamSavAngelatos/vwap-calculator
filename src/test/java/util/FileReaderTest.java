package util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.vwap.model.Trade;
import org.vwap.util.FileReader;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileReaderTest {

    @Test
    void givenCorrectFile_whenReadFromCSVFile_thenReturnTrades() throws IOException {
        // Given
        String filePath = "./src/test/resources/test_data.csv";
        String recordSeparator = ",";

        // When
        List<Trade> trades = FileReader.readFromCSVFile(filePath, recordSeparator, false);

        // Then
        assertEquals(5, trades.size());
        assertEquals(30995, trades.get(0).getVolume());
        assertEquals(139.392, trades.get(1).getPrice());
        assertEquals("AUD/USD", trades.get(2).getCurrencyPair());
        assertEquals(Instant.parse("2024-10-24T10:15:33.00Z"), trades.get(3).getTimestamp());
    }

    @Test
    void givenFileWithHeaderAndSpaceSeparator_whenReadFromCSVFile_thenReturnTrades() throws IOException {
        // Given
        String filePath = "./src/test/resources/test_data_space_separator_with_header";
        String recordSeparator = " ";

        // When
        List<Trade> trades = FileReader.readFromCSVFile(filePath, recordSeparator, true);

        // Then
        assertEquals(5, trades.size());
        assertEquals(30995, trades.get(0).getVolume());
        assertEquals(139.392, trades.get(1).getPrice());
        assertEquals("AUD/USD", trades.get(2).getCurrencyPair());
        assertEquals(Instant.parse("2024-10-24T10:15:33.00Z"), trades.get(3).getTimestamp());
    }

    @Test
    void givenIncorrectFilepath_whenReadFromCSVFile_thenThrowIOException() throws IOException {
        // Given
        String filePath = "./src/test/resources/file_does_not_exist.csv";
        String recordSeparator = ",";


        // Then
        assertThrows(IOException.class, () ->
                // When
                FileReader.readFromCSVFile(filePath, recordSeparator, true));
    }

    @Test
    void givenEmptyFilepath_whenReadFromCSVFile_thenThrowIllegalArgumentException() {
        // Given
        String filePath = "";
        String recordSeparator = ",";

        // Then
        assertThrows(IllegalArgumentException.class, () ->
                // When
                FileReader.readFromCSVFile(filePath, recordSeparator, false));
    }

    @Test
    void givenEmptyRecordSeparator_whenReadFromCSVFile_thenThrowIllegalArgumentException() {
        // Given
        String filePath = "./src/test/resources/test_data.csv";
        String recordSeparator = "";

        // Then
        assertThrows(IllegalArgumentException.class, () ->
                // WHen
                FileReader.readFromCSVFile(filePath, recordSeparator, false));
    }

    @Test
    void given5RecordEntry_whenReadFromCSVFile_thenSkipEntry() throws IOException {
        // Given
        String filePath = "./src/test/resources/5_record_entry.csv";
        String recordSeparator = ",";

        // When
        List<Trade> trades = FileReader.readFromCSVFile(filePath, recordSeparator, false);

        // Then
        assertEquals(4, trades.size());
    }

    @Test
    void givenErroneousDate_whenReadFromCSVFile_thenNullTimestamp() throws IOException {
        // Given
        String filePath = "./src/test/resources/erroneous_date.csv";
        String recordSeparator = ",";

        // When
        List<Trade> trades = FileReader.readFromCSVFile(filePath, recordSeparator, false);

        // Then
        assertEquals(5, trades.size());
        assertNull(trades.get(2).getTimestamp());
    }

    @Test
    void givenErroneousPrice_whenReadFromCSVFile_thenPrice0() throws IOException {
        // Given
        String filePath = "./src/test/resources/erroneous_price.csv";
        String recordSeparator = ",";

        // When
        List<Trade> trades = FileReader.readFromCSVFile(filePath, recordSeparator, false);

        // Then
        assertEquals(5, trades.size());
        assertEquals(0, trades.get(4).getPrice());
    }

    @Test
    void givenErroneousPrice_whenReadFromCSVFile_thenVolume0() throws IOException {
        // Given
        String filePath = "./src/test/resources/erroneous_volume.csv";
        String recordSeparator = ",";

        // When
        List<Trade> trades = FileReader.readFromCSVFile(filePath, recordSeparator, false);

        // Then
        assertEquals(5, trades.size());
        assertEquals(0, trades.get(1).getVolume());
    }

    @Test
    @Disabled
        // TODO: For Test Driven Development (TDD) - This will fail since the logic for the method under test has not been implemented yet
    void givenCorrectFile_whenReadFromJSONFile_thenReturnTrades() {
        // Given
        String filePath = "./src/test/resources/test_data.json";

        // When
        List<Trade> trades = FileReader.readFromJSONFile(filePath);

        // Then
        assertEquals(5, trades.size());
    }
}
