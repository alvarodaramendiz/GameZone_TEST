# Layer Diagram — GameZone Unicesar

Architectural representation of the four-layer application structure showing dependencies and data flow between presentation, business logic, data access, and model layers.

```mermaid
---
config:
  layout: fixed
---
flowchart TB
    subgraph PRESENTACION["Capa de Presentación (ui)"]
        UI["MainFrame.java<br>ProductPanel.java<br>SalePanel.java<br>PersonPanel.java"]
    end
    
    subgraph LOGICA["Capa de Lógica de Negocio (service)"]
        L["SaleService.java<br>SellerService.java<br>CustomerService.java<br>ProductService.java"]
    end
    
    subgraph DATOS["Capa de Datos (persistence)"]
        DM["CustomerPersistence.java<br>SellerPersistence.java<br>ProductPersistence.java<br>SalePersistence.java"]
    end
    
    subgraph MODELO["Capa de Modelo (model)"]
        C["Sale.java<br>Seller.java<br>Customer.java<br>Product.java<br>Console.java<br>VideoGame.java"]
    end
    
    subgraph PRINCIPAL["Capa Principal (com.gamezone)"]
        n1["Main.java"]
    end
    
    n1 --> UI
    UI --> L
    L --> DM
    UI -.->|utiliza| C
    L -.->|utiliza| C
    DM -.->|persiste/gestiona| C
    
    classDef presentation fill:#dbeafe,stroke:#2563eb,stroke-width:2px,color:#111827
    classDef business fill:#dcfce7,stroke:#16a34a,stroke-width:2px,color:#111827
    classDef data fill:#fef3c7,stroke:#d97706,stroke-width:2px,color:#111827
    classDef model fill:#f3e8ff,stroke:#9333ea,stroke-width:2px,color:#111827
    classDef principal fill:#fee2e2,stroke:#dc2626,stroke-width:2px,color:#111827
    
    class UI presentation
    class L business
    class DM data
    class C model
    class n1 principal
