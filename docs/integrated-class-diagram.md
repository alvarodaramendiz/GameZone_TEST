# Integrated Class Diagram — GameZone Unicesar

This is the resulting class diagram from the integration process oriented by Requirement 5. It shows the four
layers (`ui`, `service`, `persistence`, `model`) and every module: products, people,
sales, accessories, promotions, warranties and returns. Only the main attributes and
methods are shown.

```mermaid

classDiagram
    direction TB

    %% ===================== UI =====================
    class MainFrame {
        +MainFrame()
    }
    class ProductPanel
    class PersonPanel
    class SalePanel
    class AccessoryPanel
    class PromotionPanel
    class WarrantyPanel
    class ReturnPanel

    %% ===================== SERVICE =====================
    class ProductService {
        +registerVideoGame(videoGame VideoGame) String
        +registerConsole(console Console) String
        +listProducts() List~Product~
        +hasEnoughStock(productId String, quantity int) boolean
        +updateStock(productId String, quantity int) void
        +restoreStock(productId String, quantity int) String
    }
    class CustomerService {
        +registerCustomer(eMail String, name String, iD long, contactNumber long) void
        +searchCustomer(iD long) Customer
    }
    class SellerService {
        +registerSeller(long employeeCode, Shift shift, String name, long iD, long contactNumber) void
        +searchSeller(employeeCode long) Seller
    }
    class AccessoryService {
        +registerController(String productId, String title, double price, int stock, String connectionType, List<String> compatibleConsoles) Controller
        +registerCable(String productId, String title, double price, int stock, double lengthInMeters, String connectorType, List<String> compatibleConsoles) Cable
        +registerMemory(String productId, String title, double price, int stock, int capacityInGigabytes, String memoryType, List<String> compatibleConsoles) Memory
        +listAccessoriesByType(type String) List~Accessory~
        +findAccessoriesCompatibleWith(consoleId String) List~Accessory~
        +updateStock(accessoryId String, quantity int) void
        +restoreStock(accessoryId String, quantity int) void
    }
    class PromotionService {
        +registerPercentageDiscount(String promotionId, String name, LocalDate startDate, LocalDate endDate, double percentage) PercentageDiscount
        +registerCategoryDiscount(String promotionId, String name, LocalDate startDate, LocalDate endDate, double percentage, String targetCategory) CategoryDiscount
        +registerBulkPurchaseDiscount(String promotionId, String name, LocalDate startDate, LocalDate endDate, int minimumQuantity, double percentage) BulkPurchaseDiscount
        +listActivePromotions() List~Promotion~
        +findBestPromotionFor(sale Sale) Promotion
    }
    class WarrantyService {
        +assignBasicWarranty(product Product, sale Sale, startDate LocalDate) BasicWarranty
        +assignExtendedWarranty(product Product, sale Sale, startDate LocalDate) ExtendedWarranty
        +listActiveWarranties() List~Warranty~
        +listWarrantiesExpiringSoon(daysAhead int) List~Warranty~
        +cancelWarranties(productId String, saleId String) double
    }
    class SaleService {
        +registerSale(customerId long, employeeCode long, productQuantities Map, extendedWarrantyConsoleIds Set) Sale
        +viewAllSales() List~Sale~
    }
    class ReturnService {
        +registerReturn(saleId long, productIds List, reason String, amount double) Return
        +calculateMonthlySales(month int, year int) double
        +calculateMonthlyReturns(month int, year int) double
        +generateMonthlyBalance(month int, year int) double
    }

    %% ===================== PERSISTENCE =====================
    class ProductPersistence {
        +listProducts() List~Product~
        +updateProduct(product Product) void
    }
    class CustomerPersistence
    class SellerPersistence
    class AccessoryRepository {
        +saveAll(accessories List~Accessory~) void
        +loadAll() List~Accessory~
    }
    class PromotionRepository {
        +saveAll(promotions List~Promotion~) void
        +loadAll() List~Promotion~
    }
    class WarrantyRepository {
        +saveAll(warranties List~Warranty~) void
        +loadAll() List~WarrantyRecord~
    }
    class WarrantyRecord
    class SalePersistence {
        +saveAll(sales List~Sale~) void
        +loadAll() List~Sale~
    }
    class ReturnPersistence {
        +saveAll(returns List~Return~) void
        +loadAll() List~Return~
    }

    %% ===================== MODEL =====================
    class Product {
        <<abstract>>
        -productId: String
        -title: String
        -price: double
        -stock: int
        +getDescription()* String
    }
    class VideoGame
    class Console
    class Accessory {
        <<abstract>>
        -compatibleConsoleIds: List~String~
        +isCompatibleWith(consoleId String) boolean
    }
    class Controller
    class Cable
    class Memory

    class Person {
        <<abstract>>
    }
    class Customer
    class Seller

    class Promotion {
        <<abstract>>
        -startDate: LocalDate
        -endDate: LocalDate
        +isActive(date LocalDate) boolean
        +calculateDiscount(sale Sale)* double
    }
    class PercentageDiscount
    class CategoryDiscount
    class BulkPurchaseDiscount

    class Warranty {
        <<abstract>>
        -startDate: LocalDate
        -endDate: LocalDate
        +getDurationInMonths()* int
        +getAdditionalCost()* double
        +isActive(date LocalDate) boolean
    }
    class BasicWarranty
    class ExtendedWarranty

    class Sale {
        -appliedPromotionName: String
        -discountAmount: double
        -extendedWarrantyCost: double
        -totalValue: double
        +getSubtotal() double
        +receiptGen() String
        +canBeReturned() boolean
    }
    class Return {
        -reimbursedAmount: double
        -warrantyRefund: double
        +calculateRefundAmount() double
        +generateReturnReceipt() String
    }

    %% ===================== INHERITANCE =====================
    Product <|-- VideoGame
    Product <|-- Console
    Product <|-- Accessory
    Accessory <|-- Controller
    Accessory <|-- Cable
    Accessory <|-- Memory
    Person <|-- Customer
    Person <|-- Seller
    Promotion <|-- PercentageDiscount
    Promotion <|-- CategoryDiscount
    Promotion <|-- BulkPurchaseDiscount
    Warranty <|-- BasicWarranty
    Warranty <|-- ExtendedWarranty

    %% ===================== MODEL RELATIONS =====================
    Sale --> Customer
    Sale --> Seller
    Sale o-- Product : items
    Warranty --> Product
    Warranty --> Sale
    Return --> Sale
    Return o-- Product : returned items

    %% ===================== UI -> SERVICE =====================
    MainFrame --> ProductPanel
    MainFrame --> PersonPanel
    MainFrame --> SalePanel
    MainFrame --> AccessoryPanel
    MainFrame --> PromotionPanel
    MainFrame --> WarrantyPanel
    MainFrame --> ReturnPanel
    ProductPanel --> ProductService
    PersonPanel --> CustomerService
    PersonPanel --> SellerService
    SalePanel --> SaleService
    AccessoryPanel --> AccessoryService
    PromotionPanel --> PromotionService
    WarrantyPanel --> WarrantyService
    ReturnPanel --> ReturnService

    %% ===================== SERVICE -> SERVICE =====================
    SaleService --> ProductService
    SaleService --> AccessoryService
    SaleService --> PromotionService
    SaleService --> WarrantyService
    SaleService --> CustomerService
    SaleService --> SellerService
    ReturnService --> SaleService
    ReturnService --> ProductService
    ReturnService --> AccessoryService
    ReturnService --> WarrantyService
    WarrantyService --> ProductService

    %% ===================== SERVICE -> PERSISTENCE =====================
    ProductService --> ProductPersistence
    CustomerService --> CustomerPersistence
    SellerService --> SellerPersistence
    AccessoryService --> AccessoryRepository
    PromotionService --> PromotionRepository
    WarrantyService --> WarrantyRepository
    WarrantyService --> SalePersistence
    SaleService --> SalePersistence
    ReturnService --> ReturnPersistence