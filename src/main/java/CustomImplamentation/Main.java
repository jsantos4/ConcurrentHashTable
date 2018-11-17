package main.java.CustomImplamentation;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import main.java.Objects.*;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    static int threads = (Runtime.getRuntime().availableProcessors() > 32) ? 32 : Runtime.getRuntime().availableProcessors();
    static int vendors = 4;

    public static void main(String[] args) {

        Parser p = new Parser();
        HashTable guitars = new HashTable();
        ConcurrentHashMap<String, Guitar> hashMap = new ConcurrentHashMap<>();
        try {
            p.parse("https://www.normansrareguitars.com/product-category/guitars/", guitars, hashMap);
            for (int i = 2; i < 11; i++) {
                p.parse("https://www.normansrareguitars.com/product-category/guitars/page/" + i, guitars, hashMap);
            }
        } catch (IOException e) {
            System.out.println("Could not reach web page.");
        }

        final Options options = new OptionsBuilder()
                .include(Benchmark.class.getSimpleName())
                .forks(1)
                .threads(threads)
                .warmupIterations(5)
                .measurementIterations(5)
                .resultFormat(ResultFormatType.CSV)
                .result("results.csv")
                .build();


        try {
            new Runner(options).run();
        } catch (RunnerException e) {
            e.printStackTrace();
        }


        buildHTML();
    }

    private static void run(HashTable hashTable) {
        try {
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            for(int i = 0; i < threads - vendors; i++) {
                Customer customer = new Customer(i, hashTable);
                executor.execute(customer);
            }

            for (int j = 0; j < vendors; j++) {
                Vendor vendor = new Vendor(j, hashTable);
                executor.execute(vendor);
            }

            if (executor.awaitTermination(15, TimeUnit.SECONDS)) {
                System.out.println("\nStores are now closed!\n");
            } else {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.out.println("Store was robbed, everyone out.");
            e.printStackTrace();
        }
    }

    private static void buildHTML() {
        File f = new File("results.csv");
        ArrayList<String> lines = new ArrayList<String>();
        ArrayList<Double> scores = new ArrayList<>();

        try {

            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = br.readLine();

            int count = 0;
            while (line != null) {
                if (count != 0) {
                    lines.add(line);
                }
                count++;
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            System.out.println("No such file");
        }


        for (String line : lines) {
            double score = Double.parseDouble(line.split(",")[4]);
            scores.add(score);
        }

        String contents = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>csc375hw02 results</title>\n" +
                "\n" +
                "    <link rel=\"stylesheet\" href=\"results.css\">\n" +
                "\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<h1>Results</h1>\n" +
                "<h4>Benchmark results for reads/writes into custom hashtable and jdk's hashmap in ops-per-second<h4>\n" +
                "<div class=\"BarTable\">\n" +
                "    <table>\n" +
                "        <caption>Operations per Second</caption>\n" +
                "        <tr>\n" +
                "            <td><div class=\"BarLabel\"\">Custom Get</div></td>\n" +
                "            <td class=\"BarFull\">\n" + getResultBar(scores.get(1)) +
                "            </td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td><div class=\"BarLabel\"\">JDK Get</div></td>\n" +
                "            <td class=\"BarFull\">\n" + getResultBar(scores.get(3)) +
                "            </td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td><div class=\"BarLabel\"\">Custom Change Price</div></td>\n" +
                "            <td class=\"BarFull\">\n" + getResultBar(scores.get(0)) +
                "            </td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td><div class=\"BarLabel\"\">JDK Change Price</div></td>\n" +
                "            <td class=\"BarFull\">\n" + getResultBar(scores.get(2)) +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "</div>";

        File file = new File("results.html");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(contents);
            bw.close();
        } catch (IOException e) {
            System.out.println("shit");
        }


    }

    private static String getResultBar(double score) {
        double scoreAW = score * 10;
        String bar = "<img src= \"rec.png\" height = \"12\" alt=\"" + String.valueOf(score) + "\" width=\"" + String.valueOf(scoreAW) +
                "\" />\n" +
                "<p>" + String.valueOf(score) + "</p>";


        return bar;
    }

}
