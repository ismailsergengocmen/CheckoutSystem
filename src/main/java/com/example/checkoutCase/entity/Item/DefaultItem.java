package com.example.checkoutCase.entity.Item;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "items")
@TypeAlias("DefaultItem")
@SuperBuilder
@NoArgsConstructor
public class DefaultItem extends Item {

    private List<VasItem> vasItemList = new ArrayList<>();

    public DefaultItem(int itemID, int categoryID, double price, int sellerID, int quantity) {
        super(itemID, categoryID, price, sellerID, quantity);
    }

    public List<VasItem> getVasItemList() {
        return vasItemList;
    }

    public void setVasItemList(List<VasItem> vasItemList) {
        this.vasItemList = vasItemList;
    }


}

