package com.example.checkoutCase.entity;

import com.example.checkoutCase.entity.Item.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DisplayCartMessage {

    private List<Item> items;
    private double totalPrice;
    private int appliedPromotionId;
    private double totalDiscount;

    @Override
    public String toString() {
        return "{" +
                "items=" + items +
                ", totalPrice=" + (totalPrice - totalDiscount) +
                ", appliedPromotionId=" + appliedPromotionId +
                ", totalDiscount=" + totalDiscount +
                '}';
    }
}
