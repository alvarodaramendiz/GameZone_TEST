package com.gamezone.ui;

import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.model.Product;
import com.gamezone.service.ReturnService;
import com.gamezone.service.SaleService;

import java.awt.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Panel for registering customers and listing customers/sellers.
 *
 *
 */
public class ReturnPanel extends JPanel {

    private ReturnService returnService;
    private SaleService saleService;

    private JTextField txtReason, txtId, txtUidForSearch, txtCustomerId, txtValue, txtMonth, txtYear;
    private JComboBox<String> comboProducts, comboOptions;
    private JTable returnsTable, viewReturnsTable, reportTable;
    private DefaultTableModel returnsTableModel, viewReturnsTableModel, reportTableModel;

    private java.util.List<String> productsId;

    /**
     * Creates a new ReturnPanel connected to the service of the module and
     * sales.
     *
     * @param returnService A service instance of the return module for
     * validations
     * @param saleService A service instance of the sale module for validations
     * @param productsId Collection of products IDs
     *
     */
    public ReturnPanel(ReturnService returnService, SaleService saleService) {
        this.returnService = returnService;
        this.saleService = saleService;
        productsId = new ArrayList<>();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Registro de Devoluciones", createReturnsTab());
        tabs.addTab("Lista de Devoluciones", createViewTab());
        tabs.addTab("Consulta de Balance Mensual", createReportTab());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createReturnsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel form = new JPanel(new GridLayout(2, 4, 8, 8));
        JPanel formId = new JPanel(new GridLayout(2, 4, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Registro de Devoluciones"));
        formId.setBorder(BorderFactory.createTitledBorder("Buscar Transacción"));
        txtReason = new JTextField();
        comboProducts = new JComboBox<>();
        txtId = new JTextField();
        txtValue = new JTextField();
        JButton btnSearchId = new JButton("Buscar transacción");
        btnSearchId.addActionListener(e -> searchId(txtId));
        formId.add(new JLabel("Número de Transacción:"));
        formId.add(txtId);
        formId.add(btnSearchId, BorderLayout.CENTER);
        form.add(new JLabel("Productos:"));
        form.add(comboProducts);
        form.add(new JLabel("Motivo de Devolución:"));
        form.add(txtReason);
        form.add(new JLabel("Reembolso:"));
        form.add(txtValue);
        JButton btnRegister = new JButton("Registrar devolución");
        btnRegister.addActionListener(e -> registerReturn());
        form.add(btnRegister, BorderLayout.CENTER);
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.add(formId, BorderLayout.NORTH);
        formWrapper.add(form, BorderLayout.SOUTH);
        returnsTableModel = new DefaultTableModel(
                new String[]{"ID Cliente", "ID Empleado", "ID Devolución", "ID Venta", "Fecha", "Productos", "Motivo de Devolución", "Reembolso"}, 0);
        returnsTable = new JTable(returnsTableModel);
        panel.add(formWrapper, BorderLayout.NORTH);
        panel.add(new JScrollPane(returnsTable), BorderLayout.CENTER);
        refreshReturnsTable();
        return panel;
    }

    private JPanel createViewTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        comboOptions = new JComboBox(new String[]{"Mostrar Todos", "por Cliente", "por Venta"});
        panel.add(comboOptions, BorderLayout.NORTH);
        viewReturnsTableModel = new DefaultTableModel();
        viewReturnsTable = new JTable(viewReturnsTableModel);
        viewReturnsTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        comboOptions.addActionListener(e -> {
            viewReturnsTableModel.setRowCount(0);
            JPanel temp = new JPanel();
            temp = refreshViewTable(comboOptions.getSelectedItem().toString());
            temp.setVisible(true);
            BorderLayout layout = (BorderLayout) panel.getLayout();
            java.awt.Component comp = layout.getLayoutComponent(panel, BorderLayout.CENTER);
            if (comp != null) {
                panel.remove(comp);
            }
            panel.add(temp, BorderLayout.CENTER);
            panel.revalidate();
            panel.repaint();
        });
        return panel;
    }

    private JPanel createReportTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel form = new JPanel(new GridLayout(2, 4, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Consulta de Balance Mensual"));
        txtMonth = new JTextField();
        txtYear = new JTextField();
        form.add(new JLabel("Mes:"));
        form.add(txtMonth);
        form.add(new JLabel("Año:"));
        form.add(txtYear);
        JButton btnSearch = new JButton("Buscar");
        btnSearch.addActionListener(e -> refreshReportTable());
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.add(form, BorderLayout.CENTER);
        formWrapper.add(btnSearch, BorderLayout.SOUTH);
        reportTableModel = new DefaultTableModel(
                new String[]{"Mes", "Año", "Balance Neto Mensual"}, 0);
        reportTable = new JTable(reportTableModel);
        panel.add(formWrapper, BorderLayout.NORTH);
        panel.add(new JScrollPane(reportTable), BorderLayout.CENTER);
        return panel;
    }

    private void searchId(JTextField txt) {
        try {
            productsId = new ArrayList<>();
            var sales = saleService.viewAllSales();
            long id = 0;
            for (Sale s : sales) {
                id = s.getuId();
                if (id == Long.parseLong(txt.getText())) {
                    JOptionPane.showMessageDialog(null, "Id de venta encontrada.", "Devoluciones", 0);
                    for (Sale.AmountOfProduct aP : s.getProductTrack()) {
                        comboProducts.addItem(aP.getSoldProduct().getProductId());
                        productsId.add(aP.getSoldProduct().getProductId());
                    }
                    return;
                }
            }
            JOptionPane.showMessageDialog(null, "No se encontró ninguna venta con este UID.", "Devoluciones", 0);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Entrada inválida.", "Devoluciones", 0);
        }
    }

    private void registerReturn() {
        try {
            long saleId = Long.parseLong(txtId.getText());
            String reason = txtReason.getText();
            double quantity = Double.parseDouble(txtValue.getText());
            returnService.registerReturn(saleId, productsId, reason, quantity);
            JOptionPane.showMessageDialog(this, "Devolución registrada con éxito.");
            refreshReturnsTable();
            txtId.setText("");
            txtReason.setText("");
            comboProducts = new JComboBox();
            txtValue.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Los IDs y la cantidad deben ser números válidos.", "Error de formato", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error en devolución", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshReturnsTable() {
        returnsTableModel.setRowCount(0);
        try {
            var returns = returnService.viewAllReturns();
            StringBuilder productsIn = new StringBuilder();
            for (Return r : returns) {
                var returned = r.getReturnedProducts();
                for (Product p : returned) {
                    productsIn.append("(").append(p.getProductId()).append(") ");
                }
                returnsTableModel.addRow(new Object[]{
                    r.getSale().getSeller().getEmployeeCode(),
                    r.getSale().getCustomer().getiD(),
                    r.getIdReturn(),
                    r.getSale().getuId(),
                    r.getDate(),
                    productsIn.toString(),
                    r.getReturnReason(),
                    r.getReimbursedAmount()
                });
            }
        } catch (Exception e) {
            System.out.println("Error al cargar la tabla de devoluciones: " + e.getMessage());
        }
    }

    private JPanel createViewAllTab(java.util.List<Return> returns) {
        JPanel pane = new JPanel(new BorderLayout(5, 5));
        JPanel button = new JPanel(new GridLayout(2, 4, 8, 8));
        JButton btnRefresh = new JButton();
        StringBuilder pIds = new StringBuilder();
        btnRefresh.setText("Actualizar lista");
        btnRefresh.addActionListener(e -> {
            pIds.setLength(0);
            viewReturnsTableModel.setColumnIdentifiers(new String[]{"ID Cliente", "ID Empleado", "ID Devolución", "ID Venta", "Fecha", "Productos", "Motivo de Devolución", "Reembolso"});
            viewReturnsTableModel.setRowCount(0);
            for (Return r : returns) {
                var returned = r.getReturnedProducts();
                for (Product p : returned) {
                    pIds.append("(").append(p.getProductId()).append(") ");
                }
                viewReturnsTableModel.addRow(new Object[]{
                    r.getSale().getSeller().getEmployeeCode(),
                    r.getSale().getCustomer().getiD(),
                    r.getIdReturn(),
                    r.getSale().getuId(),
                    r.getDate(),
                    pIds.toString(),
                    r.getReturnReason(),
                    r.getReimbursedAmount()
                });
            }
        });
        button.add(btnRefresh, BorderLayout.NORTH);
        pane.add(button, BorderLayout.NORTH);
        pane.add(new JScrollPane(viewReturnsTable), BorderLayout.CENTER);
        return pane;
    }

    private JPanel createByCustomerTab(java.util.List<Return> returns) {
        JPanel pane = new JPanel(new BorderLayout(5, 5));
        JPanel customerSearch = new JPanel(new GridLayout(2, 4, 8, 8));
        customerSearch.setBorder(BorderFactory.createTitledBorder("Consulta por Cliente"));
        JButton btnSearchCustomer = new JButton("Buscar");
        txtCustomerId = new JTextField();
        try {
            btnSearchCustomer.addActionListener(e -> {
                long id = Long.parseLong(txtCustomerId.getText());
                StringBuilder pIds = new StringBuilder();
                pIds.setLength(0);
                viewReturnsTableModel.setColumnIdentifiers(new String[]{"ID Cliente", "ID Empleado", "ID Devolución", "ID Venta", "Fecha", "Productos", "Motivo de Devolución", "Reembolso"});
                viewReturnsTableModel.setRowCount(0);
                for (Return r : returns) {
                    if (r.getSale().getCustomer().getiD() == id) {
                        var returned = r.getReturnedProducts();
                        for (Product p : returned) {
                            pIds.append("(").append(p.getProductId()).append(") ");
                        }
                        viewReturnsTableModel.addRow(new Object[]{
                            r.getSale().getSeller().getEmployeeCode(),
                            r.getSale().getCustomer().getiD(),
                            r.getIdReturn(),
                            r.getSale().getuId(),
                            r.getDate(),
                            pIds.toString(),
                            r.getReturnReason(),
                            r.getReimbursedAmount()
                        });
                        JOptionPane.showMessageDialog(null, "Se ha encontrado al cliente.", "Devoluciones", 0);
                    } else {
                        JOptionPane.showMessageDialog(null, "No se encontró ningún cliente con este ID.", "Devoluciones", 0);
                    }
                }

            });
            customerSearch.add(new JLabel("ID de Cliente:"));
            customerSearch.add(txtCustomerId, BorderLayout.NORTH);
            customerSearch.add(btnSearchCustomer, BorderLayout.CENTER);
            pane.add(customerSearch, BorderLayout.NORTH);
            pane.add(new JScrollPane(viewReturnsTable), BorderLayout.CENTER);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Entrada inválida.", "Devoluciones", 0);
        }
        return pane;
    }

    private JPanel createIdSearchTab(java.util.List<Return> returns) {
        StringBuilder pIds = new StringBuilder();
        JPanel pane = new JPanel(new BorderLayout(5, 5));
        viewReturnsTableModel.setColumnIdentifiers(new String[]{"ID Cliente", "ID Empleado", "ID Devolución", "ID Venta", "Fecha", "Productos", "Motivo de Devolución", "Reembolso"});
        viewReturnsTableModel.setRowCount(0);
        JPanel idPanel = new JPanel(new GridLayout(2, 4, 8, 8));
        idPanel.setBorder(BorderFactory.createTitledBorder("Consultar por Venta"));
        txtUidForSearch = new JTextField();
        JButton btnSearchId = new JButton("Buscar transacción");
        btnSearchId.addActionListener(e -> searchId(txtUidForSearch));
        pIds.setLength(0);
        viewReturnsTableModel.setRowCount(0);
        for (Return r : returns) {
            var returned = r.getReturnedProducts();
            for (Product p : returned) {
                pIds.append("(").append(p.getProductId()).append(") ");
            }
            viewReturnsTableModel.addRow(new Object[]{
                r.getSale().getSeller().getEmployeeCode(),
                r.getSale().getCustomer().getiD(),
                r.getIdReturn(),
                r.getSale().getuId(),
                r.getDate(),
                pIds.toString(),
                r.getReturnReason(),
                r.getReimbursedAmount()
            });
        }

        idPanel.add(new JLabel("Número de Transacción:"));
        idPanel.add(txtUidForSearch, BorderLayout.NORTH);
        idPanel.add(btnSearchId, BorderLayout.CENTER);
        pane.add(idPanel, BorderLayout.NORTH);
        pane.add(new JScrollPane(viewReturnsTable), BorderLayout.CENTER);
        return pane;
    }
    
    private JPanel refreshViewTable(String option) {
        switch (option) {
            case "Mostrar Todos":
                return createViewAllTab(returnService.viewAllReturns());
            case "por Cliente":
                return createByCustomerTab(returnService.viewAllReturns());
            case "por Venta":
                return createIdSearchTab(returnService.viewAllReturns());
        }
        return null;
    }

    private void refreshReportTable() {
        String month = txtMonth.getText();
        String year = txtYear.getText();
        try {
            reportTableModel.setRowCount(0);
            YearMonth yearMonth = YearMonth.of(Integer.valueOf(year), Integer.valueOf(month));
            double balance = 0;
            balance = returnService.generateMonthlyBalance(Integer.valueOf(year), Integer.valueOf(month));
            reportTableModel.addRow(new Object[]{
                yearMonth.getMonth().toString(),
                yearMonth.getYear(),
                balance}
            );
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Entrada inválida.", "Balance Mensual", 0);
        }
    }
}
