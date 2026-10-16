## 1. A return is a new system entity that references an existing sale. What type of relationship exists between the Return class and the Sale class? Is this relationship inheritance, association, aggregation, or composition? Justify your answer.

- It is an association connection. It does not require any attribute or method inheretence from the sale, it would rather need a at least a Transaction parent class to do so. A return must be always associated with an existing sale registry, allowing the staff to perform refunds according to certain products in the sale. The relationship reflects in the share of products that were sold.

## 2. A return can contain only some products from the original sale, not necessarily all of them. How is this situation represented in the attributes of the Return class? What is stored in the returned products attribute?

- The Return class only stores the IDs related to the products, storing them into either an ArrayList, a List, or a Map that saves the IDs as keys and a value that indicates the amount of the specific product that was sold and registred in the sale.

## 3. The business rule states that returns can only be registered within 30 days following the sale. In which layer of the system is this validation located, and why? What Java mechanism is used to calculate the difference between two dates?

- The validation is located in the service layer, specificly the ReturnService class which is related to the module. It's located there since the validation of allowed date range of return and sale association need to be done during the registration process. There are many mechanism, such as using an if-else sentence with a condition involving the initial date of the associated sale and a calculation of the 30 days above that date.

## 4. Returning products increases stock. What existing method from the Taller 1 system is reused for this operation, and in which class is it invoked from the returns module? Why is it important to reuse existing methods instead of duplicating the stock update logic?

- The method is applied in the ProductService class, named restoreStock. Reutilization of code is good for simplicity and is based in the polymorphism OOP principle. 

## 5. The monthly balance report requires consolidating information from two different modules (sales and returns). In which service class is this report located, and why is this location consistent with the layered architecture? What dependencies does this class need to generate it?

- It is located in the ReturnService class. It is consistent since the monthly balance is a validation that requires both services and instances for sales and returns. It needs returns and sales dependencies regarding the service, to obtain all of the availables, and model compare each of them.