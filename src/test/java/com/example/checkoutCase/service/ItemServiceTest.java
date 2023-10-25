package com.example.checkoutCase.service;

import com.example.checkoutCase.constants.ItemConstants;
import com.example.checkoutCase.entity.Cart.Cart;
import com.example.checkoutCase.entity.Item.DigitalItem;
import com.example.checkoutCase.entity.Item.Item;
import com.example.checkoutCase.entity.ItemRequest.ItemRequest;
import com.example.checkoutCase.entity.RequestResponse;
import com.example.checkoutCase.exception.*;
import com.example.checkoutCase.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    private ItemService itemService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Cart cart = new Cart();
        itemService = new ItemService(itemRepository, cart);
        itemService.createCart();
    }

    ////////////////////
    // ADDITION TESTS //
    ////////////////////

    @Test
    public void add_default_item_successfully_when_cart_is_empty() {
        //Given
        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(1000).price(10.5).sellerId(5000).quantity(4).build();

        //When & Then
        String message = "Default item " + default_item_1.getItemId() + " added successfully";
        RequestResponse requestResponse = new RequestResponse(true,message);

        ResponseEntity<RequestResponse> expectedResponse = ResponseEntity.status(HttpStatus.CREATED)
                .body(requestResponse);

        assertEquals(expectedResponse.getBody().getMessage(), itemService.addItem(default_item_1).getBody().getMessage());
    }

    @Test
    public void update_quantity_of_default_item_when_an_item_with_same_id_added() {
        //Given
        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(1000).price(10.5).sellerId(5000).quantity(5).build();

        //When & Then
        itemService.addItem(default_item_1);
        itemService.addItem(default_item_1);

        assertEquals(10, itemService.getCart().getItems().get(0).getQuantity());
    }

    @Test
    public void it_should_throw_invalid_argument_error_when_seller_id_of_VAS_ITEM_SELLER_ID_is_selected_for_item_other_than_vas_item() {
        //Given
        ItemRequest default_item_has_seller_id_VAS_ITEM_SELLER_ID = ItemRequest.builder().itemId(1).categoryId(1000).price(10.5).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(2).build();

        //When & Then
        assertThrows(InvalidArgumentException.class, () -> itemService.addItem(default_item_has_seller_id_VAS_ITEM_SELLER_ID));
    }

    @Test
    public void it_should_throw_invalid_argument_error_when_seller_id_of_VAS_ITEM_SELLER_ID_is_selected_for_item_other_than_vas_item_message() {
        //Given
        ItemRequest default_item_has_seller_id_VAS_ITEM_SELLER_ID = ItemRequest.builder().itemId(1).categoryId(1000).price(10.5).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(2).build();

        //When & Then
        String message = "Only vas items can be created with seller id of " + ItemConstants.VAS_ITEM_SELLER_ID + ".";
        assertEquals(message, assertThrows(InvalidArgumentException.class, () -> {
            itemService.addItem(default_item_has_seller_id_VAS_ITEM_SELLER_ID);
        }).getMessage());
    }

    @Test
    public void add_digital_item_successfully_when_cart_is_empty(){
        //Given
        ItemRequest digital_item_1 = ItemRequest.builder().itemId(2).categoryId(ItemConstants.DIGITAL_ITEM_CATEGORY_ID).price(10.6).sellerId(5005).quantity(4).build();

        //When & Then
        String message = "Digital item " + digital_item_1.getItemId() + " added successfully";
        RequestResponse requestResponse = new RequestResponse(true,message);

        ResponseEntity<RequestResponse> expectedResponse = ResponseEntity.status(HttpStatus.CREATED)
                .body(requestResponse);

        assertEquals(expectedResponse.getBody().getMessage(), itemService.addItem(digital_item_1).getBody().getMessage());
    }

    @Test
    public void update_quantity_of_digital_item_when_an_item_with_same_id_added() {
        //Given
        ItemRequest digital_item_1 = ItemRequest.builder().itemId(1).categoryId(1000).price(10.5).sellerId(5000).quantity(2).build();

        //When & Then
        itemService.addItem(digital_item_1);
        itemService.addItem(digital_item_1);

        assertEquals(4, itemService.getCart().getItems().get(0).getQuantity());
    }

    @Test
    public void add_vas_item_successfully_when_a_default_item_with_correct_combination_selected(){
        //Given
        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(ItemConstants.ELECTRONICS_DEFAULT_ITEM_CATEGORY_ID).price(500).sellerId(5005).quantity(3).build();
        ItemRequest vas_item_1 = ItemRequest.builder().itemId(1).vasItemId(1).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(10.6).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(2).build();

        //When
        itemService.addItem(default_item_1);

        //Then
        String message = "Vas item " + vas_item_1.getItemId() + " added successfully";
        RequestResponse requestResponse = new RequestResponse(true,message);

        ResponseEntity<RequestResponse> expectedResponse = ResponseEntity.status(HttpStatus.CREATED)
                .body(requestResponse);

        assertEquals(expectedResponse.getBody().getMessage(), itemService.addVasItem(vas_item_1).getBody().getMessage());
    }

    @Test
    public void it_should_throw_invalid_argument_exception_when_a_vas_item_tried_to_be_added_with_wrong_category_id(){
        //Given
        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(ItemConstants.ELECTRONICS_DEFAULT_ITEM_CATEGORY_ID).price(500).sellerId(5002).quantity(3).build();
        ItemRequest vas_item_1 = ItemRequest.builder().itemId(1).vasItemId(1).categoryId(1000).price(10.6).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(4).build();

        //When & Then
        assertThrows(InvalidArgumentException.class, () -> {
            itemService.addItem(default_item_1);
            itemService.addVasItem(vas_item_1);
        });
    }

    @Test
    public void it_should_throw_invalid_argument_exception_when_a_vas_item_tried_to_be_added_with_more_price_than_related_default_item(){
        // Given
        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(ItemConstants.ELECTRONICS_DEFAULT_ITEM_CATEGORY_ID).price(500).sellerId(5005).quantity(3).build();
        ItemRequest vas_item_1 = ItemRequest.builder().itemId(1).vasItemId(1).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(500.1).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(4).build();

        // When & Then
        assertThrows(InvalidArgumentException.class, () -> {
            itemService.addItem(default_item_1);
            itemService.addVasItem(vas_item_1);
        });
    }

    @Test
    public void it_should_throw_invalid_argument_exception_when_a_vas_item_tried_to_be_added_with_more_price_than_related_default_item_message(){
        // Given
        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(ItemConstants.ELECTRONICS_DEFAULT_ITEM_CATEGORY_ID).price(500).sellerId(5005).quantity(3).build();
        ItemRequest vas_item_1 = ItemRequest.builder().itemId(1).vasItemId(1).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(500.1).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(2).build();

        String message = "You can not add vas item which is expensive than related default item.";
        // When & Then
        assertEquals(message, assertThrows(InvalidArgumentException.class, () -> {
            itemService.addItem(default_item_1);
            itemService.addVasItem(vas_item_1);
        }).getMessage());
    }

    @Test
    public void it_should_throw_invalid_argument_exception_when_a_vas_item_tried_to_be_added_with_wrong_seller_id(){
        //Given
        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(ItemConstants.ELECTRONICS_DEFAULT_ITEM_CATEGORY_ID).price(500).sellerId(5005).quantity(3).build();
        ItemRequest vas_item_1 = ItemRequest.builder().itemId(1).vasItemId(1).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(10.6).sellerId(5005).quantity(2).build();

        //When & Then
        assertThrows(InvalidArgumentException.class, () -> {
            itemService.addItem(default_item_1);
            itemService.addVasItem(vas_item_1);
        });
    }

    @Test
    public void it_should_throw_invalid_argument_exception_when_a_vas_item_tried_to_be_added_with_wrong_seller_id_message(){
        //Given
        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(ItemConstants.ELECTRONICS_DEFAULT_ITEM_CATEGORY_ID).price(500).sellerId(5005).quantity(3).build();
        ItemRequest vas_item_1 = ItemRequest.builder().itemId(1).vasItemId(1).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(10.6).sellerId(5005).quantity(2).build();

        //When & Then
        String message = "You can only add vas items with seller id " + ItemConstants.VAS_ITEM_SELLER_ID + ".";
        // When & Then
        assertEquals(message, assertThrows(InvalidArgumentException.class, () -> {
            itemService.addItem(default_item_1);
            itemService.addVasItem(vas_item_1);
        }).getMessage());
    }

    @Test
    public void it_should_throw_exceeded_price_exception_when_added_item_has_more_than_500k_price() {
        //Given
        ItemRequest default_item_has_more_than_500k_price = ItemRequest.builder().itemId(1).categoryId(1000).price(1000000).sellerId(5000).quantity(3).build();

        //When & Then
        assertThrows(ExceededPriceException.class, () -> {
            itemService.addItem(default_item_has_more_than_500k_price);
        });

    }

    @Test
    public void it_should_throw_exceeded_size_exception_when_added_item_has_more_than_30_quantity(){
        //Given
        ItemRequest default_item_has_more_than_30_quantity = ItemRequest.builder().itemId(1).categoryId(1000).price(10.5).sellerId(5000).quantity(32).build();

        //When & Then
        assertThrows(ExceededSizeException.class, () -> {
            itemService.addItem(default_item_has_more_than_30_quantity);
        });
    }

    @Test
    public void it_should_throw_incompatible_item_size_exception_when_digital_item_added_to_cart_containing_default_item(){
        //Given
        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(1000).price(10.5).sellerId(5001).quantity(3).build();
        ItemRequest digital_item_1 = ItemRequest.builder().itemId(2).categoryId(ItemConstants.DIGITAL_ITEM_CATEGORY_ID).price(10.6).sellerId(5005).quantity(4).build();

        //When & Then
        assertThrows(IncompatibleItemTypesException.class, () -> {itemService.addItem(default_item_1); itemService.addItem(digital_item_1);});
    }

    @Test
    public void it_should_throw_incompatible_item_size_exception_when_default_item_added_to_cart_containing_digital_item(){
        //Given
        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(1000).price(10.5).sellerId(5001).quantity(3).build();
        ItemRequest digital_item_1 = ItemRequest.builder().itemId(1).categoryId(ItemConstants.DIGITAL_ITEM_CATEGORY_ID).price(10.6).sellerId(5005).quantity(4).build();

        //When & Then
        assertThrows(IncompatibleItemTypesException.class, () -> {itemService.addItem(digital_item_1); itemService.addItem(default_item_1);});
    }

    @Test
    public void it_should_throw_unique_item_count_limit_when_11th_unique_item_tried_to_be_added(){
        //Given
        List<ItemRequest> itemList = new ArrayList<>();

        //When
        for (int i = 1; i <= 11; i++){
            ItemRequest default_item = ItemRequest.builder().itemId(i).categoryId(1000+i).price(10+i).sellerId(5001).quantity(2).build();
            itemList.add(default_item);
        }

        //Then
        assertThrows(UniqueItemCountLimit.class, () -> {
            itemList.forEach(item -> itemService.addItem(item));
        });
    }

    @Test
    public void it_should_throw_item_count_limit_exception_when_vas_item_tried_to_be_added_for_10th_time(){
        //Given
        List<ItemRequest> defaultItemList = new ArrayList<>();
        List<ItemRequest> vasItemlist = new ArrayList<>();

        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(ItemConstants.ELECTRONICS_DEFAULT_ITEM_CATEGORY_ID).price(500).sellerId(5005).quantity(3).build();
        ItemRequest default_item_2 = ItemRequest.builder().itemId(2).categoryId(ItemConstants.ELECTRONICS_DEFAULT_ITEM_CATEGORY_ID).price(600).sellerId(5005).quantity(3).build();
        ItemRequest default_item_3 = ItemRequest.builder().itemId(3).categoryId(ItemConstants.ELECTRONICS_DEFAULT_ITEM_CATEGORY_ID).price(700).sellerId(5005).quantity(3).build();
        ItemRequest default_item_4 = ItemRequest.builder().itemId(4).categoryId(ItemConstants.ELECTRONICS_DEFAULT_ITEM_CATEGORY_ID).price(800).sellerId(5005).quantity(3).build();

        ItemRequest vas_item_1 = ItemRequest.builder().itemId(1).vasItemId(1).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(10.6).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(3).build();
        ItemRequest vas_item_2 = ItemRequest.builder().itemId(2).vasItemId(1).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(10.6).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(3).build();
        ItemRequest vas_item_3 = ItemRequest.builder().itemId(3).vasItemId(1).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(10.6).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(3).build();
        ItemRequest vas_item_4 = ItemRequest.builder().itemId(4).vasItemId(1).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(10.6).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(3).build();

        //When
        defaultItemList.add(default_item_1);
        defaultItemList.add(default_item_2);
        defaultItemList.add(default_item_3);
        defaultItemList.add(default_item_4);

        vasItemlist.add(vas_item_1);
        vasItemlist.add(vas_item_2);
        vasItemlist.add(vas_item_3);
        vasItemlist.add(vas_item_4);

        //Then
        assertThrows(ItemCountLimitException.class, () -> {
            defaultItemList.forEach(item -> itemService.addItem(item));
            vasItemlist.forEach(item -> itemService.addVasItem(item));
        });
    }

    @Test
    public void it_should_throw_item_count_limit_exception_when_a_digital_item_tried_to_be_added_for_6th_time() {
        //Given
        ItemRequest digital_item_1 = ItemRequest.builder().itemId(2).categoryId(ItemConstants.DIGITAL_ITEM_CATEGORY_ID).price(10.6).sellerId(5005).quantity(4).build();

        //When & Then
        assertThrows(ItemCountLimitException.class, () -> {
            for (int i = 0; i <= 5; i++) {
                itemService.addItem(digital_item_1);
            }
        });
    }

    @Test
    public void it_should_throw_item_count_limit_exception_when_a_digital_item_tried_to_be_added_for_6th_time_message(){
        //Given
        ItemRequest digital_item_1 = ItemRequest.builder().itemId(2).categoryId(ItemConstants.DIGITAL_ITEM_CATEGORY_ID).price(10.6).sellerId(5005).quantity(4).build();

        String message = "You can not add more than " + ItemConstants.MAX_QUANTITY_LIMIT_OF_A_DIGITAL_ITEM + " digital item to your cart. You have reached the maximum item limit.";

        //When & Then
        assertEquals(message, assertThrows(ItemCountLimitException.class, () -> {
            for (int i = 0; i <= 5; i++) {
                itemService.addItem(digital_item_1);
            }
        }).getMessage());
    }

    @Test
    public void it_should_throw_invalid_argument_exception_when_a_vas_item_tried_to_be_added_to_non_exist_default_item(){
        //Given
        ItemRequest vas_item_1 = ItemRequest.builder().itemId(1).vasItemId(1).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(10.6).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(2).build();

        //When & Then
        assertThrows(InvalidArgumentException.class, () -> {
            itemService.addVasItem(vas_item_1);
        });
    }

    @Test
    public void it_should_throw_invalid_argument_exception_when_a_vas_item_tried_to_be_added_to_non_exist_default_item_message(){
        //Given
        ItemRequest vas_item_1 = ItemRequest.builder().itemId(1).vasItemId(1).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(10.6).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(2).build();

        //When & Then
        String message = "There is no default item with given id in your cart.";
        assertEquals(message, assertThrows(InvalidArgumentException.class, () -> {
            itemService.addVasItem(vas_item_1);
        }).getMessage());
    }

    @Test
    public void at_most_3_vas_item_can_be_added_to_default_item(){
        //Given
        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(ItemConstants.ELECTRONICS_DEFAULT_ITEM_CATEGORY_ID).price(500).sellerId(5005).quantity(3).build();
        ItemRequest vas_item_1 = ItemRequest.builder().itemId(1).vasItemId(1).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(10.6).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(4).build();

        //When
        itemService.addItem(default_item_1);

        //Then
        assertThrows(InvalidArgumentException.class, () -> {
            itemService.addVasItem(vas_item_1);
        });
    }

    @Test
    public void at_most_3_vas_item_can_be_added_to_default_item_with_different_vas_items(){
        //Given
        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(ItemConstants.ELECTRONICS_DEFAULT_ITEM_CATEGORY_ID).price(500).sellerId(5005).quantity(3).build();
        ItemRequest vas_item_1 = ItemRequest.builder().itemId(1).vasItemId(1).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(10.6).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(1).build();
        ItemRequest vas_item_2 = ItemRequest.builder().itemId(1).vasItemId(2).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(10.8).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(1).build();
        ItemRequest vas_item_3 = ItemRequest.builder().itemId(1).vasItemId(3).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(11.0).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(1).build();
        ItemRequest vas_item_4 = ItemRequest.builder().itemId(1).vasItemId(3).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(11.2).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(1).build();

        //When & Then
        assertThrows(InvalidArgumentException.class, () -> {
            itemService.addItem(default_item_1);
            itemService.addVasItem(vas_item_1);
            itemService.addVasItem(vas_item_2);
            itemService.addVasItem(vas_item_3);
            itemService.addVasItem(vas_item_4);
        });
    }

    @Test
    public void total_price_correctly_calculated_after_some_items_added(){
        //Given
        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(ItemConstants.ELECTRONICS_DEFAULT_ITEM_CATEGORY_ID).price(500).sellerId(5005).quantity(3).build();
        ItemRequest default_item_2 = ItemRequest.builder().itemId(2).categoryId(3004).price(2000).sellerId(5005).quantity(6).build();

        ItemRequest vas_item_1 = ItemRequest.builder().itemId(1).vasItemId(1).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(10.6).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(1).build();
        ItemRequest vas_item_2 = ItemRequest.builder().itemId(1).vasItemId(2).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(10.8).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(1).build();

        ItemRequest vas_item_3 = ItemRequest.builder().itemId(2).vasItemId(3).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(11.2).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(1).build();
        ItemRequest vas_item_4 = ItemRequest.builder().itemId(2).vasItemId(3).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(40.2).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(2).build();

        //When
        itemService.addItem(default_item_1);
        itemService.addItem(default_item_2);

        itemService.addVasItem(vas_item_1);
        itemService.addVasItem(vas_item_2);
        itemService.addVasItem(vas_item_3);
        itemService.addVasItem(vas_item_4);

        //Then
        assertEquals(13613, itemService.getCart().getTotalItemPrice());
    }

    @Test
    public void total_unique_item_count_correctly_calculated_after_some_items_added(){
        //Given
        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(ItemConstants.ELECTRONICS_DEFAULT_ITEM_CATEGORY_ID).price(500).sellerId(5005).quantity(3).build();
        ItemRequest default_item_2 = ItemRequest.builder().itemId(2).categoryId(3004).price(2000).sellerId(5005).quantity(6).build();

        ItemRequest vas_item_1 = ItemRequest.builder().itemId(1).vasItemId(1).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(10.6).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(1).build();
        ItemRequest vas_item_2 = ItemRequest.builder().itemId(1).vasItemId(2).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(10.8).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(1).build();

        ItemRequest vas_item_3 = ItemRequest.builder().itemId(2).vasItemId(3).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(11.2).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(1).build();
        ItemRequest vas_item_4 = ItemRequest.builder().itemId(2).vasItemId(3).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(40.2).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(2).build();

        //When
        itemService.addItem(default_item_1);
        itemService.addItem(default_item_2);

        itemService.addVasItem(vas_item_1);
        itemService.addVasItem(vas_item_2);
        itemService.addVasItem(vas_item_3);
        itemService.addVasItem(vas_item_4);

        //Then
        assertEquals(2, itemService.getCart().getUniqueItemQuantity());
    }

    @Test
    public void total_item_count_correctly_calculated_after_some_items_added(){
        //Given
        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(ItemConstants.ELECTRONICS_DEFAULT_ITEM_CATEGORY_ID).price(500).sellerId(5005).quantity(3).build();
        ItemRequest default_item_2 = ItemRequest.builder().itemId(2).categoryId(3004).price(2000).sellerId(5005).quantity(6).build();

        ItemRequest vas_item_1 = ItemRequest.builder().itemId(1).vasItemId(1).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(10.6).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(1).build();
        ItemRequest vas_item_2 = ItemRequest.builder().itemId(1).vasItemId(2).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(10.8).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(1).build();

        ItemRequest vas_item_3 = ItemRequest.builder().itemId(2).vasItemId(3).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(11.2).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(1).build();
        ItemRequest vas_item_4 = ItemRequest.builder().itemId(2).vasItemId(3).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(40.2).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(2).build();

        //When
        itemService.addItem(default_item_1);
        itemService.addItem(default_item_2);

        itemService.addVasItem(vas_item_1);
        itemService.addVasItem(vas_item_2);
        itemService.addVasItem(vas_item_3);
        itemService.addVasItem(vas_item_4);

        //Then
        assertEquals(14, itemService.getCart().getTotalItemQuantity());
    }

    ////////////////////
    // DELETION TESTS //
    ////////////////////

    @Test
    public void delete_default_item_successfully_when_cart_contains_only_default_item(){
        //Given
        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(1000).price(500).sellerId(5005).quantity(3).build();

        //When
        itemService.addItem(default_item_1);

        //Then
        String message = "Item " + default_item_1.getItemId() + " deleted successfully";
        RequestResponse requestResponse = new RequestResponse(true,message);

        ResponseEntity<RequestResponse> expectedResponse = ResponseEntity.status(HttpStatus.OK)
                .body(requestResponse);

        assertEquals(expectedResponse.getBody().getMessage(), itemService.deleteItem(default_item_1.getItemId()).getBody().getMessage());
    }

    @Test
    public void it_should_throw_error_when_there_is_no_default_or_digital_item_with_given_id_to_delete_item(){
        //Given
        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(1000).price(500).sellerId(5005).quantity(3).build();

        //When
        itemService.addItem(default_item_1);

        //Then
        assertThrows(ItemNotFoundException.class, () -> {
            itemService.deleteItem(2);
        });
    }

    @Test
    public void delete_digital_item_successfully_when_cart_contains_only_digital_item(){
        //Given
        ItemRequest digital_item_1 = ItemRequest.builder().itemId(2).categoryId(ItemConstants.DIGITAL_ITEM_CATEGORY_ID).price(10.6).sellerId(5005).quantity(4).build();

        //When
        itemService.addItem(digital_item_1);

        //Then
        String message = "Item " + digital_item_1.getItemId() + " deleted successfully";
        RequestResponse requestResponse = new RequestResponse(true,message);

        ResponseEntity<RequestResponse> expectedResponse = ResponseEntity.status(HttpStatus.OK)
                .body(requestResponse);

        assertEquals(expectedResponse.getBody().getMessage(), itemService.deleteItem(digital_item_1.getItemId()).getBody().getMessage());
    }

    @Test
    public void delete_vas_items_succesfully_when_related_default_item_is_removed(){
        //Given
        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(ItemConstants.ELECTRONICS_DEFAULT_ITEM_CATEGORY_ID).price(500).sellerId(5005).quantity(3).build();
        ItemRequest vas_item_1 = ItemRequest.builder().itemId(1).vasItemId(1).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(10.6).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(1).build();
        ItemRequest vas_item_2 = ItemRequest.builder().itemId(1).vasItemId(2).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(10.8).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(1).build();
        ItemRequest vas_item_3 = ItemRequest.builder().itemId(1).vasItemId(3).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(11.0).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(1).build();

        itemService.addItem(default_item_1);
        itemService.addVasItem(vas_item_1);
        itemService.addVasItem(vas_item_2);
        itemService.addVasItem(vas_item_3);

        //When
        itemService.deleteItem(default_item_1.getItemId());

        //Then
        assertEquals(0, itemService.getCart().getTotalItemQuantity());
    }

    @Test
    public void correctly_determine_unique_items_in_the_cart_after_an_item_is_removed(){
        //Given
        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(ItemConstants.ELECTRONICS_DEFAULT_ITEM_CATEGORY_ID).price(500).sellerId(5006).quantity(3).build();
        ItemRequest default_item_2 = ItemRequest.builder().itemId(3).categoryId(1002).price(500).sellerId(5005).quantity(6).build();
        ItemRequest default_item_3 = ItemRequest.builder().itemId(4).categoryId(1003).price(500).sellerId(5004).quantity(9).build();

        ItemRequest vas_item_1 = ItemRequest.builder().itemId(1).vasItemId(1).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(10.6).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(1).build();
        ItemRequest vas_item_2 = ItemRequest.builder().itemId(1).vasItemId(2).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(10.8).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(1).build();
        ItemRequest vas_item_3 = ItemRequest.builder().itemId(1).vasItemId(3).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(11.0).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(1).build();

        //When
        itemService.addItem(default_item_1);
        itemService.addItem(default_item_2);
        itemService.addItem(default_item_3);
        itemService.addVasItem(vas_item_1);
        itemService.addVasItem(vas_item_2);
        itemService.addVasItem(vas_item_3);

        //Then
        itemService.deleteItem(default_item_3.getItemId());

        assertEquals(2, itemService.getCart().getUniqueItemQuantity());
    }

    @Test
    public void correctly_determine_total_item_count_in_the_cart_after_an_item_is_removed(){
        //Given
        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(ItemConstants.ELECTRONICS_DEFAULT_ITEM_CATEGORY_ID).price(500).sellerId(5006).quantity(3).build();
        ItemRequest default_item_2 = ItemRequest.builder().itemId(3).categoryId(1002).price(500).sellerId(5005).quantity(6).build();
        ItemRequest default_item_3 = ItemRequest.builder().itemId(4).categoryId(1003).price(500).sellerId(5004).quantity(9).build();

        ItemRequest vas_item_1 = ItemRequest.builder().itemId(1).vasItemId(1).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(10.6).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(1).build();
        ItemRequest vas_item_2 = ItemRequest.builder().itemId(1).vasItemId(2).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(10.8).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(1).build();
        ItemRequest vas_item_3 = ItemRequest.builder().itemId(1).vasItemId(3).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(11.0).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(1).build();

        //When
        itemService.addItem(default_item_1);
        itemService.addItem(default_item_2);
        itemService.addItem(default_item_3);
        itemService.addVasItem(vas_item_1);
        itemService.addVasItem(vas_item_2);
        itemService.addVasItem(vas_item_3);

        //Then
        itemService.deleteItem(default_item_1.getItemId());

        assertEquals(15, itemService.getCart().getTotalItemQuantity());
    }

    @Test
    public void correctly_determine_total_price_of_items_in_the_cart_after_an_item_is_removed(){
        //Given
        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(ItemConstants.ELECTRONICS_DEFAULT_ITEM_CATEGORY_ID).price(500).sellerId(5006).quantity(3).build();
        ItemRequest default_item_2 = ItemRequest.builder().itemId(3).categoryId(1002).price(100).sellerId(5005).quantity(6).build();
        ItemRequest default_item_3 = ItemRequest.builder().itemId(4).categoryId(1003).price(700).sellerId(5004).quantity(9).build();

        ItemRequest vas_item_1 = ItemRequest.builder().itemId(1).vasItemId(1).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(10.6).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(1).build();
        ItemRequest vas_item_2 = ItemRequest.builder().itemId(1).vasItemId(2).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(10.8).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(1).build();
        ItemRequest vas_item_3 = ItemRequest.builder().itemId(1).vasItemId(3).categoryId(ItemConstants.VAS_ITEM_CATEGORY_ID).price(11.0).sellerId(ItemConstants.VAS_ITEM_SELLER_ID).quantity(1).build();

        //When
        itemService.addItem(default_item_1);
        itemService.addItem(default_item_2);
        itemService.addItem(default_item_3);
        itemService.addVasItem(vas_item_1);
        itemService.addVasItem(vas_item_2);
        itemService.addVasItem(vas_item_3);

        //Then
        itemService.deleteItem(default_item_2.getItemId());

        assertEquals(7832.4, itemService.getCart().getTotalItemPrice());
    }

    @Test
    public void correctly_add_digital_item_after_all_default_items_are_removed(){
        //Given
        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(1000).price(500).sellerId(5006).quantity(3).build();
        ItemRequest default_item_2 = ItemRequest.builder().itemId(3).categoryId(1002).price(100).sellerId(5005).quantity(6).build();

        ItemRequest digital_item_1 = ItemRequest.builder().itemId(2).categoryId(ItemConstants.DIGITAL_ITEM_CATEGORY_ID).price(10.6).sellerId(5005).quantity(4).build();

        itemService.addItem(default_item_1);
        itemService.addItem(default_item_2);

        itemService.deleteItem(default_item_1.getItemId());
        itemService.deleteItem(default_item_2.getItemId());

        //When
        Item digital_item = DigitalItem.builder().itemID(digital_item_1.getItemId()).categoryID(digital_item_1.getCategoryId()).price(digital_item_1.getPrice()).sellerID(digital_item_1.getSellerId()).quantity(digital_item_1.getQuantity()).build();
        itemService.addItem(digital_item_1);

        //Then
        assertEquals(digital_item.toString(), itemService.getCart().getItems().get(0).toString());
    }

    ////////////////
    // RESET TEST //
    ////////////////

    @Test
    public void correctly_reset_cart(){
        //Given
        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(1000).price(500).sellerId(5006).quantity(3).build();
        ItemRequest default_item_2 = ItemRequest.builder().itemId(1).categoryId(1000).price(500).sellerId(5006).quantity(3).build();

        itemService.addItem(default_item_1);
        itemService.addItem(default_item_2);

        //When
        itemService.resetCart();

        //Then
        assertEquals(new ArrayList<>(), itemService.getCart().getItems());
    }

    /////////////////////
    // PROMOTION TESTS //
    ////////////////////

    @Test
    public void cart_should_not_have_any_promotion_when_there_is_not_enough_item(){
        //Given
        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(1000).price(200).sellerId(5006).quantity(3).build();

        //When
        itemService.addItem(default_item_1);

        //Then
        assertEquals(0, itemService.getCart().getAppliedPromotionId());
    }

    @Test
    public void cart_should_have_same_seller_promotion_when_all_items_have_same_seller_id(){
        //Given
        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(1000).price(2000).sellerId(5006).quantity(3).build();
        ItemRequest default_item_2 = ItemRequest.builder().itemId(2).categoryId(1002).price(2000).sellerId(5006).quantity(3).build();

        itemService.addItem(default_item_1);
        itemService.addItem(default_item_2);

        //When
        itemService.displayCart();

        //Then
        assertEquals(9909, itemService.getCart().getAppliedPromotionId());
    }

    @Test
    public void cart_should_have_category_promotion_when_there_are_items_with_category_id_3003(){
        //Given
        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(3003).price(200).sellerId(5007).quantity(1).build();
        ItemRequest default_item_2 = ItemRequest.builder().itemId(2).categoryId(1002).price(10).sellerId(5006).quantity(1).build();

        itemService.addItem(default_item_1);
        itemService.addItem(default_item_2);

        //When
        itemService.displayCart();

        //Then
        assertEquals(5676, itemService.getCart().getAppliedPromotionId());
    }

    @Test
    public void cart_should_have_total_price_promotion_when_there_are_not_better_alternative_and_cart_have_more_than_total_price_of_250(){
        //Given
        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(3003).price(200).sellerId(5007).quantity(1).build();
        ItemRequest default_item_2 = ItemRequest.builder().itemId(2).categoryId(1002).price(60).sellerId(5006).quantity(1).build();

        itemService.addItem(default_item_1);
        itemService.addItem(default_item_2);

        //When
        itemService.displayCart();

        //Then
        assertEquals(1232, itemService.getCart().getAppliedPromotionId());
    }

    @Test
    public void cart_should_select_most_advantageous_promotion_type_if_there_are_many_applicable_promotions(){
        //Given
        ItemRequest default_item_1 = ItemRequest.builder().itemId(1).categoryId(3003).price(2000).sellerId(5006).quantity(1).build();
        ItemRequest default_item_2 = ItemRequest.builder().itemId(2).categoryId(3003).price(2000).sellerId(5006).quantity(1).build();
        ItemRequest default_item_3 = ItemRequest.builder().itemId(3).categoryId(1002).price(600).sellerId(5006).quantity(1).build();

        itemService.addItem(default_item_1);
        itemService.addItem(default_item_2);
        itemService.addItem(default_item_3);

        //When
        itemService.displayCart();

        //Then
        assertEquals(9909, itemService.getCart().getAppliedPromotionId());
    }
}