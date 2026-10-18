package com.gamezone.ui;

import com.gamezone.model.Accessory;
import com.gamezone.service.AccessoryService;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Panel for registering accessories and querying them by type or by compatible console.
 *
 * @author jahdiel
 */
public class AccessoryPanel extends JPanel {

    // UI labels (Spanish, shown to the user)
    private static final String UI_CABLE = "Cable";
    private static final String UI_CONTROLLER = "Mando";
    private static final String UI_MEMORY = "Memoria";

    // UI labels double as the values stored by the model ("Alámbrico" / "Inalámbrico")
    private static final String UI_WIRED = "Alámbrico";
    private static final String UI_WIRELESS = "Inalámbrico";

    // Values expected by AccessoryService.listAccessoriesByType
    private static final String TYPE_CABLE = "CABLE";
    private static final String TYPE_CONTROLLER = "CONTROLLER";
    private static final String TYPE_MEMORY = "MEMORY";

    private static final String[] TABLE_COLUMNS = {
        "ID Accesorio", "Accesorio", "Precio", "Stock", "Descripción"
    };

    private final AccessoryService accessoryService;

    private JTextField txtPrice, txtName, txtId, txtStock;
    private JTextField txtLength, txtCapacity, txtCompatible, txtConsoleId;
    private JComboBox<String> comboAccesoryType, comboConnection, comboAccesorySelect;
    private JComboBox<String> comboConnector, comboMemoryType;
    private JLabel lblConnection, lblLength, lblConnector, lblCapacity, lblMemoryType, lblCompatible;
    private DefaultTableModel registerAccesoryModel, viewAccesoryTableModel,
            accesoriesByTypeTableModel, compatibleTableModel;

    /**
     * Creates the accessories panel.
     *
     * @param accessoryService service used to register and query accessories
     */
    public AccessoryPanel(AccessoryService accessoryService) {
        this.accessoryService = accessoryService;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Registro de Accesorios", registerAccesoryTab());
        tabs.addTab("Lista de Accesorios", createViewAccesoriesTab());
        tabs.addTab("Consulta por Accesorio", createAccesoriesByTypeTab());
        tabs.addTab("Compatibilidad", createCompatibilityTab());
        add(tabs, BorderLayout.CENTER);

        refreshAllTables();
    }

    private JPanel registerAccesoryTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        txtId = new JTextField();
        txtName = new JTextField();
        txtPrice = new JTextField();
        txtStock = new JTextField();
        txtLength = new JTextField();
        txtCapacity = new JTextField();
        txtCompatible = new JTextField();

        comboAccesoryType = new JComboBox<>(new String[]{UI_CABLE, UI_CONTROLLER, UI_MEMORY});
        comboConnection = new JComboBox<>(new String[]{UI_WIRED, UI_WIRELESS});
        comboConnector = new JComboBox<>(new String[]{"HDMI", "USB", "Óptico"});
        comboConnector.setEditable(true);
        comboMemoryType = new JComboBox<>(new String[]{"SD", "microSD", "Tarjeta interna"});

        lblConnection = new JLabel("Conexión:");
        lblLength = new JLabel("Longitud (m):");
        lblConnector = new JLabel("Conector:");
        lblCapacity = new JLabel("Capacidad (GB):");
        lblMemoryType = new JLabel("Tipo de memoria:");
        lblCompatible = new JLabel("Consolas compatibles (IDs separados por coma):");

        comboAccesoryType.addActionListener(e -> updateTypeSpecificFields());

        JPanel selectionPane = new JPanel(new GridLayout(0, 4, 5, 5));
        selectionPane.setBorder(BorderFactory.createTitledBorder("Selección de Accesorios"));
        selectionPane.add(new JLabel("Tipo:"));
        selectionPane.add(comboAccesoryType);
        selectionPane.add(lblConnection);
        selectionPane.add(comboConnection);
        selectionPane.add(lblLength);
        selectionPane.add(txtLength);
        selectionPane.add(lblConnector);
        selectionPane.add(comboConnector);
        selectionPane.add(lblCapacity);
        selectionPane.add(txtCapacity);
        selectionPane.add(lblMemoryType);
        selectionPane.add(comboMemoryType);
        selectionPane.add(lblCompatible);
        selectionPane.add(txtCompatible);

        JPanel fieldPane = new JPanel(new GridLayout(2, 4, 5, 5));
        fieldPane.setBorder(BorderFactory.createTitledBorder("Registro de Datos"));
        fieldPane.add(new JLabel("ID del Accesorio:"));
        fieldPane.add(txtId);
        fieldPane.add(new JLabel("Nombre:"));
        fieldPane.add(txtName);
        fieldPane.add(new JLabel("Precio:"));
        fieldPane.add(txtPrice);
        fieldPane.add(new JLabel("Stock:"));
        fieldPane.add(txtStock);

        JButton btnRegister = new JButton("Registrar accesorio");
        btnRegister.addActionListener(e -> registerAccesory());

        JPanel paneWrapper = new JPanel(new BorderLayout());
        paneWrapper.add(selectionPane, BorderLayout.NORTH);
        paneWrapper.add(fieldPane, BorderLayout.CENTER);
        paneWrapper.add(btnRegister, BorderLayout.SOUTH);

        registerAccesoryModel = createTableModel();
        JTable registerAccesoryTable = new JTable(registerAccesoryModel);

        panel.add(paneWrapper, BorderLayout.NORTH);
        panel.add(new JScrollPane(registerAccesoryTable), BorderLayout.CENTER);

        updateTypeSpecificFields();
        return panel;
    }

    private JPanel createViewAccesoriesTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        viewAccesoryTableModel = createTableModel();
        JTable viewAccesoryTable = new JTable(viewAccesoryTableModel);

        JButton btnRefresh = new JButton("Actualizar lista");
        btnRefresh.addActionListener(e -> refreshViewAccesoriesTable());

        panel.add(btnRefresh, BorderLayout.NORTH);
        panel.add(new JScrollPane(viewAccesoryTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAccesoriesByTypeTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        comboAccesorySelect = new JComboBox<>(new String[]{UI_CABLE, UI_CONTROLLER, UI_MEMORY});

        JPanel listPane = new JPanel(new GridLayout(1, 2, 8, 8));
        listPane.setBorder(BorderFactory.createTitledBorder("Consulta por Tipo de Accesorio"));
        listPane.add(new JLabel("Tipo de Accesorio:"));
        listPane.add(comboAccesorySelect);

        JButton btnSearch = new JButton("Buscar");
        btnSearch.addActionListener(e -> refreshAccesoriesByTypeTable());

        JPanel listWrapper = new JPanel(new BorderLayout());
        listWrapper.add(listPane, BorderLayout.CENTER);
        listWrapper.add(btnSearch, BorderLayout.SOUTH);

        accesoriesByTypeTableModel = createTableModel();
        JTable accesoriesByTypeTable = new JTable(accesoriesByTypeTableModel);

        panel.add(listWrapper, BorderLayout.NORTH);
        panel.add(new JScrollPane(accesoriesByTypeTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCompatibilityTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        txtConsoleId = new JTextField();

        JPanel searchPane = new JPanel(new GridLayout(1, 2, 8, 8));
        searchPane.setBorder(BorderFactory.createTitledBorder("Accesorios compatibles con una consola"));
        searchPane.add(new JLabel("ID de la consola:"));
        searchPane.add(txtConsoleId);

        JButton btnSearchCompatible = new JButton("Consultar compatibles");
        btnSearchCompatible.addActionListener(e -> searchCompatibleConsole());

        JPanel searchWrapper = new JPanel(new BorderLayout());
        searchWrapper.add(searchPane, BorderLayout.CENTER);
        searchWrapper.add(btnSearchCompatible, BorderLayout.SOUTH);

        compatibleTableModel = createTableModel();
        JTable compatibleTable = new JTable(compatibleTableModel);

        panel.add(searchWrapper, BorderLayout.NORTH);
        panel.add(new JScrollPane(compatibleTable), BorderLayout.CENTER);
        return panel;
    }

    /** Shows only the inputs that apply to the selected accessory type. */
    private void updateTypeSpecificFields() {
        String type = String.valueOf(comboAccesoryType.getSelectedItem());
        boolean isController = type.equals(UI_CONTROLLER);
        boolean isCable = type.equals(UI_CABLE);
        boolean isMemory = type.equals(UI_MEMORY);

        lblConnection.setVisible(isController);
        comboConnection.setVisible(isController);

        lblLength.setVisible(isCable);
        txtLength.setVisible(isCable);
        lblConnector.setVisible(isCable);
        comboConnector.setVisible(isCable);

        lblCapacity.setVisible(isMemory);
        txtCapacity.setVisible(isMemory);
        lblMemoryType.setVisible(isMemory);
        comboMemoryType.setVisible(isMemory);

        // Compatibility applies to controllers and memories
        lblCompatible.setVisible(isController || isMemory);
        txtCompatible.setVisible(isController || isMemory);
    }

    private void registerAccesory() {
        try {
            String id = txtId.getText().trim();
            String title = txtName.getText().trim();
            double price = Double.parseDouble(txtPrice.getText().trim().replace(',', '.'));
            int stock = Integer.parseInt(txtStock.getText().trim());
            String type = String.valueOf(comboAccesoryType.getSelectedItem());
            String connector = String.valueOf(comboConnector.getSelectedItem()).trim();

            // The CSV uses '|' and ';' as separators, so they cannot appear in the data.
            // IDs are also written to the sales file, which uses ':' as an extra separator.
            if (id.contains(":") || containsReservedCharacters(id, title, connector,
                    txtCompatible.getText().replace(",", ""))) {
                showMessage("Los campos no pueden contener '|' ni ';' (el ID tampoco ':').");
                return;
            }

            switch (type) {
                case UI_CONTROLLER -> accessoryService.registerController(id, title, price, stock,
                        String.valueOf(comboConnection.getSelectedItem()),
                        parseConsoleIds(txtCompatible.getText()));
                case UI_CABLE -> {
                    double length = Double.parseDouble(txtLength.getText().trim().replace(',', '.'));
                    accessoryService.registerCable(id, title, price, stock, length, connector,
                            new ArrayList<>());
                }
                case UI_MEMORY -> {
                    int capacity = Integer.parseInt(txtCapacity.getText().trim());
                    accessoryService.registerMemory(id, title, price, stock, capacity,
                            String.valueOf(comboMemoryType.getSelectedItem()),
                            parseConsoleIds(txtCompatible.getText()));
                }
                default -> {
                    showMessage("Tipo de accesorio no válido.");
                    return;
                }
            }

            showMessage("Accesorio registrado con éxito.");
            clearForm();
            refreshAllTables();
        } catch (NumberFormatException e) {
            showMessage("Precio, stock, longitud y capacidad deben ser números válidos.");
        } catch (IllegalArgumentException e) {
            // Business messages come from AccessoryService (already in Spanish)
            showMessage(e.getMessage());
        }
    }

    private boolean containsReservedCharacters(String... values) {
        for (String value : values) {
            if (value.contains("|") || value.contains(";")) {
                return true;
            }
        }
        return false;
    }

    private void searchCompatibleConsole() {
        String consoleId = txtConsoleId.getText().trim();
        if (consoleId.isEmpty()) {
            showMessage("Ingrese el ID de la consola.");
            return;
        }
        List<Accessory> compatible = accessoryService.findAccessoriesCompatibleWith(consoleId);
        fillTable(compatibleTableModel, compatible);
        if (compatible.isEmpty()) {
            showMessage("No hay accesorios compatibles con la consola indicada.");
        }
    }

    private void refreshRegisteryTable() {
        fillTable(registerAccesoryModel, accessoryService.listAllAccessories());
    }

    private void refreshViewAccesoriesTable() {
        fillTable(viewAccesoryTableModel, accessoryService.listAllAccessories());
    }

    private void refreshAccesoriesByTypeTable() {
        String selected = String.valueOf(comboAccesorySelect.getSelectedItem());
        fillTable(accesoriesByTypeTableModel,
                accessoryService.listAccessoriesByType(toServiceType(selected)));
    }

    private void refreshAllTables() {
        refreshRegisteryTable();
        refreshViewAccesoriesTable();
        refreshAccesoriesByTypeTable();
    }

    private void fillTable(DefaultTableModel model, List<Accessory> accessories) {
        model.setRowCount(0);
        for (Accessory a : accessories) {
            model.addRow(new Object[]{
                a.getProductId(), a.getTitle(), a.getPrice(), a.getStock(), a.getDescription()
            });
        }
    }

    private String toServiceType(String uiType) {
        return switch (uiType) {
            case UI_CONTROLLER -> TYPE_CONTROLLER;
            case UI_MEMORY -> TYPE_MEMORY;
            default -> TYPE_CABLE;
        };
    }

    private List<String> parseConsoleIds(String text) {
        List<String> ids = new ArrayList<>();
        for (String part : text.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                ids.add(trimmed);
            }
        }
        return ids;
    }

    private DefaultTableModel createTableModel() {
        return new DefaultTableModel(TABLE_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtPrice.setText("");
        txtStock.setText("");
        txtLength.setText("");
        txtCapacity.setText("");
        txtCompatible.setText("");
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}