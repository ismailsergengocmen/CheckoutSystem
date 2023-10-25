# Build
- docker build -t springboot-mongo .
- docker-compose up

# Extra Features
1) Users can increase quantity of an item by adding an item with same item id.
   - System does not check whether the newer one has correct attributes. The first item's properties are protected when new request is sent.
2) System stores the current item status to local mongodb instance.
   - This is added to protect the cart state if the application closes. Normally requests are handled without interacting with database.
   - You can see the last state of the cart in mongodb express(localhost:8081) after the springboot-mongo container stopped. 

# Design Choices
1) Only positive values are excepted as item request parameters.
2) ! Important => System accept defaultItems with 3242 since there is no explicitly said that ONLY vas items can be created with 3242.
   - Since it is explicitly stated in digital item that ONLY digital items can be created with 7889 and not here, it is assumed that 3242 can be used for both default and vas items. 
3) Users can increase quantity by using same item id but removal completely deletes related default item with its vas items.
4) Price is rounded to 2 decimal places. When you send a request to display cart, you see the total price after the discount applied.
5) 0 is used to show no promotion applied to the cart.

# Mappings
1) Add default/digital item
   - POST localhost:8080/api/v1/items

2) Add vas item
    - POST localhost:8080/api/v1/vasItems

3) Remove default/digital item
    - DELETE localhost:8080/api/v1/items/{id}

4) Reset cart
    - DELETE localhost:8080/api/v1/cart
   
5) Display cart
    - GET localhost:8080/api/v1/cart