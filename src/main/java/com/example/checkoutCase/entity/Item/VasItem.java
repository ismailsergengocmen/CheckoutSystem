package com.example.checkoutCase.entity.Item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class VasItem extends Item {

    public VasItem(int vasItemID, int categoryID, double price, int sellerID, int quantity) {
        super(vasItemID, categoryID, price, sellerID, quantity);
    }

    @JsonProperty("vasItemId")
    public int getVasItemId() {
        return super.getItemID();
    }

    @JsonIgnore
    @Override
    public int getItemID() {
        return super.getItemID();
    }

}
