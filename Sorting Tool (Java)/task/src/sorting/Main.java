package sorting;

import java.io.*;
import java.util.*;

public class Main {
    public static final String SORT_TYPE = "-sortingType";
    public static final String BY_COUNT = "byCount";
    public static final String NATURAL = "natural";
    public static final String NO_SORTING_TYPE_DEFINED = "No sorting type defined!";
    public static final String DATA_TYPE = "-dataType";
    public static final String WORD = "word";
    public static final String LINE = "line";
    public static final String LONG = "long";
    public static final String NO_DATA_TYPE_DEFINED = "No data type defined!";
    public static final String INPUT_FILE = "-inputFile";
    public static final String OUTPUT_FILE = "-outputFile";
    public static final String NO_INPUT_FILE_DEFINED = "No input file defined.";
    public static final String NO_OUTPUT_FILE_DEFINED = "No output file defined.";
    public static String inpType = "word";
    public static String maxWord = "longest";
    public static String separator = " ";
    public static boolean isNaturalSorting = true;
    public static Scanner scanner;
    public static PrintStream outStream;


    public static void main(final String[] args) throws FileNotFoundException {


        ArrayList<String> strings = new ArrayList<>();
        ArrayList<MyPair<String>> stringsMap = new ArrayList<>();

        ArrayList<Long> longs = new ArrayList<>();
        ArrayList<MyPair<Long>> longsMap = new ArrayList<>();

        ArrayList<String> argList = new ArrayList<>(Arrays.asList(args));

        for (String arg : argList) {
            if (arg.charAt(0) == '-' && !arg.equals(SORT_TYPE) && !arg.equals(DATA_TYPE) &&
                    !arg.equals(INPUT_FILE) && !arg.equals(OUTPUT_FILE))
                System.out.println("\"" + arg + "\" is not a valid parameter. It will be skipped.");
        }

        int inpFilePos = argList.indexOf(INPUT_FILE);
        if (inpFilePos >= 0) {
            if (inpFilePos < (argList.size() - 1)) {
                File inpFile = new File(argList.get(inpFilePos + 1));
                if (inpFile.exists()) scanner = new Scanner(inpFile);
            } else {
                System.out.println(NO_INPUT_FILE_DEFINED);
                scanner = new Scanner(System.in);
            }
        } else scanner = new Scanner(System.in);

        int outFilePos = argList.indexOf(OUTPUT_FILE);
        if (outFilePos >= 0) {
            if (outFilePos < (argList.size() - 1)) {
                File outFile = new File(argList.get(outFilePos + 1));
                outStream = new PrintStream(outFile);
            } else {
                System.out.println(NO_OUTPUT_FILE_DEFINED);
                outStream = System.out;
            }
        } else outStream = System.out;

        int sortIntegersPos = argList.indexOf(SORT_TYPE);
        if (sortIntegersPos >= 0) {
            if (sortIntegersPos < (argList.size() - 1)) {
                if (argList.get(sortIntegersPos + 1).equals(BY_COUNT)) {
                    isNaturalSorting = false;
                } else if (!argList.get(sortIntegersPos + 1).equals(NATURAL)) {
                    System.out.println(NO_SORTING_TYPE_DEFINED);
                }
            } else {
                System.out.println(NO_SORTING_TYPE_DEFINED);
            }
        }

        int dataTypePos = argList.indexOf(DATA_TYPE);
        if (dataTypePos >= 0) {
            if (dataTypePos < argList.size() - 1) {
                switch (argList.get(dataTypePos + 1)) {
                    case WORD -> {
                        readWords(strings);
                        processData(strings, stringsMap);
                    }
                    case LINE -> {
                        inpType = "line";
                        separator = "\n";
                        readLines(strings);
                        processData(strings, stringsMap);
                    }
                    case LONG -> {
                        inpType = "number";
                        maxWord = "greatest";
                        readLongs(longs);
                        processData(longs, longsMap);
                    }
                    default -> System.out.println(NO_DATA_TYPE_DEFINED);
                }
            } else {
                System.out.println(NO_DATA_TYPE_DEFINED);
            }
        } else {
            readWords(strings);
            processData(strings, stringsMap);
        }

        scanner.close();
        outStream.close();
    }

    private static <T extends Comparable<T>> void processData(ArrayList<T> strings, ArrayList<MyPair<T>> stringsMap) {
        if (isNaturalSorting) {
            strings.sort(new MyComparator<>());
            printNatural(strings);
        }
        else {
            convertToMap(strings, stringsMap);
            stringsMap.sort(new MyComparator<>());
            printByCount(stringsMap, strings.size());
        }
    }

    private static <T extends Comparable> void printByCount(ArrayList<MyPair<T>> map, int size) {
        outStream.println("Total " + inpType + "s: " + size + ".");
        for (MyPair<T> pair : map) {
            outStream.println(pair.key + ": " + pair.num + " time(s), " + (pair.num * 100 / size) + "%");
        }
    }

    private static <T> void printNatural(ArrayList<T> list) {
        outStream.println("Total " + inpType + "s: " + list.size() + ".");
        outStream.print("Sorted data:");
        for (T elem : list) outStream.print(separator + elem);
        outStream.println();
    }

    private static <T extends Comparable> void convertToMap(ArrayList<T> list, ArrayList<MyPair<T>> map) {
        Map<T, Integer> res = new HashMap<>();

        for (T elem : list) {
            if (res.containsKey(elem)) {
                res.put(elem, res.get(elem) + 1);
            } else res.put(elem, 1);
        }

        for (Map.Entry<T, Integer> entry : res.entrySet()) {
            map.add(new MyPair<>(entry.getKey(), entry.getValue()));
        }
    }

    private static void readWords(ArrayList<String> words) {
        while (scanner.hasNext()) {
            String word = scanner.next();
            words.add(word);
        }
    }

    private static void readLines(ArrayList<String> lines) {
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            lines.add(line);
        }
    }

    private static void readLongs(ArrayList<Long> longs) {
        while (scanner.hasNext()) {
            String word = scanner.next();
            try {
                long number = Integer.parseInt(word);
                longs.add(number);
            } catch (NumberFormatException e) {
                System.out.println("\"" + word + "\" is not a long. It will be skipped.");
            }
        }
    }
}

// My class to save pairs: element - number of occurrences
class MyPair<T extends Comparable> {
    T key;
    int num;

    public MyPair(T key, int num) {
        this.key = key;
        this.num = num;
    }
}

// My comparator to compare longs and strings naturally
// and MyPair as defined in the task: first by num value, then by key
class MyComparator <T> implements Comparator<T> {

    @Override
    public int compare(T t1, T t2) {


        long diff;
        if (t1 instanceof Long) {
            diff = (long) t1 - (long) t2;
            if (diff < 0) return -1;
            if (diff > 0) return 1;
            return 0;
        }
        if (t1 instanceof String)
            return  ((String) t1).compareTo((String) t2);
        if (t1 instanceof MyPair<?>) {
            if (((MyPair<?>) t1).num > ((MyPair<?>) t2).num) return 1;
            if (((MyPair<?>) t1).num < ((MyPair<?>) t2).num) return -1;
            return ((MyPair<?>) t1).key.compareTo(((MyPair<?>) t2).key);
        }

        return 0;
    }
}