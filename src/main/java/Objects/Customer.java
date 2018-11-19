package main.java.Objects;

import main.java.CustomImplamentation.*;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Customer implements Runnable {

    private int id;
    public ArrayList<String> catalog;
    public HashTable hashTable;

    public Customer(int id, HashTable ht) {
        this.id = id;
        this.hashTable = ht;
        catalog = ht.getAll();
    }

    private void shop() {
        Guitar guitar = hashTable.search(catalog.get(ThreadLocalRandom.current().nextInt(catalog.size())));
        //System.out.println("Shopper " + id + " is currently playing the " + guitar.getName());
    }
    public void run() {
        /*for(;;) {
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(5000));
            } catch (InterruptedException e) {
                System.out.println("\nShopper " + id + " has left\n");
                return;
            }

        }*/
        shop();
    }
}
