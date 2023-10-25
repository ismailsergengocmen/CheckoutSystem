package com.example.checkoutCase.entity.ItemRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class ItemRequest {
    private int itemId;
    private int categoryId;
    private int sellerId;
    private double price;
    private int quantity;
    private int vasItemId;

    @Override
    public String toString() {
        return "ItemRequest{" +
                "itemId=" + itemId +
                ", categoryId=" + categoryId +
                ", sellerId=" + sellerId +
                ", price=" + price +
                ", quantity=" + quantity +
                ", vasItemId=" + vasItemId +
                '}';
    }

    public boolean isValid() {
        return this.itemId != 0 && this.categoryId != 0 && this.sellerId != 0 && this.price > 0 && this.quantity > 0;
    }
}