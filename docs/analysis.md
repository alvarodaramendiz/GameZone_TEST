# Analyis

## Regarding the people in the system

### 1. What attributes are common to all people who interact with the store, and which are specific to each particular type of person? How is this distinction reflected in a class hierarchy?

There are only two types of people who interact in the system: Seller and Costumer. They share essential attributes, such as an ID document (iD attribute), a name and a contact number. This means that they come from an abstract class Person that stores those attributes, and serves as a superclass for the previous two. The subclasses differ in private attributes that envolve their level of engagement with the system. From a customer you'd only need their email. On the other hand, from an employee or seller you'd need their employee code, which is related to their job, and a shift that indicates the employee's schedule through the week.

### 2. Should there be a class representing a "generic person" without specifying their role? Why or why not? What implication does this decision have regarding the possibility of instantiating that class?

You can have a generic person class which does not represent an specific role. However, the class cannot engage directly with the system, therefore, it cannot be instanced directly and needs child classes to generate instances of its attributes. It is considered an abstract class.

## Regarding the system products

### 3. What characteristics do all the products sold by the store have in common, regardless of their type? What characteristics are specific to each product type?

All sold products have a stock, a price, a sold amount and they are related to an specific sale transaction that contains all of the previous information, including seller and customer information. The products do have specific characteristics; for a Console you have its model as an attribute, the brand it belongs to and the generation in which it was released. For a videogame, it contains its provider's platform, a genre describing the type of game and an age rating to deliver to a specific public.

### 4. Each product type must be able to provide a description that incorporates its specific characteristics. How should this behavior be declared in the base class to ensure that all subclasses implement it in their own way? Which object-oriented programming mechanism enables this?

This behavior is declared in the abstract Product class as an abstract method, which has no implementation in the base class. Each subclass, Console and Videogame, is forced to override this method and build its own description using its particular attributes, model, brand and generation for Console, and platform, genre and age rating for Videogame. The mechanism that makes this possible is polymorphism, since the system can call getDescription() on any Product reference without knowing its concrete type, and the correct version runs automatically depending on the actual object.

## Regarding sales and relationships between entities

### 5. A sale involves a customer, a salesperson, and one or more products. What type of relationships exist between the class representing the sale and the other classes in the system? Are these relationships inheritance, association, composition, or of another type? Justify your answer.

The relationships between Sale and the other classes are associations rather than inheritance or composition. Sale is related to Customer and Seller through simple associations, since both people exist independently before and after the sale and can take part in other sales as well, so the sale does not own or control their lifecycle. Sale is also related to Product through an association, since it keeps a reference to the products and quantities involved in the transaction, but the products themselves belong to the store's inventory and are not created or destroyed depending on the sale. There is no inheritance in any of these cases, because a sale is not a type of customer, seller or product, it simply references and uses them.

### 6. Should the sale be responsible for calculating its own total, or should this responsibility fall to another class? Justify your decision.

The calculation of total value is only assigned to the sale module, it cannot be calculated from another class aside SaleService, which belongs to the sale module. It retreives and sends back information between the sale domain (Sale.java).

## Regarding business constraints

### 7. How does the design ensure that a sale cannot be recorded without at least one product? At what point in the system should this rule be validated?

This rule is validated in SaleService, where the system reads the entry from an user interface instance through a custom function called registerSale, and validates the products involved in the sale transaction using other custom methods, specially focused on stock validation, actors' information validation (the information of both customer and seller) and product validation (if the sale contains at least one product, otherwise the process is immediately terminated).

### 8. How is the automatic inventory update reflected in the design when a sale is recorded? Which classes are involved in this operation?

The classes that engage in this process involve both service and persistence packages, specifically, ProductPersistence, ProductService and SaleService. Whenever a sale is completely validated, the SaleService calls ProductService through an instance to use function that references the products that were bought and the amount of unities sold from that product, infering the decrease in the item's inventory through the ProductService validation, and finally persisting this change with a function that saves it in the .csv where all of the products' information belong to.

## Regarding layer organization

### 9. The system must be organized into four layers: model, persistence, services, and user interface. What types of classes belong to each layer? What criterion determines which layer a class should be placed in?

A model contains all of the domain classes of the system. Those classes contain the essential information about every entity that engages in the system such as people, products and sales. 
A persistence contains the respective files of the domain, there lays all of the saving and loading files logic which connects the program to a physical space, such as a plain archive.
A service contains the business logic, where classes respective to the domain validate entries and determine values that qualify to be saved in a database.
An user interface serves as a management menu uniquely, it only contains the visual part and the entries obtained from the real world, in this scenario, from the GameZone Unicesar game store.

### 10. Why shouldn't the logic for saving and retrieving data from files be located within domain classes? What problems arise when these responsibilities are mixed?

The logic for saving and retrieving data should not live inside domain classes because their only responsibility should be representing the business information and rules of the system, not handling file storage. If a class like Person, Product or Sale also handled file reading and writing, it would have more than one reason to change, since both a business rule update and a storage format change would affect it, which goes against the rules of the assignment. It would also reduce reusability, since the same domain object could not be reused in a different context such as testing or a different storage engine without dragging file-handling code with it. Testing would become harder too, because verifying business logic would require an actual file system to be present. Finally, mixing these responsibilities creates tight coupling between the domain and the storage mechanism, so any change in the file format would force changes across the domain layer instead of staying isolated in the persistence layer, breaking the separation of concerns the four-layer architecture is meant to protect.

### 11. Which dependencies are permitted between the layers, and which are prohibited? Justify the rationale behind the permitted dependencies.

The system's layer architecture has a hierchical dependency concept, where the ui lays in the summit, connects with the service that both takes and makes changes from the persistence layer, and borrows information from the model layer for validations. The UI cannot directly connect to the persistence and the model, it is strictly prohibited since it'd violate the entire development flow of a CRUD system.