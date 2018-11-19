package main.java.Objects;

import main.java.CustomImplamentation.*;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Vendor implements Runnable{

    private int id;
    public ArrayList<String> catalog;
    public HashTable hashTable;

    public Vendor(int id, HashTable ht) {
        this.id = id;
        this.hashTable = ht;
        catalog = ht.getAll();
    }

    private void changeGuitarPrice() {
        Guitar guitar = hashTable.search(catalog.get(ThreadLocalRandom.current().nextInt(catalog.size())));
        double oldPrice = guitar.getPrice();
        double newPrice = hashTable.changePrice(guitar);
        /*if (newPrice > oldPrice)
            System.out.println("Vendor " + id + " raised the price of the " + guitar.getName() + " by $" + (newPrice - oldPrice));
        else
            System.out.println("Vendor " + id + " lowered the price of the " + guitar.getName() + " by $" + (oldPrice - newPrice));
        */

    }

    public void run() {
        /*for(;;) {
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(5000));
            } catch (InterruptedException e) {
                System.out.println("\nVendor " + id + " has left\n");
                return;
            }

        }*/
        changeGuitarPrice();
    }
}
