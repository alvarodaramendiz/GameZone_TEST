##1. The three promotions have different calculation rules but share common attributes and behaviors. How is this situation reflected in the design of the class hierarchy? What object-oriented programming mechanism allows each type of promotion to calculate its discount differently without the rest of the system having to know the concrete types?

- The abstraction is a mechanism used in the hierarchy of these kind of classes where common attributes need a different behaviour with a required context, in this case, the differentiation between a promotion offer by bulk purchase, promotion by category or a simple promotion without any of the previous requirements. With an abstract parent class like promotion, assigned classes can share characteristics in common while producing different results.

##2. The base class Promotion cannot implement the discount calculation method because each type has a different logic. How is this method declared in the base class, and what does this declaration guarantee regarding the subclasses?

- The method can be declared in the base class by restricting a behaviour through instances. Since the method is inhereted, it needs to infer whether the class is from an specific kind (if its an instance of PercentageDiscount, or CategoryDiscount, or BulkPurchaseDiscount). This declaration guarantees that every class, when instanced, does have its unique calculation method.

##3. The business rule states that only the promotion with the highest discount is applied. In which class is this selection logic located, and why is this location consistent with the layered architecture principle? Why should this logic NOT be in the Sale class or in the console menu?

- By default, all business logic belongs to the service module, therefore, the PromotionService class is the most appropiate place to use a logic that recommends the best promotions. It cannot be used neither in the UI (or console, our project uses GUI) nor the Sale class, because that would overload the layer's responsibility.

##4. What modifications are necessary in the Sale class and in the generateReceipt method so that the receipt shows the applied discount? Do these modifications break any of the existing behavior in the system?

Sale class needs the name of the promotion and its respective percentage value, measured in decimal values between 1 and 0 (defined as a double). It does not break the system's behaviour only if the inicialization of the class' instances are correctly refactored. Either way, it requires some fixes needed to be addressed, but the overall behaviour remains the same.

##5. Current promotions are determined by comparing the current date with the start and end dates of each promotion. Where is this validation performed (in the Promotion class, in the PromotionService, or in both)? Justify your answer.

It is essentially performed in the Promotion class through a method called isActive, that receives a date value and returns a boolean value confirming if the promotion related to that instance is currently active or not by comparing its initial and expiring date values. 