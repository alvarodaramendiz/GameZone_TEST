package com.gamezone.ui;

import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.persistence.SalePersistence;
import com.gamezone.service.CustomerService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;
import com.gamezone.service.SellerService;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Panel for registering sales and viewing sales history.
 *
 * @author jahdiel
 */
public class SalePanel extends JPanel {

    private ProductService productService = new ProductService();
    private SellerService sellerService = new SellerService();
    private CustomerService customerService = new CustomerService();
    private SalePersistence salePersistence =
            new SalePersistence(productService, sellerService, customerService);
    private SaleService saleService =
            new SaleService(salePersistence, productService, sellerService, customerService);

    private JTextField txtCustomerId, txtEmployeeCode;
    private JComboBox<String> comboProduct, comboAccesory;
    private JTextField txtQuantity;
    private Map<String, Integer> cart = new LinkedHashMap<>();

    private DefaultTableModel cartTableModel;
    private JTable cartTable;
    private DefaultTableModel salesTableModel;
    private JTable salesTable;

    public SalePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(createFormPanel(), BorderLayout.NORTH);
        add(createSalesTablePanel(), BorderLayout.CENTER);

        refreshSalesTable();
    }

    private JPanel createFormPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(10, 10));
        wrapper.setBorder(BorderFactory.createTitledBorder("Registrar venta"));

        JPanel topFields = new JPanel(new GridLayout(2, 4, 8, 8));
        txtCustomerId = new JTextField();
        txtEmployeeCode = new JTextField();
        comboProduct = new JComboBox<>();
        txtQuantity = new JTextField();

        refreshProductCombo();

        topFields.add(new JLabel("ID Cliente:"));
        topFields.add(txtCustomerId);
        topFields.add(new JLabel("Código Vendedor:"));
        topFields.add(txtEmployeeCode);

        topFields.add(new JLabel("Producto:"));
        topFields.add(comboProduct);
        topFields.add(new JLabel("Cantidad:"));
        topFields.add(txtQuantity);

        JButton btnAddToCart = new JButton("Agregar al carrito");
        btnAddToCart.addActionListener(e -> addToCart());

        cartTableModel = new DefaultTableModel(new String[]{"Producto", "Cantidad"}, 0);
        cartTable = new JTable(cartTableModel);
        cartTable.setPreferredScrollableViewportSize(new Dimension(400, 100));

        JButton btnRegisterSale = new JButton("Registrar venta");
        btnRegisterSale.addActionListener(e -> registerSale());

        JPanel cartButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cartButtons.add(btnAddToCart);
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

    private void addToCart() {
        if (comboProduct.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "No hay productos disponibles.");
            return;
        }
        try {
            String selected = (String) comboProduct.getSelectedItem();
            String productId = selected.split(" - ")[0];
            int quantity = Integer.parseInt(txtQuantity.getText());

            cart.put(productId, cart.getOrDefault(productId, 0) + quantity);
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

            Sale sale = saleService.registerSale(customerId, employeeCode, cart);

            JOptionPane.showMessageDialog(this, "Venta registrada. Total: " + sale.saleTotalValue());
            cart.clear();
            refreshCartTable();
            refreshSalesTable();
            refreshProductCombo();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID de cliente y código de vendedor deben ser números válidos.");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void refreshSalesTable() {
        salesTableModel.setRowCount(0);
        List<Sale> sales = saleService.viewAllSales();
        for (Sale s : sales) {
            salesTableModel.addRow(new Object[]{
                    s.getuId(), s.getDate(), s.getCustomer().getName(),
                    s.getSeller().getName(), s.saleTotalValue()
            });
        }
    }
}