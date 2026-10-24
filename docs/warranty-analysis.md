## 1. The two types of warranty share common attributes (dates, associated product) but also have different attributes and behaviors (duration, coverage, cost). How is this situation reflected in the design of the class hierarchy? What object-oriented programming mechanism allows each type of warranty to have its own duration without duplicating code?

- Abstraction and polymorphism are the principles that allow this behaviour while the attributes may differ. They share a method but not a kind of behavior, just like an overloaded method that needs distinct parameters to perform multiple behaviors.

## 2. The business rule states that only consoles generate an automatic basic warranty, not video games. In which layer of the system is this decision located, and what Java mechanism is used to check the actual type of a product? Justify your answer.

- This is located in the WarrantyService, where, by a list of IDs with allowed extended warranty, and a list of consoles found in a sale, the system checks the availability comparing the lists.

## 3. The duration of each type of warranty is different (6 months or 12 months). How is the expiration date calculated in each subclass? Should this calculation be done in the warranty constructor or in a separate method? Justify your answer.

- Each concrete type has its amount of valid months (6 and 12) stored as an attribute. The calculation can be done by summing up that value into the initial date of the warranty, and storing it into the end date. This method is done in the constructor to make it default.

## 4. The extended warranty adds a cost of 10% of the product's price to the sale total. At what point in the sale registration workflow is this additional cost calculated and applied? What modifications are necessary in the SaleService.registerSale method?

- The cost is calculated after the sale is validated and saved: for each console, `registerSale` assigns the basic warranty and, if the product ID is in the new `productIdsWithExtendedWarranty` parameter, also the extended one, whose `getAdditionalCost()` returns 10% of the product's price. The sale total is then obtained with `SaleService.getSaleTotal`, which adds the warranties' additional cost to the products' value, so the `Sale` model does not need to be modified.

## 5. The query for "warranties about to expire" requires iterating over all warranties and filtering those whose end date is within the next 30 days. In which class is this method located, and what dependencies does it need? Why is this location consistent with the layered architecture?

- It is located in `WarrantyService.listWarrantiesExpiringSoon(int daysAhead)`, which depends on `WarrantyRepository` to get the warranties and on `LocalDate` to compare dates. This is consistent with the layered architecture because filtering by date is business logic that belongs to the service layer, while the UI only calls the service and the persistence layer only reads and writes the file.