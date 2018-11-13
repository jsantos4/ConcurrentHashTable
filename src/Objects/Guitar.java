package Objects;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Random;

public class Guitar {
    private String name;
    private double price;


    public Guitar() {
        name = "";
        price = 0.0;
    }

    //Getters

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }


    //Setters

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        try {
            Number number = format.parse(price);
            this.price = number.doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //Randomly change price of item (for vendors)
    public double changePrice() {
        Random random = new Random();
        int delta = random.nextInt(100);
        if (random.nextInt(2) == 1) {
            price = price + delta;
        } else {
            price = price - delta;
        }

        return price;
    }

    public String toString() {
        return getName() + ", "  + "$" + getPrice();
    }

}

