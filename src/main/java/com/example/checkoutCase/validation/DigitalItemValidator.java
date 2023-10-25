package com.example.checkoutCase.validation;

import com.example.checkoutCase.constants.ItemConstants;
import com.example.checkoutCase.entity.Item.Item;
import com.example.checkoutCase.exception.*;

public class DigitalItemValidator implements ItemValidator {
    public void validate_for_creation(Item item) {
        if (item.getSellerID() == ItemConstants.VAS_ITEM_SELLER_ID) {
            throw new InvalidArgumentException("Only vas items can be created with seller id of " + ItemConstants.VAS_ITEM_SELLER_ID + ".");
        }
        if (item.getQuantity() > ItemConstants.MAX_QUANTITY_LIMIT_OF_A_DIGITAL_ITEM) {
            throw new ItemCountLimitException("You can not add more than " + ItemConstants.MAX_QUANTITY_LIMIT_OF_A_DIGITAL_ITEM + " for a specific digitalItem to your cart. You have reached the specific digital item limit.");
        }
    }

    public void validate_for_update(Item item, int matchingItemQuantity) {
       if (matchingItemQuantity + item.getQuantity() > ItemConstants.MAX_QUANTITY_LIMIT_OF_A_DIGITAL_ITEM){
            throw new ItemCountLimitException("You can not add more than " + ItemConstants.MAX_QUANTITY_LIMIT_OF_A_DIGITAL_ITEM + " digital item to your cart. You have reached the maximum item limit.");
        }
    }
}
