package com.example.checkoutCase.controller;

import com.example.checkoutCase.entity.CartResponse;
import com.example.checkoutCase.entity.ItemRequest.ItemRequest;
import com.example.checkoutCase.entity.RequestResponse;
import com.example.checkoutCase.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping("/items")
    public ResponseEntity<RequestResponse> addItem(@RequestBody ItemRequest newItemRequest) {return itemService.addItem(newItemRequest);}

    @PostMapping("/vasItems")
    public ResponseEntity<RequestResponse> addVasItem(@RequestBody ItemRequest newItemRequest) {return itemService.addVasItem(newItemRequest);}

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<RequestResponse> deleteItem(@PathVariable int itemId) {return itemService.deleteItem(itemId);}

    @DeleteMapping("/cart")
    public ResponseEntity<RequestResponse> resetCart(){return itemService.resetCart();}

    @GetMapping("/cart")
    public ResponseEntity<CartResponse> displayCart(){
        return itemService.displayCart();
    }
}
