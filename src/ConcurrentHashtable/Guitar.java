package ConcurrentHashtable;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class Guitar {
    private String name;
    private double price;


    Guitar() {
        name = "";
        price = 0.0;
    }

    //Getters

    String getName() {
        return name;
    }

    double getPrice() {
        return price;
    }


    //Setters

    void setName(String name) {
        this.name = name;
    }

    void setPrice(String price) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        try {
            Number number = format.parse(price);
            this.price = number.doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return getName() + ", "  + "$" + getPrice();
    }

}

