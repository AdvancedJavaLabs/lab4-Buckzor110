package lab4.buckzor110.sort;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ShuffleData implements Writable {
    private String category;
    private int quantity;

    // Default constructor for deserialization
    public ShuffleData() {}

    // Constructor with parameters
    public ShuffleData(String category, int quantity) {
        this.category = category;
        this.quantity = quantity;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(category);
        out.writeInt(quantity);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        category = in.readUTF();
        quantity = in.readInt();
    }

    // Getters and setters (if needed)
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}