package com.gamezone.ui;

import com.gamezone.model.Accessory;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Console;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;

/**
 * Panel for registering sales (products and accessories) and viewing sales history.
 *
 * @author jahdiel
 */
public class SalePanel extends JPanel {

    private ProductService productService = new ProductService();

    private final AccessoryService accessoryService;
    private final SaleService saleService;

    private JTextField txtCustomerId, txtEmployeeCode;
    private JComboBox<String> comboProduct, comboAccesory;
    private JTextField txtQuantity;

    // Maps the id of a product or accessory to the quantity being sold
    private Map<String, Integer> cart = new LinkedHashMap<>();
    private DefaultTableModel cartTableModel;
    private JTable cartTable;

    private DefaultTableModel salesTableModel;
    private JTable salesTable;

    /**
     * Creates the sales panel.
     *
     * @param saleService      the shared sale service created in MainFrame
     * @param accessoryService the shared accessory service (the same instance
     *                         used by the accessories panel)
     */
    public SalePanel(SaleService saleService, AccessoryService accessoryService) {
        this.saleService = saleService;
        this.accessoryService = accessoryService;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(createFormPanel(), BorderLayout.NORTH);
        add(createSalesTablePanel(), BorderLayout.CENTER);

        refreshSalesTable();
    }

    private JPanel createFormPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(10, 10));
        wrapper.setBorder(BorderFactory.createTitledBorder("Registrar venta"));

        JPanel topFields = new JPanel(new GridLayout(3, 4, 8, 8));
        txtCustomerId = new JTextField();
        txtEmployeeCode = new JTextField();
        comboProduct = new JComboBox<>();
        comboAccesory = new JComboBox<>();
        txtQuantity = new JTextField();

        refreshProductCombo();
        refreshAccessoryCombo();

        // Reload the accessories every time the list is opened, so newly
        // registered accessories show up without restarting the application
        comboAccesory.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                refreshAccessoryCombo();
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        topFields.add(new JLabel("ID Cliente:"));
        topFields.add(txtCustomerId);
        topFields.add(new JLabel("Código Vendedor:"));
        topFields.add(txtEmployeeCode);
        topFields.add(new JLabel("Producto:"));
        topFields.add(comboProduct);
        topFields.add(new JLabel("Cantidad:"));
        topFields.add(txtQuantity);
        topFields.add(new JLabel("Accesorio:"));
        topFields.add(comboAccesory);
        topFields.add(new JLabel(""));
        topFields.add(new JLabel(""));

        JButton btnAddToCart = new JButton("Agregar al carrito");
        btnAddToCart.addActionListener(e -> addToCart());

        JButton btnAddAccessory = new JButton("Agregar accesorio al carrito");
        btnAddAccessory.addActionListener(e -> addAccessoryToCart());

        cartTableModel = new DefaultTableModel(new String[]{"Producto", "Cantidad"}, 0);
        cartTable = new JTable(cartTableModel);
        cartTable.setPreferredScrollableViewportSize(new Dimension(400, 100));

        JButton btnRegisterSale = new JButton("Registrar venta");
        btnRegisterSale.addActionListener(e -> registerSale());

        JPanel cartButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cartButtons.add(btnAddToCart);
        cartButtons.add(btnAddAccessory);
        cartButtons.add(btnRegisterSale);

        wrapper.add(topFields, BorderLayout.NORTH);
        wrapper.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        wrapper.add(cartButtons, BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel createSalesTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Historial de ventas"));

        salesTableModel = new DefaultTableModel(
                new String[]{"UID", "Fecha", "Cliente", "Vendedor", "Total"}, 0);
        salesTable = new JTable(salesTableModel);
        panel.add(new JScrollPane(salesTable), BorderLayout.CENTER);

        JButton btnRefresh = new JButton("Actualizar historial");
        btnRefresh.addActionListener(e -> refreshSalesTable());
        panel.add(btnRefresh, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshProductCombo() {
        comboProduct.removeAllItems();
        for (Product p : productService.listProducts()) {
            comboProduct.addItem(p.getProductId() + " - " + p.getTitle());
        }
    }

    private void refreshAccessoryCombo() {
        Object selected = comboAccesory.getSelectedItem();
        comboAccesory.removeAllItems();
        for (Accessory a : accessoryService.listAllAccessories()) {
            comboAccesory.addItem(a.getProductId() + " - " + a.getTitle());
        }
        if (selected != null) {
            comboAccesory.setSelectedItem(selected);
        }
    }

    private void addToCart() {
        addSelectionToCart(comboProduct, "No hay productos disponibles.");
    }

    private void addAccessoryToCart() {
        addSelectionToCart(comboAccesory, "No hay accesorios disponibles.");
    }

    /**
     * Adds the item selected in the given combo to the cart, using the quantity field.
     *
     * @param combo the combo whose selected item ("id - title") is added
     * @param emptyMessage the message shown when the combo has no items
     */
    private void addSelectionToCart(JComboBox<String> combo, String emptyMessage) {
        if (combo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, emptyMessage);
            return;
        }
        try {
            String selected = (String) combo.getSelectedItem();
            String itemId = selected.split(" - ")[0];
            int quantity = Integer.parseInt(txtQuantity.getText().trim());
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor que cero.");
                return;
            }
            cart.put(itemId, cart.getOrDefault(itemId, 0) + quantity);
            refreshCartTable();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un número válido.");
        }
    }

    private void refreshCartTable() {
        cartTableModel.setRowCount(0);
        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            cartTableModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }
    }

    private void registerSale() {
        try {
            long customerId = Long.parseLong(txtCustomerId.getText());
            long employeeCode = Long.parseLong(txtEmployeeCode.getText());

            Set<String> extendedWarranties = askExtendedWarranties();
            Sale sale = saleService.registerSale(customerId, employeeCode, cart, extendedWarranties);
            JOptionPane.showMessageDialog(this, new JTextArea(sale.receiptGen()), "Venta registrada", JOptionPane.INFORMATION_MESSAGE);
            cart.clear();
            refreshCartTable();
            refreshSalesTable();
            refreshProductCombo();
            refreshAccessoryCombo();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID de cliente y código de vendedor deben ser números válidos.");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    
        /**
     * Asks, for every console in the cart, whether the customer wants an
     * extended warranty.
     *
     * @return the ids of the consoles that get an extended warranty
     */
    private Set<String> askExtendedWarranties() {
        Set<String> extended = new LinkedHashSet<>();
        for (String itemId : cart.keySet()) {
            for (Product product : productService.listProducts()) {
                if (product instanceof Console && product.getProductId().equals(itemId)) {
                    int answer = JOptionPane.showConfirmDialog(this,
                            "¿Desea garantía extendida para " + product.getTitle() + "?\n"
                            + "Su costo se sumará al total de la venta.",
                            "Garantía extendida", JOptionPane.YES_NO_OPTION);
                    if (answer == JOptionPane.YES_OPTION) {
                        extended.add(itemId);
                    }
                }
            }
        }
        return extended;
    }
    

    private void refreshSalesTable() {
        salesTableModel.setRowCount(0);
        List<Sale> sales = saleService.viewAllSales();
        for (Sale s : sales) {
            salesTableModel.addRow(new Object[]{
                    s.getuId(), s.getDate(), s.getCustomer().getName(),
                    s.getSeller().getName(), s.getTotalValue()
            });
        }
    }
}