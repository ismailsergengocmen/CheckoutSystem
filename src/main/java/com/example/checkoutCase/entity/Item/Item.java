package com.example.checkoutCase.entity.Item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="items")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Item {

    private int itemID;

    private int categoryID;

    private double price;

    private int sellerID;

    private int quantity;

    @Override
    public String toString() {
        return "Item{" +
                "itemID=" + itemID +
                ", categoryID=" + categoryID +
                ", price=" + price +
                ", sellerID=" + sellerID +
                ", quantity=" + quantity +
                '}';
    }

}
