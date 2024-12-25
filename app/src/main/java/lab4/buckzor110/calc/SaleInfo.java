package lab4.buckzor110.calc;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class SaleInfo implements Writable {
    private double price;
    private int quantity;

    // Default constructor for deserialization
    public SaleInfo() {}

    // Constructor with parameters
    public SaleInfo(double price, int quantity) {
        this.price = price;
        this.quantity = quantity;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeDouble(price);
        out.writeInt(quantity);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        price = in.readDouble();
        quantity = in.readInt();
    }

    // Getters and setters
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
