package Objects;

import CustomImplamentation.HashTable;

import java.util.ArrayList;
import java.util.Random;

public class Customer implements Runnable {

    private int id;
    private ArrayList<String> catalog;
    private HashTable hashTable;

    public Customer(int id, HashTable ht) {
        this.id = id;
        this.hashTable = ht;
        catalog = ht.getAll();
    }

    private void shop() {
        Random random = new Random();
        Guitar guitar = hashTable.search(catalog.get(random.nextInt(catalog.size())));
        System.out.println("Shopper " + id + " is currently playing the " + guitar.getName());
    }
    public void run() {
        Random random = new Random();
        for(;;) {
            try {
                Thread.sleep(random.nextInt(5000));
            } catch (InterruptedException e) {
                System.out.println("Shopper " + id + "has left");
            }

            shop();
        }
    }
}
