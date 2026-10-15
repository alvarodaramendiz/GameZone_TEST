# Hierarchy Diagram — GameZone Unicesar

Class inheritance hierarchy showing the relationships between abstract base classes and their concrete implementations.

```mermaid
classDiagram
    %% Person Hierarchy
    class Person {
        <<abstract>>
        -id: int
        -name: String
        -email: String
        +getId(): int
        +getName(): String
        +getEmail(): String
        +getFullName(): String
    }
    
    class Customer {
        -purchaseHistory: List~Sale~
        +getPurchaseHistory(): List~Sale~
        +addPurchase(sale: Sale): void
    }
    
    class Seller {
        -employeeCode: String
        -shift: String
        +getEmployeeCode(): String
        +getShift(): String
    }
    
    Person <|-- Customer : inherits
    Person <|-- Seller : inherits
    
    %% Product Hierarchy
    class Product {
        <<abstract>>
        -id: int
        -name: String
        -price: double
        -stock: int
        +getId(): int
        +getName(): String
        +getPrice(): double
        +getStock(): int
        +updateStock(quantity: int): void
        +getDescription(): String*
    }
    
    class VideoGame {
        -platform: String
        -genre: String
        -ageRating: String
        +getPlatform(): String
        +getGenre(): String
        +getAgeRating(): String
        +getDescription(): String
    }
    
    class Console {
        -brand: String
        -model: String
        -generation: int
        +getBrand(): String
        +getModel(): String
        +getGeneration(): int
        +getDescription(): String
    }
    
    Product <|-- VideoGame : inherits
    Product <|-- Console : inherits
    
    class Sale {
        -id: int
        -customer: Customer
        -seller: Seller
        -items: List~SaleItem~
        -date: LocalDateTime
        -status: String
        +getId(): int
        +getCustomer(): Customer
        +getSeller(): Seller
        +getItems(): List~SaleItem~
        +calculateTotal(): double
        +generateReceipt(): String
        +canBeReturned(): boolean
    }
    
    class SaleItem {
        -product: Product
        -quantity: int
        -unitPrice: double
        +getProduct(): Product
        +getQuantity(): int
        +getUnitPrice(): double
        +getSubtotal(): double
    }
    
    Sale "1" --> "*" SaleItem : contains
    SaleItem "1" --> "1" Product : references
    Sale "1" --> "1" Customer : involves
    Sale "1" --> "1" Seller : involves
