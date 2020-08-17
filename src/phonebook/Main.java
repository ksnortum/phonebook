package phonebook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String PREFIX = "/home/knute/Documents/hyperskill/Phone-Book/";
    private static final int NANO_PER_MILLI = 1_000_000;

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        List<PhoneBookEntry> phoneBook = loadPhoneBook(PREFIX + "directory.txt");
        List<String> names = loadLinesFromFile(PREFIX + "find.txt");

        long linearSearchTime = doLinearSearch(phoneBook, names);
        doJumpSearch(phoneBook, names, linearSearchTime);
    }

    private void doJumpSearch(List<PhoneBookEntry> phoneBook,
                                              List<String> names,
                                              long linearSearchTime) {
        System.out.println("Start searching (bubble sort + jump search)...");
        long sortStart = System.currentTimeMillis();
        List<PhoneBookEntry> sortedPhoneBook = bubbleSort(phoneBook, start, linearSearchTime);
        long sortStop = System.currentTimeMillis();
        System.out.printf("Found %d / %d entries. ", phoneBook.size(), phoneBook.size());

        if (sortedPhoneBook.isEmpty()) {
            long searchStart = System.currentTimeMillis();
            linearSearch(phoneBook, names);
            long searchStop = System.currentTimeMillis();
            printTime("Time taken", sortStart, searchStop, true);
            printTime("Sorting time", sortStart, sortStop, false);
            System.out.println(" - STOPPED, moved to linear search");
            printTime("Searching time", searchStart, searchStop, true);
        } else {
            //     search with jump search, grab start and stop times
            long searchStart = System.currentTimeMillis();
            jumpSearch();
            //     print total time taken
            //     print sorting time
            //     print searching time
        }

        return sortedPhoneBook;
    }

    private List<PhoneBookEntry> bubbleSort(List<PhoneBookEntry> phoneBook,
                                            long start,
                                            long linearSearchTime) {
        for (int i = 0; i < phoneBook.size() - 1; i++) {
            for (int j = phoneBook.size() - 2; j >= i; j--) {
                if (System.currentTimeMillis() - start > 10 * linearSearchTime) {
                    // Signal caller that sort was aborted
                    return new ArrayList<>();
                }

                // if name at j is "greater than" name at j + 1...
                if (phoneBook.get(j).getName().compareTo(phoneBook.get(j + 1).getName()) > 0) {
                    // swap
                    PhoneBookEntry entry = phoneBook.remove(j + 1);
                    phoneBook.add(j, entry);
                }
            }
        }

        return phoneBook;
    }

    private long doLinearSearch(List<PhoneBookEntry> phoneBook, List<String> names) {
        System.out.println("Start searching (linear search)...");
        long start = System.currentTimeMillis();
        linearSearch(phoneBook, names);
        long stop = System.currentTimeMillis();
        System.out.printf("Found %d / %d entries. ", phoneBook.size(), phoneBook.size());
        printTime("Time taken", start, stop, true);

        return stop - start;
    }

    private void printTime(String heading, long start, long stop, boolean endWithNl) {
        LocalTime time = LocalTime.ofNanoOfDay((stop - start) * NANO_PER_MILLI);
        System.out.printf("%s: %d min. %d sec. %d ms.",
                heading,
                time.getMinute(),
                time.getSecond(),
                time.getNano() / NANO_PER_MILLI);

        if (endWithNl) {
            System.out.println();
        }
    }

    private void linearSearch(List<PhoneBookEntry> phoneBook, List<String> names) {
        for (String name : names) {
            for (PhoneBookEntry phoneBookEntry : phoneBook) {
                if (name.equals(phoneBookEntry.getPhoneNumber())) {
                    break;
                }
            }
        }
    }

    private List<String> loadLinesFromFile(String fileName) {
        List<String> lines = new ArrayList<>();

        try {
            lines = Files.readAllLines(Paths.get(fileName));
        } catch (IOException e) {
            System.out.println("Could not read " + fileName);
        }

        return lines;
    }

    private List<PhoneBookEntry> loadPhoneBook(String fileName) {
        List<String> lines = loadLinesFromFile(fileName);
        List<PhoneBookEntry> phoneBook = new ArrayList<>();

        for (String line : lines) {
            String[] parts = line.split("\\s+");
            phoneBook.add(new PhoneBookEntry(parts[0], parts[1]));
        }

        return phoneBook;
    }
}
