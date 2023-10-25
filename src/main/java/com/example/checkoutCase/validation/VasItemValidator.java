package com.example.checkoutCase.validation;

import com.example.checkoutCase.constants.ItemConstants;
import com.example.checkoutCase.entity.Item.DefaultItem;
import com.example.checkoutCase.entity.Item.Item;
import com.example.checkoutCase.exception.InvalidArgumentException;
import com.example.checkoutCase.exception.ItemCountLimitException;

import java.util.Optional;

public class VasItemValidator implements ItemValidator {
    public void validate_for_creation(Item item) {
        if (item.getCategoryID() != ItemConstants.VAS_ITEM_CATEGORY_ID){
            throw new InvalidArgumentException("You can only add vas items with category id " + ItemConstants.VAS_ITEM_CATEGORY_ID + ".");
        }

        if (item.getSellerID() != ItemConstants.VAS_ITEM_SELLER_ID){
            throw new InvalidArgumentException("You can only add vas items with seller id " + ItemConstants.VAS_ITEM_SELLER_ID + ".");
        }

        if (item.getQuantity() > ItemConstants.MAX_VAS_ITEM_COUNT_ON_A_DEFAULT_ITEM) {
            throw new InvalidArgumentException("You can not add more than " + ItemConstants.MAX_VAS_ITEM_COUNT_ON_A_DEFAULT_ITEM + " vasItem to any default item. You have too much vas item in your request.");
        }
    }


    public void validate_for_update(Item item, int matchingItemQuantity) {
        if (matchingItemQuantity + item.getQuantity() > ItemConstants.SPECIFIC_VAS_ITEM_COUNT_ON_A_DEFAULT_ITEM){
            throw new ItemCountLimitException("You can not add more than " + ItemConstants.SPECIFIC_VAS_ITEM_COUNT_ON_A_DEFAULT_ITEM + " vasItem to any default item. You have reached specific vas item limit for vas item attached to default item.");
        }
    }

    public void validate_for_addition(Item item, Optional<Item> relatedOptionalDefaultItem) {
        if (relatedOptionalDefaultItem.isEmpty()) {
            throw new InvalidArgumentException("There is no default item with given id in your cart.");
        }

        if (relatedOptionalDefaultItem.get().getCategoryID() != ItemConstants.FURNITURE_DEFAULT_ITEM_CATEGORY_ID && relatedOptionalDefaultItem.get().getCategoryID() != ItemConstants.ELECTRONICS_DEFAULT_ITEM_CATEGORY_ID) {
            throw new InvalidArgumentException("You can only add vas items to furniture or electronics.");
        }

        DefaultItem relatedDefaultItem = (DefaultItem) relatedOptionalDefaultItem.get();

        int vasItemCountOfDefaultItem = relatedDefaultItem.getVasItemList().stream().mapToInt(Item::getQuantity).sum();

        if (vasItemCountOfDefaultItem + item.getQuantity() > ItemConstants.MAX_VAS_ITEM_COUNT_ON_A_DEFAULT_ITEM) {
            throw new InvalidArgumentException("You can not add more than " + ItemConstants.MAX_VAS_ITEM_COUNT_ON_A_DEFAULT_ITEM + " vasItem to any default item. You have reached the vas item limit for selected default item.");
        }

        if (item.getPrice() > relatedDefaultItem.getPrice()){
            throw new InvalidArgumentException("You can not add vas item which is expensive than related default item.");
        }

    }

    public void check_if_there_will_be_more_than_10_quantity_of_a_vas_item_after_addition(Item newItem, int amountOfVasItem) {
        if (amountOfVasItem + newItem.getQuantity() > ItemConstants.MAX_QUANTITY_LIMIT_OF_A_VAS_ITEM) {
            throw new ItemCountLimitException("You can not add a vasItem more than " + ItemConstants.MAX_QUANTITY_LIMIT_OF_A_VAS_ITEM + " times to cart");
        }
    }

}
