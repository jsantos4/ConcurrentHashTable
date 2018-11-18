package main.java.Benchmark;

import com.sun.java.accessibility.util.GUIInitializedListener;
import main.java.CustomImplamentation.*;
import main.java.Objects.*;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
public class Benchmark {
    private HashTable hashTable = new HashTable();
    private ConcurrentHashMap<String, Guitar> hashMap = new ConcurrentHashMap<>();
    private Parser parser = new Parser();

    private ExecutorService executor;
    private static int threads = (Runtime.getRuntime().availableProcessors() > 32) ? 32 : Runtime.getRuntime().availableProcessors();
    private ArrayList<Vendor> vendors = new ArrayList<>();
    private ArrayList<Customer> customers = new ArrayList<>();
    private ArrayList<String> catalog = new ArrayList<>();
    private Random random = new Random();

    @Setup
    public void setup() {
        try {
            parser.parse("https://www.normansrareguitars.com/product-category/guitars/", hashTable, hashMap);
            for (int i = 2; i < 11; i++) {
                parser.parse("https://www.normansrareguitars.com/product-category/guitars/page/" + i, hashTable, hashMap);
            }
        } catch (IOException e) {
            System.out.println("Could not reach web page.");
        }

        catalog = hashTable.getAll();

        for (int i = 0; i < threads; i++) {
            Customer customer = new Customer(i, hashTable);
            customers.add(customer);
        }

        for (int i = 0; i < threads/4; i++) {
            Vendor vendor = new Vendor(i, hashTable);
            vendors.add(vendor);
        }
    }

    @org.openjdk.jmh.annotations.Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void benchCustomGet() {
        executor = Executors.newFixedThreadPool(customers.size());
        for (Customer customer : customers) {
            executor.execute(customer);

        }
        executor.shutdown();
    }

    @org.openjdk.jmh.annotations.Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void benchJDKGet() {
        executor = Executors.newFixedThreadPool(customers.size());
        Runnable c = new Runnable() {
            @Override
            public void run() {
                 Guitar guitar = hashMap.get(catalog.get(random.nextInt(catalog.size())));
            }
        };

        for (Customer customer : customers) {
            executor.execute(c);

        }
        executor.shutdown();
    }

    @org.openjdk.jmh.annotations.Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void benchCustomChangePrice() {
        executor = Executors.newFixedThreadPool(threads);
        for (Vendor vendor : vendors) {
            executor.execute(vendor);

        }
        executor.shutdown();
    }

    @org.openjdk.jmh.annotations.Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void benchJDKChangePrice() {
        executor = Executors.newFixedThreadPool(threads);
        Runnable v = new Runnable() {
            @Override
            public void run() {
                Guitar guitar = hashMap.get(catalog.get(random.nextInt(catalog.size())));
                hashMap.put(guitar.getName(), guitar);
            }
        };

        for (Vendor vendor : vendors) {
            executor.execute(v);
        }
        executor.shutdown();
    }

    @TearDown
    public void tearDown() {
        for (Customer customer : customers) {
            customer.hashTable = null;
            customer.catalog = null;
        }

        for (Vendor vendor : vendors) {
            vendor.hashTable = null;
            vendor.catalog = null;
        }

    }
}
