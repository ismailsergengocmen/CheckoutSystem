package com.example.checkoutCase.validation;

import com.example.checkoutCase.constants.ItemConstants;
import com.example.checkoutCase.entity.Item.Item;
import com.example.checkoutCase.exception.ExceededSizeException;
import com.example.checkoutCase.exception.InvalidArgumentException;

public class DefaultItemValidator implements ItemValidator {
    public void validate_for_creation(Item item) {
        if (item.getSellerID() == ItemConstants.VAS_ITEM_SELLER_ID) {
            throw new InvalidArgumentException("Only vas items can be created with seller id of " + ItemConstants.VAS_ITEM_SELLER_ID + ".");
        }
        if (item.getQuantity() > ItemConstants.MAX_QUANTITY_LIMIT_OF_A_DEFAULT_ITEM) {
            throw new RuntimeException("Default item quantity cannot be more than " + ItemConstants.MAX_QUANTITY_LIMIT_OF_A_DEFAULT_ITEM);
        }
    }

    public void validate_for_update(Item item, int matchingItemQuantity) {
        if (matchingItemQuantity + item.getQuantity() > ItemConstants.MAX_QUANTITY_LIMIT_OF_A_DEFAULT_ITEM) {
            throw new ExceededSizeException("You can not add more than " + ItemConstants.MAX_QUANTITY_LIMIT_OF_A_DEFAULT_ITEM + " default item to your cart. You have reached the maximum default item limit.");
        }
    }
}
