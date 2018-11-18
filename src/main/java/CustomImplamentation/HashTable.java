package main.java.CustomImplamentation;

import com.sun.java.accessibility.util.GUIInitializedListener;
import main.java.Objects.*;

import java.lang.management.GarbageCollectorMXBean;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class HashTable {

    //define node for linked lists in table
    static final class node {
        String key;
        Guitar value;
        node next;
        //ReadWriteLock lock;
        node(String key, Guitar value, node next) {
            this.key = key;
            this.value = value;
            this.next = next;
            //lock = new ReadWriteLock();
        }
    }

    //local variables
    private static node[] table;
    private static final int initialcap = 4;
    private AtomicInteger count;
    //private ReadWriteLock tableLock = new ReadWriteLock();
    private AtomicBoolean resizing = new AtomicBoolean(false);

    public HashTable() {
        table = new node[initialcap];
        count = new AtomicInteger(0);
    }

    //Hashes guitar names
    private int hash(String k, int size) {
        int sum = 0;
        for(int i = 0; i < k.length(); i++) {   //Run through each character in name(key)

            char a = k.charAt(i);
            int ascii = (int) a;        //Get ascii value and sum up
            sum = sum + ascii;
        }
        return sum % size;          //Return modulo of ascii total of key
    }

    //Inserts guitar objects into table
    public void put(Guitar b) {
        for (;;) {
            if (!resizing.get()){

                if ((count.get() / table.length) >= 0.75) {   //If table is 75% full
                    resizing.getAndSet(true);
                    resize();                           //Call resize()
                    resizing.getAndSet(false);
                }

                String key = b.getName();

                int hashcode = hash(key, table.length);     //Hash key
                int i = hashcode & table.length - 1;
                node bucketHead = table[i];
                for (node e = bucketHead; e != null; e = e.next) {    //Run through nodes in index until the end
                    if (key.equals(e.key)) {
                        e.value = b;    //Set node's value
                        count.compareAndSet(count.get(), count.get() + 1);        //Increment hash table counter
                        return;
                    }
                }
                node p = new node(key, b, table[i]);    //Create node at index if empty
                table[i] = p;
                count.compareAndSet(count.get(), count.get() + 1);
                return;
            }
        }


    }

    //Changes price of guitar in data structure
    public double changePrice(Guitar guitar) {
        for (;;) {
            if (!resizing.get()) {
                if (search(guitar.getName()) != null) {
                    guitar.changePrice();
                }
            }
            return guitar.getPrice();
        }
    }

    //Returns all keys
    public ArrayList<String> getAll() {
        for (;;) {
            if (!resizing.get()) {
                ArrayList list = new ArrayList();
                for (int i = 0; i < table.length; i++) {
                    for (node e = table[i]; e != null; e = e.next) {    //Run through whole table
                        list.add(e.key);        //Insert node into arrayList
                    }
                }
                return list;    //Return arraylist
            }

        }
    }

    //Returns if element in table is empty
    private boolean isEmpty(node e) {
        return e == null;
    }

    //Returns value of a searched key
    public Guitar search(String k) {
        for (;;) {
            if (!resizing.get()) {
                int hashcode = hash(k, table.length);
                int i = hashcode & table.length - 1;    //Hash key
                node bucketHead = table[i];
                for (node e = bucketHead; !isEmpty(e); e = e.next) {
                    if (k.equals(e.key)) {
                        return e.value;         //Run through index's nodes and return searched guitar
                    }
                }
                return null;
            }
        }
    }

    //doubles table size when three quarters load is reached
    private void resize() {
        //tableLock.lockWrite();
        node[] newTable = new node[table.length*2];     //Create new table with twice size of original
        //Can't use put, essentially had to rewrite and tweak
        //the put method to remap all elements of the old table
        for (int i = 0; i < table.length; i++) {
            node bucketHead = table[i];
            if (bucketHead != null)
                //bucketHead.lock.lockWrite();
            for (node e = bucketHead; e != null; e = e.next) {        //Run through table
                int hc = hash(e.key, newTable.length);      //Rehash node
                if (newTable[hc] == null){          //If index is empty
                    node p = new node(e.key, e.value, newTable[hc]);
                    newTable[hc] = p;           //Place node in index
                    //bucketHead.lock.unlockWrite();
                } else {        //If not
                    node f = newTable[hc];
                    while(!isEmpty(f)) {
                        f = f.next;     //Run through nodes(bucket)
                    }
                    node p = new node(e.key, e.value, f);       //Place node at end of bucket
                    f = p;
                    //bucketHead.lock.unlockWrite();
                }
            }
        }
        table = newTable;       //Set table to new table
        //tableLock.unlockWrite();

    }
}

