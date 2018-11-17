package main.java.Objects;

import main.java.CustomImplamentation.*;

import java.util.ArrayList;
import java.util.Random;

public class Vendor implements Runnable{

    private int id;
    private ArrayList<String> catalog;
    private HashTable hashTable;

    public Vendor(int id, HashTable ht) {
        this.id = id;
        this.hashTable = ht;
        catalog = ht.getAll();
    }

    private void changeGuitarPrice() {
        Random random = new Random();
        Guitar guitar = hashTable.search(catalog.get(random.nextInt(catalog.size())));
        double oldPrice = guitar.getPrice();
        double newPrice = hashTable.changePrice(guitar);
        if (newPrice > oldPrice)
            System.out.println("Vendor " + id + " raised the price of the " + guitar.getName() + " by $" + (newPrice - oldPrice));
        else
            System.out.println("Vendor " + id + " lowered the price of the " + guitar.getName() + " by $" + (oldPrice - newPrice));

    }

    public void run() {
        /*Random random = new Random();
        for(;;) {
            try {
                Thread.sleep(random.nextInt(5000));
            } catch (InterruptedException e) {
                System.out.println("\nVendor " + id + " has left\n");
                return;
            }

        }*/
        changeGuitarPrice();
    }
}
