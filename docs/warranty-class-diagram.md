# Warranty Module — Class Diagram

Classes of the warranty module and how they integrate with the existing system.
After adjustment A2, `WarrantyRepository` stores and loads only the identifiers of
the sale and the product, and `WarrantyService` resolves them using `SalePersistence`
and `ProductService`. This removes the cycle SaleService → WarrantyService →
WarrantyRepository → SaleService.

```mermaid
classDiagram
    direction LR

    class Warranty {
        <<abstract>>
        -warrantyId: String
        -product: Product
        -sale: Sale
        -startDate: LocalDate
        -endDate: LocalDate
        +Warranty(warrantyId String, product Product, sale Sale, startDate LocalDate)
        +getDurationInMonths()* int
        +getWarrantyType()* String
        +getAdditionalCost()* double
        +isActive(date LocalDate) boolean
        +generateWarrantyCertificate() String
    }

    class BasicWarranty {
        +getDurationInMonths() int
        +getWarrantyType() String
        +getAdditionalCost() double
    }

    class ExtendedWarranty {
        +getDurationInMonths() int
        +getWarrantyType() String
        +getAdditionalCost() double
    }

    class WarrantyRecord {
        -type: String
        -warrantyId: String
        -saleId: String
        -productId: String
        -startDate: LocalDate
    }

    class WarrantyRepository {
        -filePath: String
        +saveAll(warranties List~Warranty~) void
        +loadAll() List~WarrantyRecord~
    }

    class WarrantyService {
        -repository: WarrantyRepository
        -saleRepository: SalePersistence
        -productService: ProductService
        -warranties: List~Warranty~
        +assignBasicWarranty(product Product, sale Sale, startDate LocalDate) BasicWarranty
        +assignExtendedWarranty(product Product, sale Sale, startDate LocalDate) ExtendedWarranty
        +findWarrantyByProduct(productId String, saleId String) Warranty
        +listAllWarranties() List~Warranty~
        +listActiveWarranties() List~Warranty~
        +listWarrantiesExpiringSoon(daysAhead int) List~Warranty~
        +calculateAdditionalCost(sale Sale) double
        +getStatus(warranty Warranty) String
    }

    class SaleService {
        +registerSale(customerId long, employeeCode long, productQuantities Map, productIdsWithExtendedWarranty List) Sale
    }

    class SalePersistence {
        +loadAll() List~Sale~
    }

    class ProductService {
        +listProducts() List~Product~
    }

    class Product {
        <<abstract>>
    }
    class Console
    class Sale

    Warranty <|-- BasicWarranty
    Warranty <|-- ExtendedWarranty
    Product <|-- Console
    Warranty --> Product : covers
    Warranty --> Sale : belongs to

    WarrantyRepository ..> Warranty : saves
    WarrantyRepository ..> WarrantyRecord : loads
    WarrantyService --> WarrantyRepository
    WarrantyService --> SalePersistence : resolves sales
    WarrantyService --> ProductService : resolves products
    SaleService --> WarrantyService : assigns warranties to consoles
```

## Construction order (MainFrame)

1. `ProductService`, `SellerService`, `CustomerService`, `AccessoryService`, `PromotionService`
2. `SalePersistence`
3. `WarrantyRepository` (no dependencies)
4. `WarrantyService(warrantyRepository, salePersistence, productService)`
5. `SaleService(..., warrantyService)`
