package com.example.checkoutCase.service;

import com.example.checkoutCase.constants.PromotionConstants;
import com.example.checkoutCase.constants.ItemConstants;
import com.example.checkoutCase.entity.CartResponse;
import com.example.checkoutCase.entity.DisplayCartMessage;
import com.example.checkoutCase.entity.RequestResponse;
import com.example.checkoutCase.entity.Cart.Cart;
import com.example.checkoutCase.entity.Item.*;
import com.example.checkoutCase.entity.ItemRequest.ItemRequest;
import com.example.checkoutCase.validation.CartValidator;
import com.example.checkoutCase.validation.ItemValidator;
import com.example.checkoutCase.validation.ValidatorFactory;
import com.example.checkoutCase.validation.VasItemValidator;
import com.example.checkoutCase.exception.InvalidArgumentException;
import com.example.checkoutCase.repository.ItemRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;

@Service
public class ItemService {
    private ItemRepository itemRepository;

    @Getter
    private Cart cart;

    private CartValidator cartValidator;

    @Autowired
    public ItemService(ItemRepository itemRepository, Cart cart) {
        this.itemRepository = itemRepository;
        this.cart = cart;
        this.cartValidator = new CartValidator();
    }

    @PostConstruct
    public void createCart(){
        List<Item> itemsInCart = itemRepository.findAll();
        Map<Integer, Integer> vasItemQuantityMap = new HashMap<>();

        int count_of_items_in_cart = 0;
        double total_price = 0;
        int count_of_unique_items_in_cart = 0;

        for(Item item : itemsInCart){
            count_of_items_in_cart += item.getQuantity();
            total_price += item.getPrice() * item.getQuantity();
            count_of_unique_items_in_cart += 1;

            if (item.getClass() == DefaultItem.class) {
                DefaultItem defaultItem = (DefaultItem) item;
                for (VasItem vasItem : defaultItem.getVasItemList()) {
                    count_of_items_in_cart += vasItem.getQuantity();
                    total_price += vasItem.getPrice() * vasItem.getQuantity();
                    vasItemQuantityMap.put(vasItem.getItemID(), vasItem.getQuantity());
                }
            }
        }

        cart.setItems(itemsInCart);
        cart.setTotalItemQuantity(count_of_items_in_cart);
        cart.setTotalItemPrice(total_price);
        cart.setUniqueItemQuantity(count_of_unique_items_in_cart);
        cart.setVasItemQuantityMap(vasItemQuantityMap);

        update_whether_cart_contain_default_or_digital_item();
    }

    @PreDestroy
    private void saveCart(){
        itemRepository.deleteAll();
        itemRepository.saveAll(cart.getItems());
    }

    private double calculate_total_price_promotion_discount(){
        double total_price_promotion_discount = 0;

        if (cart.getTotalItemPrice() > PromotionConstants.TOTAL_PRICE_PROMOTION_RANGE_STAGE_1_START && cart.getTotalItemPrice() < PromotionConstants.TOTAL_PRICE_PROMOTION_RANGE_STAGE_1_END) {
            total_price_promotion_discount = PromotionConstants.TOTAL_PRICE_PROMOTION_RANGE_STAGE_1_AMOUNT;
        }
        else if (cart.getTotalItemPrice() >= PromotionConstants.TOTAL_PRICE_PROMOTION_RANGE_STAGE_1_END && cart.getTotalItemPrice() < PromotionConstants.TOTAL_PRICE_PROMOTION_RANGE_STAGE_2_END){
            total_price_promotion_discount = PromotionConstants.TOTAL_PRICE_PROMOTION_RANGE_STAGE_2_AMOUNT;
        }
        else if (cart.getTotalItemPrice() >= PromotionConstants.TOTAL_PRICE_PROMOTION_RANGE_STAGE_2_END && cart.getTotalItemPrice() < PromotionConstants.TOTAL_PRICE_PROMOTION_RANGE_STAGE_3_END){
            total_price_promotion_discount = PromotionConstants.TOTAL_PRICE_PROMOTION_RANGE_STAGE_3_AMOUNT;
        }
        else if(cart.getTotalItemPrice() >= PromotionConstants.TOTAL_PRICE_PROMOTION_RANGE_STAGE_3_END){
            total_price_promotion_discount = PromotionConstants.TOTAL_PRICE_PROMOTION_RANGE_STAGE_4_AMOUNT;
        }

        return total_price_promotion_discount;
    }

    private double calculate_same_seller_promotion_discount(){
        double same_seller_promotion_discount = 0;

        boolean every_item_share_same_seller_id = cart.getItems().stream().map(Item::getSellerID).distinct().count() == 1;

        if (every_item_share_same_seller_id && cart.getUniqueItemQuantity() > 1){
            same_seller_promotion_discount = cart.getTotalItemPrice() * PromotionConstants.SAME_SELLER_PROMOTION_PERCENTAGE;
        }

        return same_seller_promotion_discount;
    }

    private double calculate_category_promotion_discount(){
        double category_promotion_discount = cart.getItems().stream()
                .filter(item -> item.getCategoryID() == PromotionConstants.CATEGORY_PROMOTION_ITEM_CATEGORY_ID)
                .mapToDouble(item -> item.getPrice() * PromotionConstants.CATEGORY_PROMOTION_PERCENTAGE)
                .sum();

        return category_promotion_discount;
    }

    private void determine_promotion_type() {
        double max_discount = 0;
        cart.setAppliedPromotionId(0);

        double same_seller_promotion_discount = calculate_same_seller_promotion_discount();
        double category_promotion_discount = calculate_category_promotion_discount();
        double total_price_promotion_discount = calculate_total_price_promotion_discount();

        if (same_seller_promotion_discount > max_discount){
            max_discount = same_seller_promotion_discount;
            cart.setAppliedPromotionId(PromotionConstants.SAME_SELLER_PROMOTION_ID);
        }

        if (category_promotion_discount > max_discount){
            max_discount = category_promotion_discount;
            cart.setAppliedPromotionId(PromotionConstants.CATEGORY_PROMOTION_ID);
        }

        if(total_price_promotion_discount > max_discount){
            max_discount = total_price_promotion_discount;
            cart.setAppliedPromotionId(PromotionConstants.TOTAL_PRICE_PROMOTION_ID);
        }

        cart.setTotalDiscount(max_discount);
    }


    private void add_vas_item_helper(Item newItem, Optional<Item> relatedOptionalDefaultItem){
        VasItemValidator vasItemValidator = (VasItemValidator) ValidatorFactory.createValidator(newItem);
        vasItemValidator.validate_for_addition(newItem, relatedOptionalDefaultItem);

        DefaultItem relatedDefaultItem = (DefaultItem) relatedOptionalDefaultItem.get();

        Optional<VasItem> optionalMatchingItem = relatedDefaultItem.getVasItemList().stream().filter(itemInVasItemList -> itemInVasItemList.getItemID() == newItem.getItemID()).findFirst();

        int specific_vas_item_amount = cart.getVasItemQuantityMap().getOrDefault(newItem.getItemID(), 0);

        vasItemValidator.check_if_there_will_be_more_than_10_quantity_of_a_vas_item_after_addition(newItem, specific_vas_item_amount);

        if (optionalMatchingItem.isPresent()) {
            Item matchingItem = optionalMatchingItem.get();
            cart.getVasItemQuantityMap().put(matchingItem.getItemID(), matchingItem.getQuantity() + newItem.getQuantity());
            matchingItem.setQuantity(matchingItem.getQuantity() + newItem.getQuantity());
        }

        if (optionalMatchingItem.isEmpty()) {
            relatedDefaultItem.getVasItemList().add((VasItem) newItem);
            cart.getVasItemQuantityMap().put(newItem.getItemID(), cart.getVasItemQuantityMap().getOrDefault(newItem.getItemID(), 0) + newItem.getQuantity());
        }

        cart.setTotalItemQuantity(cart.getTotalItemQuantity() + newItem.getQuantity());
        cart.setTotalItemPrice(cart.getTotalItemPrice() + (newItem.getPrice() * newItem.getQuantity()));
    }

    private void add_item_helper(Item newItem, Optional<Item> optionalMatchingItem){
        ItemValidator validator = ValidatorFactory.createValidator(newItem);
        validator.validate_for_creation(newItem);

        if (optionalMatchingItem.isPresent()) {
            Item matchingItem = optionalMatchingItem.get();
            validator.validate_for_update(newItem,matchingItem.getQuantity());
            matchingItem.setQuantity(matchingItem.getQuantity() + newItem.getQuantity());
        }

        cartValidator.check_if_there_are_more_than_10_unique_item_in_cart(cart);

        if (optionalMatchingItem.isEmpty()) {
            cart.getItems().add(newItem);
            cart.setUniqueItemQuantity(cart.getUniqueItemQuantity() + 1);
        }

        cart.setTotalItemQuantity(cart.getTotalItemQuantity() + newItem.getQuantity());
        cart.setTotalItemPrice(cart.getTotalItemPrice() + (newItem.getPrice() * newItem.getQuantity()));
    }

    public ResponseEntity<RequestResponse> addItem(ItemRequest itemRequest) {
        cartValidator.validate_total_quantity_and_price_of_the_cart(cart, itemRequest);
        cartValidator.check_if_the_request_contains_valid_arguments(itemRequest);

        Item newItem;

        if (itemRequest.getCategoryId() == ItemConstants.DIGITAL_ITEM_CATEGORY_ID) {
            newItem = new DigitalItem(itemRequest.getItemId(), itemRequest.getCategoryId(), itemRequest.getPrice(), itemRequest.getSellerId(), itemRequest.getQuantity());
        } else {
            newItem = new DefaultItem(itemRequest.getItemId(), itemRequest.getCategoryId(), itemRequest.getPrice(), itemRequest.getSellerId(), itemRequest.getQuantity());
        }

        Optional<Item> optionalMatchingItem = cart.getItems().stream().filter(itemInCart -> itemInCart.getItemID() == newItem.getItemID() && newItem.getClass() == itemInCart.getClass()).findFirst();

        String message = "";

        if (newItem instanceof DefaultItem) {
            cartValidator.check_if_there_is_a_digital_item_in_the_cart(cart);
            add_item_helper(newItem,optionalMatchingItem);
            cart.setContainsDefaultItem(true);
            message = "Default item " + newItem.getItemID() + " added successfully";
        } else {
            cartValidator.check_if_there_is_a_default_item_in_the_cart(cart);
            add_item_helper(newItem,optionalMatchingItem);
            message = "Digital item " + newItem.getItemID() + " added successfully";
            cart.setContainsDigitalItem(true);
        }

        RequestResponse requestResponse = new RequestResponse(true,message);
        return new ResponseEntity<>(requestResponse, HttpStatus.CREATED);
    }

    public ResponseEntity<RequestResponse> addVasItem(ItemRequest itemRequest) {
        cartValidator.validate_total_quantity_and_price_of_the_cart(cart, itemRequest);
        cartValidator.check_if_the_request_contains_valid_arguments(itemRequest);

        Item newItem = new VasItem(itemRequest.getVasItemId(), itemRequest.getCategoryId(), itemRequest.getPrice(), itemRequest.getSellerId(), itemRequest.getQuantity());

        ItemValidator vasItemValidator = ValidatorFactory.createValidator(newItem);
        vasItemValidator.validate_for_creation(newItem);

        Optional<Item> relatedDefaultItem = cart.getItems().stream()
                .filter(item -> item.getItemID() == itemRequest.getItemId() && item.getClass() == DefaultItem.class)
                .findFirst();

        add_vas_item_helper(newItem, relatedDefaultItem);
        String message = "Vas item " +  newItem.getItemID() + " added successfully";

        RequestResponse requestResponse = new RequestResponse(true,message);
        return new ResponseEntity<>(requestResponse, HttpStatus.CREATED);
    }

    public ResponseEntity<RequestResponse> deleteItem(int itemId){
        if (itemId <= 0) {
            throw new InvalidArgumentException("Invalid item id. Please enter positive item id");
        }

        Optional<Item> optionalItemToDelete = cart.getItems().stream().filter(itemInCart -> itemInCart.getItemID() == itemId).findFirst();
        cartValidator.check_if_the_item_exist_in_the_cart(optionalItemToDelete);

        delete_item_helper(optionalItemToDelete);

        update_whether_cart_contain_default_or_digital_item();

        String message = "Item " + itemId +  " deleted successfully";
        RequestResponse requestResponse = new RequestResponse(true,message);

        return new ResponseEntity<>(requestResponse, HttpStatus.OK);
    }

    private void delete_item_helper(Optional<Item> optionalItemToDelete){
        double totalPriceOfDeletedItems = 0.0;
        int totalQuantityOfDeletedItems = 0;

        Item itemToDelete = optionalItemToDelete.get();
        totalPriceOfDeletedItems += itemToDelete.getPrice() * itemToDelete.getQuantity();
        totalQuantityOfDeletedItems += itemToDelete.getQuantity();

        if (itemToDelete.getClass() == DefaultItem.class){
            DefaultItem defaultItem = (DefaultItem) itemToDelete;
            for (VasItem vasItem : defaultItem.getVasItemList()) {
                totalPriceOfDeletedItems += vasItem.getPrice() * vasItem.getQuantity();
                totalQuantityOfDeletedItems += vasItem.getQuantity();
                cart.getVasItemQuantityMap().remove(vasItem.getItemID());
            }
        }

        cart.getItems().remove(itemToDelete);
        cart.setTotalItemPrice(cart.getTotalItemPrice() - totalPriceOfDeletedItems);
        cart.setTotalItemQuantity(cart.getTotalItemQuantity() - totalQuantityOfDeletedItems);
        cart.setUniqueItemQuantity(cart.getUniqueItemQuantity() - 1);
    }

    private void update_whether_cart_contain_default_or_digital_item(){
        cart.setContainsDefaultItem(false);
        cart.setContainsDigitalItem(false);

        boolean hasDigitalItem = cart.getItems().stream().anyMatch(item -> item.getClass() == DigitalItem.class);
        cart.setContainsDigitalItem(hasDigitalItem);

        boolean hasDefaultItem = cart.getItems().stream().anyMatch(item -> item.getClass() == DefaultItem.class);
        cart.setContainsDefaultItem(hasDefaultItem);
    }

    public ResponseEntity<RequestResponse> resetCart(){
        cart.setItems(new ArrayList<>());
        cart.setTotalItemQuantity(0);
        cart.setTotalItemPrice(0);
        cart.setUniqueItemQuantity(0);
        cart.setTotalDiscount(0);
        cart.setAppliedPromotionId(0);
        cart.setContainsDefaultItem(false);
        cart.setContainsDigitalItem(false);
        cart.setVasItemQuantityMap(new HashMap<>());

        String message = "Cart reset successfully";
        RequestResponse requestResponse = new RequestResponse(true,message);

        return new ResponseEntity<>(requestResponse, HttpStatus.OK);
    }

    public ResponseEntity<CartResponse> displayCart() {
        determine_promotion_type();

        DecimalFormat decimalFormatter = new DecimalFormat("#.##");

        double cart_total_price = Double.parseDouble(decimalFormatter.format(cart.getTotalItemPrice() - cart.getTotalDiscount()));

        DisplayCartMessage message = new DisplayCartMessage(cart.getItems(), cart_total_price,
                cart.getAppliedPromotionId(), cart.getTotalDiscount());

        CartResponse cartResponse = new CartResponse(true, message);
        return new ResponseEntity<>(cartResponse, HttpStatus.OK);
    }

}
