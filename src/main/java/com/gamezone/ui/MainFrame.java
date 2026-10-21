package com.gamezone.ui;
import com.gamezone.persistence.SalePersistence;
//import com.gamezone.persistence.ReturnPersistence;
import com.gamezone.persistence.ProductPersistence;
import com.gamezone.persistence.SellerPersistence;
import com.gamezone.persistence.CustomerPersistence;
import com.gamezone.service.CustomerService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;
import com.gamezone.service.SellerService;
//import com.gamezone.service.ReturnService;

import com.gamezone.service.AccessoryService;
import com.gamezone.persistence.AccessoryRepository;
import com.gamezone.service.PromotionService;
import com.gamezone.persistence.PromotionRepository;
import com.gamezone.service.WarrantyService;
import com.gamezone.persistence.WarrantyRepository;

import java.awt.*;
import javax.swing.*;



/**
 * Main window of the GameZone Unicesar management system.
 * Contains a sidebar menu and a central panel that switches
 * between views using CardLayout.
 *
 * @author alvaro
 */
public class MainFrame extends JFrame {

    private JPanel sidebar;
    private JPanel centerPanel;
    private CardLayout cardLayout;

    private JButton btnHome;
    private JButton btnProducts;
    private JButton btnPeople;
    private JButton btnSales;
    private JButton btnAccesories;
    private JButton btnPromotions;
    private JButton btnWarranties;
    private JButton btnExit;
    
    private final AccessoryRepository accessoryRepository = new AccessoryRepository();
    
    private final CustomerService customerService = new CustomerService();
    private final SellerService sellerService = new SellerService();
    private final ProductService productService = new ProductService();
    
    private final AccessoryService accessoryService = new AccessoryService(accessoryRepository);
    private final PromotionRepository promotionRepository = new PromotionRepository();
    private final PromotionService promotionService = new PromotionService(promotionRepository);
    private final SalePersistence salePersistence = new SalePersistence(productService, accessoryService, sellerService, customerService);
    


    private final WarrantyRepository warrantyRepository = new WarrantyRepository();
    private final WarrantyService warrantyService = new WarrantyService(warrantyRepository, salePersistence, productService);
        private final SaleService saleService = new SaleService(salePersistence, productService, accessoryService, promotionService, warrantyService, sellerService, customerService);

    public MainFrame() {
        setTitle("GameZone Unicesar - Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        initComponents();
    }
    

    private void initComponents() {
        setLayout(new BorderLayout());

        // Sidebar
        sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(0, 1, 0, 8));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(new Color(51, 51, 51));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        btnHome = createMenuButton("Inicio");
        btnProducts = createMenuButton("Productos");
        btnPeople = createMenuButton("Personas");
        btnSales = createMenuButton("Ventas");
        btnExit = createMenuButton("Salir");
        btnAccesories = createMenuButton("Accesorios");
        btnPromotions = createMenuButton("Promociones");
        btnWarranties = createMenuButton("Garantías");

        sidebar.add(btnHome);
        sidebar.add(btnProducts);
        sidebar.add(btnPeople);
        sidebar.add(btnSales);
        sidebar.add(btnWarranties);
        sidebar.add(new JLabel());
        sidebar.add(btnExit);
        sidebar.add(btnAccesories);
        sidebar.add(btnPromotions);

        // Center panel with CardLayout
        cardLayout = new CardLayout();
        centerPanel = new JPanel(cardLayout);

        centerPanel.add(new JLabel("Bienvenido a GameZone Unicesar", SwingConstants.CENTER), "home");
        centerPanel.add(new ProductPanel(productService), "products");
        centerPanel.add(new SalePanel(saleService, accessoryService), "sales");
        centerPanel.add(new PersonPanel(), "people");
        centerPanel.add(new AccessoryPanel(accessoryService), "accesories");
        centerPanel.add(new PromotionPanel(promotionService), "promotions");
        centerPanel.add(new WarrantyPanel(warrantyService, saleService), "warranties");
        

        btnHome.addActionListener(e -> cardLayout.show(centerPanel, "home"));
        btnProducts.addActionListener(e -> cardLayout.show(centerPanel, "products"));
        btnPeople.addActionListener(e -> cardLayout.show(centerPanel, "people"));
        btnSales.addActionListener(e -> cardLayout.show(centerPanel, "sales"));
        btnAccesories.addActionListener(e -> cardLayout.show(centerPanel, "accesories"));
        btnPromotions.addActionListener(e -> cardLayout.show(centerPanel, "promotions"));
        btnWarranties.addActionListener(e -> cardLayout.show(centerPanel, "warranties"));
        btnExit.addActionListener(e -> System.exit(0));

        add(sidebar, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 70, 70));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
    }
}