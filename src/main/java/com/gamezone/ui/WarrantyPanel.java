package com.gamezone.ui;

import com.gamezone.service.WarrantyService;
import com.gamezone.service.SaleService;
import com.gamezone.model.Warranty;
import com.gamezone.model.Sale;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Panel for consulting warranties.
 *
 * @author jahdiel
 */
public class WarrantyPanel extends JPanel {

    private static final int EXPIRING_SOON_DAYS = 30;
    private static final String PRODUCT_SEPARATOR = " - ";

    private final WarrantyService warrantyService;
    private final SaleService saleService;

    private Sale currentSale;

    private JTextField txtSaleId;
    private JComboBox<String> comboProducts;

    private JTable warrantyTable, viewWarrantyTable;
    private DefaultTableModel warrantyTableModel, viewWarrantyTableModel;

    public WarrantyPanel(WarrantyService warrantyService, SaleService saleService) {
        this.warrantyService = warrantyService;
        this.saleService = saleService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Consultar Garantía", createWarrantiesTab());
        tabs.addTab("Lista de Garantías", createViewTab());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createWarrantiesTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(1, 3, 8, 8));
        JPanel formSearch = new JPanel(new GridLayout(1, 3, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Buscar Garantía de Producto"));
        formSearch.setBorder(BorderFactory.createTitledBorder("Buscar Venta"));

        txtSaleId = new JTextField();
        comboProducts = new JComboBox<>();

        formSearch.add(new JLabel("Número de Transacción:"));
        formSearch.add(txtSaleId);

        form.add(new JLabel("Productos:"));
        form.add(comboProducts);

        JButton btnSearch = new JButton("Buscar Venta");
        btnSearch.addActionListener(e -> searchSale());
        formSearch.add(btnSearch);

        JButton btnRegister = new JButton("Comprobar Garantía");
        btnRegister.addActionListener(e -> searchWarranty());
        form.add(btnRegister);

        JPanel formWrapper = new JPanel(new BorderLayout(0, 10));
        formWrapper.add(formSearch, BorderLayout.NORTH);
        formWrapper.add(form, BorderLayout.SOUTH);

        warrantyTableModel = new DefaultTableModel(
                new String[]{"ID Garantía", "Transacción Asociada", "Tipo", "Producto asociado", "Fecha Inicial", "Vencimiento", "Estado" }, 0);
        warrantyTable = new JTable(warrantyTableModel);

        panel.add(formWrapper, BorderLayout.NORTH);
        panel.add(new JScrollPane(warrantyTable), BorderLayout.CENTER);

        refreshWarrantyTable();
        return panel;
    }

    private JPanel createViewTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));

        viewWarrantyTableModel = new DefaultTableModel(
                new String[]{"ID Garantía", "Transacción Asociada", "Tipo", "Producto asociado", "Fecha Inicial", "Vencimiento", "Estado"}, 0);
        viewWarrantyTable = new JTable(viewWarrantyTableModel);

        JButton btnViewAll = new JButton("Ver todas las garantías");
        btnViewAll.addActionListener(e -> RefreshViewAllWarranties());

        JButton btnAvailable = new JButton("Garantías vigentes");
        btnAvailable.addActionListener(e -> RefreshAvailableWarranties());

        JButton btnSoonExpire = new JButton("Garantías por expirar");
        btnSoonExpire.addActionListener(e -> RefreshSoonToExpireWarranties());

        panelButtons.add(btnViewAll);
        panelButtons.add(btnAvailable);
        panelButtons.add(btnSoonExpire);
        panel.add(panelButtons, BorderLayout.NORTH);
        panel.add(new JScrollPane(viewWarrantyTable), BorderLayout.CENTER);

        RefreshViewAllWarranties();
        return panel;
    }
    
    
    /**
     * Searches the sale typed by the user and fills the product combo with the
     * products that were sold in it.
     */
    private void searchSale() {
        currentSale = null;
        comboProducts.removeAllItems();
        warrantyTableModel.setRowCount(0);

        long saleId;
        try {
            saleId = Long.parseLong(txtSaleId.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El número de transacción debe ser un número válido.");
            return;
        }

        for (Sale sale : saleService.viewAllSales()) {
            if (sale.getuId() == saleId) {
                currentSale = sale;
                break;
            }
        }

        if (currentSale == null) {
            JOptionPane.showMessageDialog(this, "No se encontró la venta " + saleId + ".");
            return;
        }

        for (Sale.AmountOfProduct item : currentSale.getProductTrack()) {
            comboProducts.addItem(item.getSoldProduct().getProductId()
                    + PRODUCT_SEPARATOR + item.getSoldProduct().getTitle());
        }
    }

    /**
     * Looks up the warranty of the product selected in the combo, within the
     * sale found by searchSale, and shows it in the table.
     */
    private void searchWarranty() {
        if (currentSale == null) {
            JOptionPane.showMessageDialog(this, "Primero busque una venta.");
            return;
        }
        String selected = (String) comboProducts.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la venta.");
            return;
        }
        String productId = selected.substring(0, selected.indexOf(PRODUCT_SEPARATOR));

        Warranty warranty;
        try {
            warranty = warrantyService
                    .findWarrantyByProduct(productId, String.valueOf(currentSale.getuId()));
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "No se pudo consultar la garantía: " + e.getMessage());
            return;
        }

        warrantyTableModel.setRowCount(0);
        if (warranty == null) {
            JOptionPane.showMessageDialog(this, "El producto no tiene garantía registrada en esta venta.");
            return;
        }
        warrantyTableModel.addRow(toRow(warranty));
    }

    private Object[] toRow(Warranty warranty) {
        return new Object[]{
            warranty.getWarrantyId(),
            warranty.getSale().getuId(),
            warranty.getWarrantyType(),
            warranty.getProduct().getProductId() + PRODUCT_SEPARATOR + warranty.getProduct().getTitle(),
            warranty.getStartDate(),
            warranty.getEndDate(),
            warrantyService.getStatus(warranty)
        };
    }

    private void refreshWarrantyTable() {
        warrantyTableModel.setRowCount(0);
    }

    private void RefreshViewAllWarranties() {
        fillViewTable(warrantyService.listAllWarranties());
    }

    private void RefreshAvailableWarranties() {
        fillViewTable(warrantyService.listActiveWarranties());
    }

    private void RefreshSoonToExpireWarranties() {
        fillViewTable(warrantyService.listWarrantiesExpiringSoon(WarrantyService.EXPIRING_SOON_DAYS));
    }

    /**
     * Fills the list table with the given warranties.
     */
    private void fillViewTable(List<Warranty> warranties) {
        viewWarrantyTableModel.setRowCount(0);
        for (Warranty warranty : warranties) {
            viewWarrantyTableModel.addRow(toRow(warranty));
        }
    }
    

    
}