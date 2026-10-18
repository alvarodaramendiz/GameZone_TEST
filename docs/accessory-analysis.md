
## 1. Should accessories be integrated into the existing product hierarchy (extending Product) or should they form an independent hierarchy? Justify your decision considering code reuse and model consistency.

- The accessories must form a hierarchy, but not independent from Product, because it inherits its attributes such as ID and Name. Accessory is a category by its own, which contains 3 types of objects that can be sold; cables, controllers and memories. Although, to approach code reutilization, it is more efficient to use Product as its parent class and form a hierarchy between Product and Accessory.

## 2. Which attributes are common to all three types of accessories and which are specific to each type? How is this distinction reflected in the module's class hierarchy?

- The Accessory abstract class inherits all core product attributes from Product (such as id, name, price, and stock) and introduces a private collection to manage its compatible consoles. Branching from this base, the Controller class introduces a private connection type field to specify if it is wired or wireless. The Cable class encapsulates a private double for its length in meters alongside a private String for the connector type. Finally, the Memory class defines its unique state using a private integer for storage capacity in gigabytes and a private String to identify the specific memory type.

## 3. Compatibility between an accessory and a console is a relationship between two system entities. How is this relationship represented in the design and in persistence? Is compatibility an attribute of the accessory, of the console, or of both?

- The compatibility is an attribute stored in the Accessory abstract class, which then gets inherited to the subtypes. In the domain design, this is represented as a structural association where an Accessory knows its target platforms. In the persistence layer, this relationship is represented visually as a collection of the available consoles that are compatible to the accesory.

## 4. What modifications are necessary in the sale service class (SaleService) so that sales can include accessories without breaking the existing behavior with video games and consoles?

- There are no major modifications since Accesories are involved in the Product hierarchy. It requires a subtle modification in registerSale method to include the accesories into the purchase. It will need an injection of both modules Product and Accesory, Product is already injected so it needs lastly the Accesory module injection through the AccesoryService class to connect with the available accesories.

## 5. In which layer of the system architecture should the new classes of the accessories module be located? Justify your decision based on the responsibilities of each layer.

- The new classes must be distributed across the layers of the system based on their direct structural responsibilities. The entity classes (Accessory, Controller, Cable, Memory) must be placed in the model Layer because they define the core business objects, data structures, and internal behaviors of the application. The logic for managing accessories belongs in the service layer (AccessoryService) to keep workflows organized. Finally, the data fetching logic must reside in the persistence layer (AccessoryRepository) to isolate the persistence framework from the rest of the application.