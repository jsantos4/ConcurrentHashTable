package main.java.Objects;

import main.java.CustomImplamentation.*;

import java.util.ArrayList;
import java.util.Random;

public class Customer implements Runnable {

    private int id;
    public ArrayList<String> catalog;
    public HashTable hashTable;
    private Random random = new Random();

    public Customer(int id, HashTable ht) {
        this.id = id;
        this.hashTable = ht;
        catalog = ht.getAll();
    }

    private void shop() {
        Guitar guitar = hashTable.search(catalog.get(random.nextInt(catalog.size())));
        //System.out.println("Shopper " + id + " is currently playing the " + guitar.getName());
    }
    public void run() {
        shop();
    }
}
