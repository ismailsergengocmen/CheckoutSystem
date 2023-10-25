package com.example.checkoutCase.validation;

import com.example.checkoutCase.entity.Item.DefaultItem;
import com.example.checkoutCase.entity.Item.DigitalItem;
import com.example.checkoutCase.entity.Item.Item;
import com.example.checkoutCase.entity.Item.VasItem;

public class ValidatorFactory {
    public static ItemValidator createValidator(Item item) {
        if (item instanceof DigitalItem) {
            return new DigitalItemValidator();
        } else if (item instanceof DefaultItem) {
            return new DefaultItemValidator();
        } else if (item instanceof VasItem) {
            return new VasItemValidator();
        } else {
            throw new RuntimeException("Invalid item type");
        }
    }
}
