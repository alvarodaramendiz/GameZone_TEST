# Layer Diagram — GameZone Unicesar

This is the resulting four-layer architecture after the integration of Requiremnt 5 adjustments: products, people, sales, accessories, promotions, warranties and returns now stay in the same diagram, within the same layer functionality.

```mermaid
---
config:
  layout: fixed
---
flowchart TB
    subgraph GUI["Visual Layer (UI)"]
        UI["MainFrame.java<br>ProductPanel.java<br>PersonPanel.java<br>SalePanel.java<br>AccessoryPanel.java<br>PromotionPanel.java<br>WarrantyPanel.java<br>ReturnPanel.java"]
    end
    
    subgraph SERVICE["Business Logic Layer (service)"]
        S["ProductService.java<br>CustomerService.java<br>SellerService.java<br>SaleService.java<br>AccessoryService.java<br>PromotionService.java<br>WarrantyService.java<br>ReturnService.java"]
    end
    
    subgraph PERSISTENCE["Data Persistence Layer (persistence)"]
        DM["ProductPersistence.java<br>CustomerPersistence.java<br>SellerPersistence.java<br>SalePersistence.java<br>AccessoryRepository.java<br>PromotionRepository.java<br>WarrantyRepository.java<br>WarrantyRecord.java<br>ReturnPersistence.java"]
    end
    
    subgraph MODEL["Model Layer (model)"]
        C["Product.java<br>VideoGame.java<br>Console.java<br>Accessory.java<br>Controller.java<br>Cable.java<br>Memory.java<br>Person.java<br>Customer.java<br>Seller.java<br>Sale.java<br>Promotion.java<br>PercentageDiscount.java<br>CategoryDiscount.java<br>BulkPurchaseDiscount.java<br>Warranty.java<br>BasicWarranty.java<br>ExtendedWarranty.java<br>Return.java"]
    end
    
    subgraph MAIN["Parent Layer (com.gamezone)"]
        n1["Main.java"]
    end
    
    n1 --> UI
    UI --> S
    S --> DM
    UI -.->|uses| C
    S -.->|uses| C
    DM -.->|persists/manages| C
    
    classDef ui fill:#dbeafe,stroke:#2563eb,stroke-width:2px,color:#111827
    classDef service fill:#dcfce7,stroke:#16a34a,stroke-width:2px,color:#111827
    classDef persistence fill:#fef3c7,stroke:#d97706,stroke-width:2px,color:#111827
    classDef model fill:#f3e8ff,stroke:#9333ea,stroke-width:2px,color:#111827
    classDef main fill:#fee2e2,stroke:#dc2626,stroke-width:2px,color:#111827
    
    class UI ui
    class S service
    class DM persistence
    class C model
    class n1 main
```

## Layer responsibilities

| Layer | Package | Responsibility |
|---|---|---|
| Presentation | `com.gamezone.ui` | Swing windows and panels; reads user input and shows results and messages in Spanish. |
| Business logic | `com.gamezone.service` | Business rules: stock validation, best promotion, warranty assignment, return rules and monthly balance. |
| Data access | `com.gamezone.persistence` | Reads and writes the text/CSV files in `data/`. |
| Model | `com.gamezone.model` | Domain classes and hierarchies (products, accessories, people, sales, promotions, warranties, returns). |

Dependencies always go in one direction: `ui → service → persistence → model`.

