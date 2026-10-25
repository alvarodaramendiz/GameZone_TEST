package com.gamezone.ui;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.PersistenceException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;
import com.gamezone.service.ProductService;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Panel for registering and listing products (video games and consoles).
 *
 * @author jahdiel
 */
public class ProductPanel extends JPanel {

    private static final String TYPE_VIDEOGAME = "VideoGame";
    private static final String TYPE_CONSOLE = "Console";

    private final ProductService productService;

    private JTextField txtId, txtTitle, txtPrice, txtStock, txtAttr1, txtAttr2, txtAttr3;
    private JLabel lblAttr1, lblAttr2, lblAttr3;
    private JComboBox<String> comboType;
    private JTable table;
    private DefaultTableModel tableModel;

    public ProductPanel(ProductService productService) {
        this.productService = productService;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);

        updateAttributeLabels();
        refreshTable();
    }

    private JPanel createFormPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(10, 10));
        wrapper.setBorder(BorderFactory.createTitledBorder("Registrar producto"));

        comboType = new JComboBox<>(new String[]{TYPE_VIDEOGAME, TYPE_CONSOLE});
        comboType.addActionListener(e -> updateAttributeLabels());

        txtId = new JTextField();
        txtTitle = new JTextField();
        txtPrice = new JTextField();
        txtStock = new JTextField();
        txtAttr1 = new JTextField();
        txtAttr2 = new JTextField();
        txtAttr3 = new JTextField();

        lblAttr1 = new JLabel();
        lblAttr2 = new JLabel();
        lblAttr3 = new JLabel();

        // Common fields: type, id, title, price, stock
        JPanel commonFields = new JPanel(new GridLayout(3, 4, 8, 8));
        commonFields.add(new JLabel("Tipo:"));
        commonFields.add(comboType);
        commonFields.add(new JLabel("ID:"));
        commonFields.add(txtId);
        commonFields.add(new JLabel("Título:"));
        commonFields.add(txtTitle);
        commonFields.add(new JLabel("Precio:"));
        commonFields.add(txtPrice);
        commonFields.add(new JLabel("Stock:"));
        commonFields.add(txtStock);
        commonFields.add(new JLabel());
        commonFields.add(new JLabel());

        // Type-specific fields (labels change with the selected type)
        JPanel specificFields = new JPanel(new GridLayout(1, 6, 8, 8));
        specificFields.add(lblAttr1);
        specificFields.add(txtAttr1);
        specificFields.add(lblAttr2);
        specificFields.add(txtAttr2);
        specificFields.add(lblAttr3);
        specificFields.add(txtAttr3);

        JPanel fields = new JPanel(new BorderLayout(0, 8));
        fields.add(commonFields, BorderLayout.NORTH);
        fields.add(specificFields, BorderLayout.SOUTH);

        JButton btnRegister = new JButton("Registrar");
        btnRegister.addActionListener(e -> registerProduct());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(btnRegister);

        wrapper.add(fields, BorderLayout.CENTER);
        wrapper.add(buttons, BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Inventario de productos"));

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Título", "Precio", "Stock", "Descripción"}, 0);
        table = new JTable(tableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    /** Shows the attribute names that match the selected product type. */
    private void updateAttributeLabels() {
        if (TYPE_VIDEOGAME.equals(comboType.getSelectedItem())) {
            lblAttr1.setText("Plataforma:");
            lblAttr2.setText("Género:");
            lblAttr3.setText("Clasificación:");
        } else {
            lblAttr1.setText("Marca:");
            lblAttr2.setText("Modelo:");
            lblAttr3.setText("Generación:");
        }
    }

    private void registerProduct() {
        try {
            String id = txtId.getText();
            String title = txtTitle.getText();
            double price = Double.parseDouble(txtPrice.getText());
            int stock = Integer.parseInt(txtStock.getText());
            String result;

            if (TYPE_VIDEOGAME.equals(comboType.getSelectedItem())) {
                VideoGame vg = new VideoGame(txtAttr1.getText(), txtAttr2.getText(),
                        txtAttr3.getText(), id, title, price, stock);
                result = productService.registerVideoGame(vg);
            } else {
                Console console = new Console(txtAttr1.getText(), txtAttr2.getText(),
                        txtAttr3.getText(), id, title, price, stock);
                result = productService.registerConsole(console);
            }

            JOptionPane.showMessageDialog(this, result);
            refreshTable();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Precio y stock deben ser números válidos.");
        } catch (ResourceNotFoundException e) {
            JOptionPane.showMessageDialog(this, "No se encontró el recurso solicitado: " + e.getMessage());
        } catch (BusinessRuleException e) {
            JOptionPane.showMessageDialog(this, "No se puede completar la operación: " + e.getMessage());
        } catch (InvalidDataException e) {
            JOptionPane.showMessageDialog(this, "Los datos ingresados no son válidos: " + e.getMessage());
        } catch (PersistenceException e) {
            JOptionPane.showMessageDialog(this, "Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        try {
            List<Product> products = productService.listProducts();
            for (Product p : products) {
                tableModel.addRow(new Object[]{
                        p.getProductId(), p.getTitle(), p.getPrice(), p.getStock(), p.getDescription()
                });
            }
        } catch (PersistenceException e) {
            JOptionPane.showMessageDialog(this, "Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        }
    }
}