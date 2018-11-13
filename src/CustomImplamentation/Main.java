package CustomImplamentation;

import Objects.Customer;
import Objects.Vendor;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    static int threads = (Runtime.getRuntime().availableProcessors() > 32) ? 32 : Runtime.getRuntime().availableProcessors();
    static int vendors = 4;

    public static void main(String[] args) {

        Parser p = new Parser();
        HashTable guitars = new HashTable();
        try {
            p.parse("https://www.normansrareguitars.com/product-category/guitars/", guitars);
            for (int i = 2; i < 11; i++) {
                p.parse("https://www.normansrareguitars.com/product-category/guitars/page/" + i, guitars);
            }
        } catch (IOException e) {
            System.out.println("Could not reach web page.");
        }

        run(guitars);

    }

    private static void run(HashTable hashTable) {
        try {
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            for(int i = 0; i < Runtime.getRuntime().availableProcessors() - vendors; i++) {
                Customer customer = new Customer(i, hashTable);
                executor.execute(customer);
            }

            for (int j = 0; j < vendors; j++) {
                Vendor vendor = new Vendor(j, hashTable);
                executor.execute(vendor);
            }

            executor.awaitTermination(60, TimeUnit.SECONDS);
            executor.shutdownNow();
            System.out.println("\nStores are now closed!");
        } catch (InterruptedException e) {
            System.out.println("Store was robbed, everyone out.");
            e.printStackTrace();
        }
    }

}
