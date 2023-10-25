package com.example.checkoutCase.entity.Cart;

import com.example.checkoutCase.entity.Item.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Getter
@Setter
@NoArgsConstructor
public class Cart {

    private List<Item> items = new ArrayList<>();

    private int AppliedPromotionId;

    private int totalItemQuantity;

    private double totalItemPrice;

    private int uniqueItemQuantity;

    private boolean containsDigitalItem;

    private boolean containsDefaultItem;

    private double totalDiscount;

    private Map<Integer, Integer> vasItemQuantityMap;

    public boolean getContainsDigitalItem() {
        return containsDigitalItem;
    }

    public void setContainsDigitalItem(boolean containsDigitalItem) {
        this.containsDigitalItem = containsDigitalItem;
    }

    public boolean getContainsDefaultItem() {
        return containsDefaultItem;
    }

    public void setContainsDefaultItem(boolean containsDefaultItem) {
        this.containsDefaultItem = containsDefaultItem;
    }

    public void setVasItemQuantityMap(Map<Integer, Integer> vasItemQuantityMap) {
        this.vasItemQuantityMap = vasItemQuantityMap;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "items=" + items +
                ", promotion=" + AppliedPromotionId +
                ", totalItemQuantity=" + totalItemQuantity +
                ", totalItemPrice=" + totalItemPrice +
                ", uniqueItemQuantity=" + uniqueItemQuantity +
                ", containsDigitalItem=" + containsDigitalItem +
                ", containsDefaultItem=" + containsDefaultItem +
                ", totalDiscount=" + totalDiscount +
                '}';
    }
}
