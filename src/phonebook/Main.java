package phonebook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    private static final String PREFIX = "/home/knute/Documents/hyperskill/Phone-Book/";
    private static final int NANO_PER_MILLI = 1_000_000;

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        PhoneBookEntry[] phoneBook = loadPhoneBook(PREFIX + "directory.txt");
        String[] names = loadLinesFromFile(PREFIX + "find.txt");
        long linearSearchTime = doLinearSearch(phoneBook, names);
        doJumpSearch(phoneBook, names, linearSearchTime);
        doBinarySearch(phoneBook, names);
        doHashSearch(phoneBook, names);
    }

    private void doHashSearch(PhoneBookEntry[] phoneBook, String[] names) {
        System.out.println();
        System.out.println("Start searching (hash table)...");
        long createStart = System.currentTimeMillis();
        HashTable<Integer> table = new HashTable<>(phoneBook.length);

        for (PhoneBookEntry entry : phoneBook) {
            if (!table.put(entry.getName(), entry.getPhoneNumber())) {
                System.out.printf("Problem putting name = %s, number = %d%n",
                        entry.getName(),
                        entry.getPhoneNumber());
            }
        }

        long createStop = System.currentTimeMillis();
        System.out.printf("Found %d / %d entries. ", names.length, names.length);
        long searchStart = System.currentTimeMillis();

        for (String name : names) {
            Integer phoneNumber = table.get(name);

            if (phoneNumber == null) {
                System.out.printf("Couldn't get %s%n", name);
            }
        }

        long searchStop = System.currentTimeMillis();
        printTime("Time taken", createStart, searchStop, true);
        printTime("Creating time", createStart, createStop, true);
        printTime("Searching time", searchStart, searchStop, true);
    }

    private void doBinarySearch(PhoneBookEntry[] phoneBook, String[] names) {
        System.out.println();
        System.out.println("Start searching (quick sort + binary search)...");
        long sortStart = System.currentTimeMillis();
        quickSort(phoneBook, 0, phoneBook.length - 1); // phoneBook changes as a side effect
        long sortStop = System.currentTimeMillis();
        System.out.printf("Found %d / %d entries. ", names.length, names.length);
        long searchStart = System.currentTimeMillis();

        for (String name : names) {
            binarySearch(phoneBook, name, 0, phoneBook.length - 1);
        }

        long searchStop = System.currentTimeMillis();
        printTime("Time taken", sortStart, searchStop, true);
        printTime("Sorting time", sortStart, sortStop, true);
        printTime("Searching time", searchStart, searchStop, true);
    }

    private int binarySearch(PhoneBookEntry[] array, String elem, int left, int right) {
        if (left > right) {
            return -1; // search interval is empty, the element is not found
        }

        int mid = left + (right - left) / 2; // the index of the middle element

        if (elem.equals(array[mid].getName())) {
            return mid; // the element is found, return its index
        } else if (elem.compareTo(array[mid].getName()) < 0) {
            return binarySearch(array, elem, left, mid - 1); // go to the left subarray
        } else {
            return binarySearch(array, elem, mid + 1, right); // go the the right subarray
        }
    }

    private void quickSort(PhoneBookEntry[] array, int left, int right) {
        if (left < right) {
            int pivotIndex = partition(array, left, right); // the pivot is already on its place
            quickSort(array, left, pivotIndex - 1);  // sort the left subarray
            quickSort(array, pivotIndex + 1, right); // sort the right subarray
        }
    }

    private int partition(PhoneBookEntry[] array, int left, int right) {
        PhoneBookEntry pivot = array[right];  // choose the rightmost element as the pivot
        int partitionIndex = left; // the first element greater than the pivot

        /* move large values into the right side of the array */
        for (int i = left; i < right; i++) {
            if (array[i].getName().compareTo(pivot.getName()) <= 0) { // may be used '<' as well
                swap(array, i, partitionIndex);
                partitionIndex++;
            }
        }

        swap(array, partitionIndex, right); // put the pivot on a suitable position

        return partitionIndex;
    }

    private void swap(PhoneBookEntry[] array, int i, int j) {
        PhoneBookEntry temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    private void doJumpSearch(PhoneBookEntry[] phoneBook, String[] names, long linearSearchTime) {
        System.out.println();
        System.out.println("Start searching (bubble sort + jump search)...");
        long sortStart = System.currentTimeMillis();
        PhoneBookEntry[] sortedPhoneBook = bubbleSort(phoneBook, sortStart, linearSearchTime);
        long sortStop = System.currentTimeMillis();
        System.out.printf("Found %d / %d entries. ", names.length, names.length);

        // Aborted sort?
        if (sortedPhoneBook.length == 0) {
            long searchStart = System.currentTimeMillis();
            linearSearch(phoneBook, names);
            long searchStop = System.currentTimeMillis();
            printTime("Time taken", sortStart, searchStop, true);
            printTime("Sorting time", sortStart, sortStop, false);
            System.out.println(" - STOPPED, moved to linear search");
            printTime("Searching time", searchStart, searchStop, true);

            return;
        }

        long searchStart = System.currentTimeMillis();

        for (String name : names) {
            jumpSearch(sortedPhoneBook, name);
        }

        long searchStop = System.currentTimeMillis();
        printTime("Time taken", sortStart, searchStop, true);
        printTime("Sorting time", sortStart, sortStop, true);
        printTime("Searching time", searchStart, searchStop, true);
    }

    private PhoneBookEntry[] bubbleSort(PhoneBookEntry[] phoneBook, long start, long linearSearchTime) {
        PhoneBookEntry[] sortedPhoneBook = new PhoneBookEntry[phoneBook.length];
        System.arraycopy(phoneBook, 0, sortedPhoneBook, 0, phoneBook.length);

        for (int i = 0; i < sortedPhoneBook.length - 1; i++) {
            for (int j = sortedPhoneBook.length - 2; j >= i; j--) {
                if (System.currentTimeMillis() - start > 10 * linearSearchTime) {
                    // Signal caller that sort was aborted
                    return new PhoneBookEntry[0];
                }

                // if name at j is "greater than" name at j + 1...
                if (sortedPhoneBook[j].getName().compareTo(sortedPhoneBook[j + 1].getName()) > 0) {
                    // swap
                    PhoneBookEntry tempEntry = sortedPhoneBook[j];
                    sortedPhoneBook[j] = sortedPhoneBook[j + 1];
                    sortedPhoneBook[j + 1] = tempEntry;
                }
            }
        }

        return sortedPhoneBook;
    }

    private long doLinearSearch(PhoneBookEntry[] phoneBook, String[] names) {
        System.out.println("Start searching (linear search)...");
        long start = System.currentTimeMillis();
        linearSearch(phoneBook, names);
        long stop = System.currentTimeMillis();
        System.out.printf("Found %d / %d entries. ", names.length, names.length);
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

    private void linearSearch(PhoneBookEntry[] phoneBook, String[] names) {
        for (String name : names) {
            for (PhoneBookEntry phoneBookEntry : phoneBook) {
                if (name.equals(phoneBookEntry.getName())) {
                    break;
                }
            }
        }
    }

    private String[] loadLinesFromFile(String fileName) {
        List<String> lines = new ArrayList<>();

        try {
            lines = Files.readAllLines(Paths.get(fileName));
        } catch (IOException e) {
            System.out.println("Could not read " + fileName);
        }

        return lines.toArray(new String[0]);
    }

    @SuppressWarnings("SameParameterValue")
    private PhoneBookEntry[] loadPhoneBook(String fileName) {
        String[] lines = loadLinesFromFile(fileName);
        PhoneBookEntry[] phoneBook = new PhoneBookEntry[lines.length];

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String[] parts = line.split("\\s+");
            int phoneNumber = Integer.parseInt(parts[0]);
            String name = Arrays.stream(parts)
                    .skip(1)
                    .collect(Collectors.joining(" "));
            phoneBook[i] = new PhoneBookEntry(phoneNumber, name);
        }

        return phoneBook;
    }

    private int jumpSearch(PhoneBookEntry[] array, String target) {
        int currentRight = 0; // right border of the current block
        int prevRight = 0; // right border of the previous block

        /* If array is empty, the element is not found */
        if (array.length == 0) {
            return -1;
        }

        /* Check the first element */
        if (array[currentRight].getName().equals(target)) {
            return 0;
        }

        /* Calculating the jump length over array elements */
        int jumpLength = (int) Math.sqrt(array.length);

        /* Finding a block where the element may be present */
        while (currentRight < array.length - 1) {

            /* Calculating the right border of the following block */
            currentRight = Math.min(array.length - 1, currentRight + jumpLength);

            if (array[currentRight].getName().compareTo(target) >= 0) {
                break; // Found a block that may contain the target element
            }

            prevRight = currentRight; // update the previous right block border
        }

        /* If the last block is reached and it cannot contain the target value => not found */
        if (currentRight == array.length - 1 && target.compareTo(array[currentRight].getName()) > 0) {
            return -1;
        }

        /* Doing linear search in the found block */
        return backwardSearch(array, target, prevRight, currentRight);
    }

    private static int backwardSearch(PhoneBookEntry[] array, String target, int leftExcl, int rightIncl) {
        for (int i = rightIncl; i > leftExcl; i--) {
            if (array[i].getName().compareTo(target) == 0) {
                return i;
            }
        }
        return -1;
    }
}
