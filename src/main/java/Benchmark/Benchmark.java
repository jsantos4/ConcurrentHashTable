package main.java.Benchmark;

import main.java.CustomImplamentation.*;
import main.java.Objects.*;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class Benchmark {
    private HashTable hashTable = new HashTable();
    private ConcurrentHashMap<String, Guitar> hashMap = new ConcurrentHashMap<>();
    private Parser parser = new Parser();

    public void go() {
        try {
            parser.parse("https://www.normansrareguitars.com/product-category/guitars/", hashTable, hashMap);
            for (int i = 2; i < 11; i++) {
                parser.parse("https://www.normansrareguitars.com/product-category/guitars/page/" + i, hashTable, hashMap);
            }
        } catch (IOException e) {
            System.out.println("Could not reach web page.");
        }
    }



}
