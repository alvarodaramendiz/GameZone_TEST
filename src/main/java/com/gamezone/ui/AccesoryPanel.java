package com.gamezone.ui;



import com.gamezone.model.Customer;
import com.gamezone.model.Seller;
import com.gamezone.model.Shift;
import com.gamezone.service.CustomerService;
import com.gamezone.service.SellerService;
import com.gamezone.service.ProductService;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Panel for registering customers and listing customers/sellers.
 *
 * @author jahdiel
 */
public class AccesoryPanel extends JPanel {

    private CustomerService customerService = new CustomerService();
    private SellerService sellerService = new SellerService();

    private JTextField txtPrice, txtName, txtId, txtStock, txtMonth, txtYear;
    private JComboBox<String> comboAccesoryType, comboConnection, comboAccesorySelect;
    private JTable registerAccesoryTable, viewAccesoryTable, AccesoriesByTypeTable;
    private DefaultTableModel registerAccesoryModel, viewAccesoryTableModel, AccesoriesByTypeTableModel;

    public AccesoryPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Registro de Accesorios", registerAccesoryTab());
        tabs.addTab("Lista de Accesorios", createViewAccesoriesTab());
        tabs.addTab("Consulta por Accesorio", createAccesoriesByTypeTab());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel registerAccesoryTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel registerPane = new JPanel(new GridLayout(2, 4));
        JPanel fieldPane = new JPanel(new GridLayout(2, 4, 5, 5));
        JPanel selectionPane = new JPanel(new GridLayout(2, 4, 5, 5));
        selectionPane.setBorder(BorderFactory.createTitledBorder("Selección de Accesorios"));
        fieldPane.setBorder(BorderFactory.createTitledBorder("Registro de Datos"));
        
        JLabel connectionType = new JLabel("Conexión:");
        
        txtName = new JTextField();
        txtStock = new JTextField();
        comboAccesoryType = new JComboBox<>(new String[]{"Cable", "Mando", "Memoria"});
        comboAccesorySelect = new JComboBox<>(new String[]{"Cable", "Mando", "Memoria"});
        comboConnection = new JComboBox<>(new String[]{"Alámbrica", "Inhalámbrica"});
        
        connectionType.setVisible(false);
        comboConnection.setVisible(false);
        
        comboAccesoryType.addActionListener(e ->{
            
            String type = comboAccesoryType.getSelectedItem().toString();
            if (type.equals("Mando")) {
                connectionType.setVisible(true);
                comboConnection.setVisible(true);
            } else {
                connectionType.setVisible(false);
                comboConnection.setVisible(false);
            }
            
        });
        comboConnection.addActionListener(e ->{});
        txtId = new JTextField();
        txtPrice = new JTextField();
        
        
        fieldPane.add(new JLabel("ID del Accesorio:"));
        fieldPane.add(txtId);
        
        fieldPane.add(new JLabel("Nombre:"));
        fieldPane.add(txtName);

        fieldPane.add(new JLabel("Precio:"));
        fieldPane.add(txtPrice);
        
        fieldPane.add(new JLabel("Stock:"));
        fieldPane.add(txtStock);
        
        selectionPane.add(new JLabel("Tipo:"));
        selectionPane.add(comboAccesoryType);
        
        selectionPane.add(connectionType);
        selectionPane.add(comboConnection);

        JButton btnRegister = new JButton("Registrar accesorio");
        btnRegister.addActionListener(e -> registerAccesory());
        registerPane.add(btnRegister, BorderLayout.CENTER);

        JPanel paneWrapper = new JPanel(new BorderLayout());
        paneWrapper.add(selectionPane, BorderLayout.NORTH);
        paneWrapper.add(fieldPane, BorderLayout.CENTER);
        paneWrapper.add(registerPane, BorderLayout.SOUTH);

        registerAccesoryModel = new DefaultTableModel(
                new String[]{"ID Accesorio", "Accesorio", "Precio", "Stock"}, 0);
        registerAccesoryTable = new JTable(registerAccesoryModel);

        panel.add(paneWrapper, BorderLayout.NORTH);
        panel.add(new JScrollPane(registerAccesoryTable), BorderLayout.CENTER);

        refreshRegisteryTable();
        return panel;
    }

    private JPanel createViewAccesoriesTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        viewAccesoryTableModel = new DefaultTableModel(
                new String[]{"ID Accesorio", "Accesorio", "Precio", "Stock"}, 0);
        viewAccesoryTable = new JTable(viewAccesoryTableModel);

        JButton btnRefresh = new JButton("Actualizar lista");
        btnRefresh.addActionListener(e -> refreshViewAccesoriesTable());

        panel.add(btnRefresh, BorderLayout.NORTH);
        panel.add(new JScrollPane(viewAccesoryTable), BorderLayout.CENTER);

        refreshViewAccesoriesTable();
        return panel;
    }
    
    private JPanel createAccesoriesByTypeTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel listPane = new JPanel(new GridLayout(2, 4, 8, 8));

        listPane.setBorder(BorderFactory.createTitledBorder("Consulta por Tipo de Accesorio"));

        txtMonth = new JTextField();
        txtYear = new JTextField();

        listPane.add(new JLabel("Tipo de Accesorio:"));
        listPane.add(comboAccesorySelect);

        JButton btnSearch = new JButton("Buscar");
        btnSearch.addActionListener(e -> refreshAccesoriesByTypeTable());

        JPanel listWrapper = new JPanel(new BorderLayout());
        listWrapper.add(listPane, BorderLayout.CENTER);
        listWrapper.add(btnSearch, BorderLayout.SOUTH);

        AccesoriesByTypeTableModel = new DefaultTableModel(
                new String[]{"ID Accesorio", "Accesorio", "Precio", "Stock"}, 0);
        AccesoriesByTypeTable = new JTable(AccesoriesByTypeTableModel);

        panel.add(listWrapper, BorderLayout.NORTH);
        panel.add(new JScrollPane(AccesoriesByTypeTable), BorderLayout.CENTER);
        
        refreshAccesoriesByTypeTable();
        return panel;
    }
    
    private void searchCompatibleConsole() {
        // implementar cuando se complete ReturnService y ReturnPersistence
    }
    
    private void searchId() {
        // implementar cuando se complete ReturnService y ReturnPersistence
    }

    private void registerAccesory() {
//        try {
//            String email = txtReason.getText();
//            String name = txtName.getText();
//            long id = Long.parseLong(txtId.getText());
//            long contact = Long.parseLong(txtValue.getText());
//
//            customerService.registerCustomer(email, name, id, contact);
//            JOptionPane.showMessageDialog(this, "Cliente registrado con éxito.");
//            refreshRegisteryTable();
//        } catch (NumberFormatException e) {
//            JOptionPane.showMessageDialog(this, "ID y contacto deben ser números válidos.");
//        } catch (IllegalArgumentException e) {
//            JOptionPane.showMessageDialog(this, e.getMessage());
//        }
    }

    private void refreshRegisteryTable() {
//        registerAccesoryModel.setRowCount(0);
//        ArrayList<Customer> customers = customerService.listCustomers();
//        for (Customer c : customers) {
//            registerAccesoryModel.addRow(new Object[]{
//                    c.getiD(), c.getName(), c.geteMail(), c.getContactNumber()
//            });
//        }
    }

    private void refreshViewAccesoriesTable() {
//        viewAccesoryTableModel.setRowCount(0);
//        ArrayList<Seller> sellers = sellerService.listSellers();
//        for (Seller s : sellers) {
//            viewAccesoryTableModel.addRow(new Object[]{
//                    s.getEmployeeCode(), s.getName(), s.getShift(), s.getiD(), s.getContactNumber()
//            });
//        }
    }
    
    
    private void showReportTable() {
        // Desarrollar cuando se complete ReturnService y RreturnPersistence
    }
        
    private void refreshAccesoriesByTypeTable() {
        // Desarrollar cuando se complete ReturnService y RreturnPersistence
    }
}