package com.example.checkoutCase.validation;

import com.example.checkoutCase.constants.ItemConstants;
import com.example.checkoutCase.entity.Cart.Cart;
import com.example.checkoutCase.entity.Item.Item;
import com.example.checkoutCase.entity.ItemRequest.ItemRequest;
import com.example.checkoutCase.exception.*;

import java.util.Optional;

public class CartValidator {

    public CartValidator(){}

    public void check_if_there_is_a_digital_item_in_the_cart(Cart cart){
        if (cart.getContainsDigitalItem()){
            throw new IncompatibleItemTypesException("You can not add default item to your cart. You have already added a digital item to your cart.");
        }
    }

    public void check_if_there_is_a_default_item_in_the_cart(Cart cart){
        if (cart.getContainsDefaultItem()){
            throw new IncompatibleItemTypesException("You can not add digital item to your cart. You have already added a default item to your cart.");
        }
    }

    public void validate_total_quantity_and_price_of_the_cart(Cart cart, ItemRequest itemRequest) {
        if (cart.getTotalItemQuantity() == ItemConstants.MAX_CART_ITEM_LIMIT) {
            throw new ExceededSizeException("Your cart is full. You can not add more than " + ItemConstants.MAX_CART_ITEM_LIMIT + " to your cart.");
        }

        else if (cart.getTotalItemQuantity() + itemRequest.getQuantity() > ItemConstants.MAX_CART_ITEM_LIMIT) {
            throw new ExceededSizeException("There are not enough capacity in the cart. You can add at most " + ItemConstants.MAX_CART_ITEM_LIMIT + " items to your cart");
        }

        else if (cart.getTotalItemPrice() == ItemConstants.MAX_CART_PRICE_LIMIT) {
            throw new ExceededPriceException("You can not add more items to your cart. You have reached the maximum price limit which is " + ItemConstants.MAX_CART_PRICE_LIMIT + ".");
        }

        else if (cart.getTotalItemPrice() + itemRequest.getPrice() * itemRequest.getQuantity() > ItemConstants.MAX_CART_PRICE_LIMIT) {
            throw new ExceededPriceException("You exceed price limit. You can add at most " + ItemConstants.MAX_CART_PRICE_LIMIT + " to your cart.");
        }
    }

    public void check_if_there_are_more_than_10_unique_item_in_cart(Cart cart){
        if (cart.getUniqueItemQuantity() == ItemConstants.MAX_UNIQUE_ITEM_LIMIT_GENERAL) {
            throw new UniqueItemCountLimit("You can not add more items to your cart. You can add at most " + ItemConstants.MAX_UNIQUE_ITEM_LIMIT_GENERAL + " different items to your cart.");
        }
    }

    public void check_if_the_item_exist_in_the_cart(Optional<Item> optionalItemToDelete) {
        if (optionalItemToDelete.isEmpty()) {
            throw new ItemNotFoundException("There is no item with given id in your cart.");
        }
    }

    public void check_if_the_request_contains_valid_arguments(ItemRequest itemRequest) {
        if (!itemRequest.isValid()) {
            throw new InvalidArgumentException("Invalid item request. Please fill all the fields and use positive integers");
        }
    }

}
