package com.example.checkoutCase.validation;

import com.example.checkoutCase.entity.Item.Item;

import java.util.Optional;

public interface ItemValidator {
    void validate_for_creation(Item item);

    void validate_for_update(Item item, int matchingItemQuantity);
}
