# Class Diagram — GameZone Unicesar

Complete class structure showing all classes, attributes, methods, and relationships in the GameZone Unicesar application.

```mermaid

classDiagram

    %% =========================================================
    %% MODEL LAYER
    %% =========================================================

    namespace model {
        class Product {
            <<abstract>>
            -productId: String
            -title: String
            -price: double
            -stock: int
            +Product(productId String, title String, price double, stock int)
            +getProductId() String
            +getTitle() String
            +getPrice() double
            +getStock() int
            +setProductId(productId String) void
            +setTitle(title String) void
            +setPrice(price double) void
            +setStock(stock int) void
            +getDescription() String*
            +toText() String
        }

        class VideoGame {
            -platform: String
            -genre: String
            -ageRating: String
            +VideoGame(platform String, genre String, ageRating String, productId String, title String, price double, stock int)
            +getPlatform() String
            +getGenre() String
            +getAgeRating() String
            +setPlatform(platform String) void
            +setGenre(genre String) void
            +setAgeRating(ageRating String) void
            +getDescription() String
            +toText() String
        }

        class Console {
            -brand: String
            -model: String
            -generation: String
            +Console(brand String, model String, generation String, productId String, title String, price double, stock int)
            +getBrand() String
            +getModel() String
            +getGeneration() String
            +setBrand(brand String) void
            +setModel(model String) void
            +setGeneration(generation String) void
            +getDescription() String
            +toText() String
        }

        class Person {
            <<abstract>>
            -name: String
            -iD: long
            -contactNumber: long
            +Person()
            +Person(name String, iD long, contactNumber long)
            +getName() String
            +setName(name String) void
            +getiD() long
            +setiD(iD long) void
            +getContactNumber() long
            +setContactNumber(contactNumber long) void
            +toText() String
        }

        class Customer {
            -eMail: String
            +Customer()
            +Customer(eMail String, name String, iD long, contactNumber long)
            +geteMail() String
            +seteMail(eMail String) void
            +toText() String
        }

        class Seller {
            -employeeCode: long
            -shift: Shift
            +Seller()
            +Seller(employeeCode long, shift Shift, name String, iD long, contactNumber long)
            +getEmployeeCode() long
            +setEmployeeCode(employeeCode long) void
            +getShift() Shift
            +setShift(shift Shift) void
            +toText() String
        }

        class Shift {
            <<enumeration>>
            PART_TIME_MORNING
            PART_TIME_AFTERNOON
            FULL_TIME
            WEEKENDS
            HOLIDAYS
        }

        class Sale {
            -uId: long
            -date: LocalDate
            -productTrack: ArrayList~AmountOfProduct~
            -seller: Seller
            -customer: Customer
            -totalValue: double
            +Sale(uId long, date LocalDate, productTrack ArrayList, seller Seller, customer Customer)
            +getuId() long
            +setuId(uId long) void
            +getDate() LocalDate
            +setDate(date LocalDate) void
            +getProductTrack() ArrayList~AmountOfProduct~
            +setProductTrack(productTrack ArrayList) void
            +getSeller() Seller
            +setSeller(seller Seller) void
            +getCustomer() Customer
            +setCostumer(costumer Customer) void
            +saleTotalValue() double
            +receiptGen() String
        }

        class AmountOfProduct {
            <<inner class of Sale>>
            -soldProduct: Product
            -productAmount: int
            +AmountOfProduct()
            +AmountOfProduct(soldProduct Product, productAmount int)
            +getSoldProduct() Product
            +setSoldProduct(soldProduct Product) void
            +getProductAmount() int
            +setProductAmount(productAmount int) void
        }
    }

    Product <|-- VideoGame
    Product <|-- Console

    Person <|-- Customer
    Person <|-- Seller
    Seller --> Shift

    Sale "1" *-- "1..*" AmountOfProduct : contains
    Sale "1" --> "1" Customer
    Sale "1" --> "1" Seller
    AmountOfProduct --> Product


    %% =========================================================
    %% PERSISTENCE LAYER
    %% =========================================================

    namespace persistence {
        class ProductPersistence {
            +saveProduct(product Product) void
            +listProducts() List~Product~
            +searchProduct(productId String) Product
            +existsProduct(productId String) boolean
            +updateProduct(product Product) void
            -loadVideoGames() List~Product~
            -loadConsoles() List~Product~
        }

        class CustomerPersistence {
            +saveCustomer(customer Customer) void
            +listCustomers() ArrayList~Customer~
            +searchCustomer(id long) Customer
            +updateCustomer(customer Customer) void
            +deleteCustomer(iD long) void
            -saveCustomers(customers ArrayList) void
        }

        class SellerPersistence {
            +saveSeller(seller Seller) void
            +listSellers() ArrayList~Seller~
            +searchSeller(employeeCode long) Seller
            +updateSeller(seller Seller) void
            +deleteSeller(employeeCode long) void
            -saveSellers(sellers ArrayList) void
        }

        class SalePersistence {
            -FILE_PATH: String
            -FIELD_SEPARATOR: String
            -PRODUCT_SEPARATOR: String
            -PRODUCT_JOINER: String
            -PRODUCT_AMOUNT_SEPARATOR: String
            -productService: ProductService
            -sellerService: SellerService
            -customerService: CustomerService
            +SalePersistence(productService ProductService, sellerService SellerService, customerService CustomerService)
            +saveAll(sales List~Sale~) void
            +loadAll() List~Sale~
            -toCsvLine(sale Sale) String
            -productTrackToField(productTrack List) String
            -fromCsvLine(line String) Sale
            -fieldToProductTrack(sale Sale, productsField String) ArrayList~AmountOfProduct~
            -findProduct(productId String) Product
        }

    }

    ProductPersistence ..> Product
    CustomerPersistence ..> Customer
    SellerPersistence ..> Seller
    SalePersistence ..> Sale
    SalePersistence --> ProductService
    SalePersistence --> SellerService
    SalePersistence --> CustomerService


    %% =========================================================
    %% SERVICE LAYER
    %% =========================================================

    namespace service {
        class ProductService {
            -productDAO: ProductPersistence
            +ProductService()
            +registerVideoGame(videoGame VideoGame) String
            +registerConsole(console Console) String
            +listProducts() List~Product~
            +hasEnoughStock(productId String, quantity int) boolean
            +updateStock(productId String, quantity int) void
            -validateProduct(product Product) String
            -isValidAgeRating(ageRating String) boolean
        }

        class CustomerService {
            -customerDAO: CustomerPersistence
            +CustomerService()
            +validateCustomer(eMail String, name String, iD long, contactNumber long) void
            +registerCustomer(eMail String, name String, iD long, contactNumber long) void
            +updateCustomer(iD long, eMail String, name String, contactNumber long) void
            +deleteCustomer(iD long) void
            +listCustomers() ArrayList~Customer~
            +searchCustomer(iD long) Customer
        }

        class SellerService {
            -sellerDAO: SellerPersistence
            +SellerService()
            +validateSeller(employeeCode long, shift Shift, name String, iD long, contactNumber long) void
            +registerSeller(employeeCode long, shift Shift, name String, iD long, contactNumber long) void
            +updateSeller(employeeCode long, shift Shift, name String, iD long, contactNumber long) void
            +deleteSeller(employeeCode long) void
            +listSellers() ArrayList~Seller~
            +searchSeller(employeeCode long) Seller
        }

        class SaleService {
            -repository: SalePersistence
            -productService: ProductService
            -sellerService: SellerService
            -customerService: CustomerService
            -sales: List~Sale~
            +SaleService(repository SalePersistence, productService ProductService, sellerService SellerService, customerService CustomerService)
            +registerSale(customerId long, employeeCode long, productQuantities Map~String,Integer~) Sale
            +viewAllSales() List~Sale~
            +viewSalesByCustomer(customerId long) List~Sale~
            +viewSalesBySeller(employeeCode long) List~Sale~
            -buildProductTrack(sale Sale, productQuantities Map) ArrayList~AmountOfProduct~
            -findProduct(productId String) Product
        }

    }

    ProductService "1" --> "1" ProductPersistence
    CustomerService "1" --> "1" CustomerPersistence
    SellerService "1" --> "1" SellerPersistence

    SaleService "1" --> "1" SalePersistence
    SaleService "1" --> "1" ProductService
    SaleService "1" --> "1" SellerService
    SaleService "1" --> "1" CustomerService

    ProductService ..> Product
    ProductService ..> VideoGame
    ProductService ..> Console
    CustomerService ..> Customer
    SellerService ..> Seller
    SellerService ..> Shift
    SaleService ..> Sale


    %% =========================================================
    %% UI LAYER
    %% =========================================================

    namespace ui {
        class MainFrame {
            -sidebar: JPanel
            -centerPanel: JPanel
            -cardLayout: CardLayout
            -btnHome: JButton
            -btnProducts: JButton
            -btnPeople: JButton
            -btnSales: JButton
            -btnExit: JButton
            +MainFrame()
            -initComponents() void
            -createMenuButton(text String) JButton
            +main(args String[]) void
        }

        class ProductPanel {
            -productService: ProductService
            -txtId: JTextField
            -txtTitle: JTextField
            -txtPrice: JTextField
            -txtStock: JTextField
            -txtAttr1: JTextField
            -txtAttr2: JTextField
            -txtAttr3: JTextField
            -comboType: JComboBox
            -table: JTable
            -tableModel: DefaultTableModel
            +ProductPanel()
            -createFormPanel() JPanel
            -createTablePanel() JPanel
            -registerProduct() void
            -refreshTable() void
        }

        class PersonPanel {
            -customerService: CustomerService
            -sellerService: SellerService
            -txtEmail: JTextField
            -txtName: JTextField
            -txtId: JTextField
            -txtContact: JTextField
            -customerTable: JTable
            -sellerTable: JTable
            -customerTableModel: DefaultTableModel
            -sellerTableModel: DefaultTableModel
            +PersonPanel()
            -createCustomerTab() JPanel
            -createSellerTab() JPanel
            -registerCustomer() void
            -refreshCustomerTable() void
            -refreshSellerTable() void
        }

        class SalePanel {
            -productService: ProductService
            -sellerService: SellerService
            -customerService: CustomerService
            -salePersistence: SalePersistence
            -saleService: SaleService
            -txtCustomerId: JTextField
            -txtEmployeeCode: JTextField
            -comboProduct: JComboBox
            -txtQuantity: JTextField
            -cart: Map~String,Integer~
            -cartTableModel: DefaultTableModel
            -cartTable: JTable
            -salesTableModel: DefaultTableModel
            -salesTable: JTable
            +SalePanel()
            -createFormPanel() JPanel
            -createSalesTablePanel() JPanel
            -refreshProductCombo() void
            -addToCart() void
            -refreshCartTable() void
            -registerSale() void
            -refreshSalesTable() void
        }
    }

    MainFrame --> ProductPanel : creates
    MainFrame --> PersonPanel : creates
    MainFrame --> SalePanel : creates

    ProductPanel --> ProductService
    PersonPanel --> CustomerService
    PersonPanel --> SellerService

    SalePanel --> ProductService
    SalePanel --> CustomerService
    SalePanel --> SellerService
    SalePanel --> SalePersistence
    SalePanel --> SaleService
    %% =========================================================
    %% ENTRY POINT
    %% =========================================================
    class Main {
        ~SellerPersistence SellerDAO
        ~CustomerPersistence CustomerDAO
        +main(args String[]) void$
    }

    Main ..> MainFrame
    Main ..> SellerPersistence
    Main ..> CustomerPersistence