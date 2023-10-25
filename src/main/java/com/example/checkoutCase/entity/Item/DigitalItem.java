package com.example.checkoutCase.entity.Item;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "items")
@TypeAlias("DigitalItem")
@SuperBuilder
@NoArgsConstructor
public class DigitalItem extends Item {

    public DigitalItem(int itemID, int categoryID, double price, int sellerID, int quantity) {
        super(itemID, categoryID, price, sellerID, quantity);
    }

}
