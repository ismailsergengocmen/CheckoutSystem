package com.example.checkoutCase.repository;

import com.example.checkoutCase.entity.Item.Item;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ItemRepository extends MongoRepository<Item, String> {

    void deleteAll();

}
