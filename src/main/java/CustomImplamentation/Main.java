package main.java.CustomImplamentation;

import main.java.Objects.*;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    static int threads = (Runtime.getRuntime().availableProcessors() > 32) ? 32 : Runtime.getRuntime().availableProcessors();
    static int vendors = 1;

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
                .threads(Runtime.getRuntime().availableProcessors())
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

            if (executor.awaitTermination(60, TimeUnit.SECONDS)) {
                System.out.println("\nStores are now closed!\n");
            } else {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.out.println("Store was robbed, everyone out.");
            e.printStackTrace();
        }
    }

}
