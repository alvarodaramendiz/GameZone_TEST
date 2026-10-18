package com.gamezone.ui;

import com.gamezone.model.BulkPurchaseDiscount;
import com.gamezone.model.CategoryDiscount;
import com.gamezone.model.PercentageDiscount;
import com.gamezone.model.Promotion;
import com.gamezone.service.PromotionService;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Panel for promotions management: registration, full listing and active listing.
 *
 * @author alvaro
 */
public class PromotionPanel extends JPanel {

    private static final String TYPE_PERCENTAGE = "Porcentaje";
    private static final String TYPE_CATEGORY = "Categoría";
    private static final String TYPE_BULK = "Volumen";

    private static final String[] TABLE_COLUMNS = {
        "ID de Promoción", "Promoción", "Tipo", "Fecha inicio", "Vencimiento"
    };

    private final PromotionService promotionService;

    private JTextField txtId, txtName, txtDateStart, txtDateEnd, txtPercentage, txtMinQuantity;
    private JComboBox<String> comboPromotions, comboCategory;
    private JLabel lblCategory, lblMinQuantity;
    private DefaultTableModel registerPromotionsTableModel, viewPromotionsTableModel, availablePromotionsTableModel;

    /**
     * Creates the promotions panel.
     *
     * @param promotionService service used to register and query promotions
     */
    public PromotionPanel(PromotionService promotionService) {
        this.promotionService = promotionService;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Registro de Promociones", registerPromotionsTab());
        tabs.addTab("Lista de Promociones", viewPromotionsTab());
        tabs.addTab("Promociones Vigentes", availablePromotionsTab());
        add(tabs, BorderLayout.CENTER);

        refreshAllTables();
    }

    private JPanel registerPromotionsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        txtId = new JTextField();
        txtName = new JTextField();
        txtDateStart = new JTextField();
        txtDateEnd = new JTextField();
        txtPercentage = new JTextField();
        txtMinQuantity = new JTextField();

        comboPromotions = new JComboBox<>(new String[]{TYPE_PERCENTAGE, TYPE_CATEGORY, TYPE_BULK});
        comboCategory = new JComboBox<>(new String[]{"Consola", "Videojuego"});

        lblCategory = new JLabel("Categoría:");
        lblMinQuantity = new JLabel("Cantidad mínima de productos:");

        // Fields that only apply to a specific promotion type start hidden
        lblCategory.setVisible(false);
        comboCategory.setVisible(false);
        lblMinQuantity.setVisible(false);
        txtMinQuantity.setVisible(false);

        comboPromotions.addActionListener(e -> updateTypeSpecificFields());

        JPanel formPane = new JPanel(new GridLayout(0, 4, 8, 8));
        formPane.setBorder(BorderFactory.createTitledBorder("Registro de Promociones"));
        formPane.add(new JLabel("ID de la Promoción:"));
        formPane.add(txtId);
        formPane.add(new JLabel("Nombre:"));
        formPane.add(txtName);
        formPane.add(new JLabel("Fecha de inicio (AAAA-MM-DD):"));
        formPane.add(txtDateStart);
        formPane.add(new JLabel("Vencimiento (AAAA-MM-DD):"));
        formPane.add(txtDateEnd);
        formPane.add(new JLabel("Promociones:"));
        formPane.add(comboPromotions);
        formPane.add(new JLabel("Porcentaje de descuento (0-100):"));
        formPane.add(txtPercentage);
        formPane.add(lblCategory);
        formPane.add(comboCategory);
        formPane.add(lblMinQuantity);
        formPane.add(txtMinQuantity);

        JButton btnRegister = new JButton("Registrar promoción");
        btnRegister.addActionListener(e -> registerPromotion());
        JPanel buttonPane = new JPanel(new BorderLayout());
        buttonPane.add(btnRegister, BorderLayout.CENTER);

        registerPromotionsTableModel = createTableModel();
        JTable registerPromotionsTable = new JTable(registerPromotionsTableModel);

        panel.add(formPane, BorderLayout.NORTH);
        panel.add(new JScrollPane(registerPromotionsTable), BorderLayout.CENTER);
        panel.add(buttonPane, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel viewPromotionsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        viewPromotionsTableModel = createTableModel();
        JTable viewPromotionsTable = new JTable(viewPromotionsTableModel);

        JButton btnRefresh = new JButton("Actualizar lista");
        btnRefresh.addActionListener(e -> refreshViewTable());

        panel.add(btnRefresh, BorderLayout.NORTH);
        panel.add(new JScrollPane(viewPromotionsTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel availablePromotionsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        availablePromotionsTableModel = createTableModel();
        JTable availablePromotionsTable = new JTable(availablePromotionsTableModel);

        JButton btnUpdate = new JButton("Actualizar lista vigentes");
        btnUpdate.addActionListener(e -> refreshAvailablesTable());

        panel.add(btnUpdate, BorderLayout.NORTH);
        panel.add(new JScrollPane(availablePromotionsTable), BorderLayout.CENTER);
        return panel;
    }

    /** Shows or hides the inputs that depend on the selected promotion type. */
    private void updateTypeSpecificFields() {
        String option = String.valueOf(comboPromotions.getSelectedItem());
        boolean isCategory = option.equals(TYPE_CATEGORY);
        boolean isBulk = option.equals(TYPE_BULK);

        lblCategory.setVisible(isCategory);
        comboCategory.setVisible(isCategory);
        lblMinQuantity.setVisible(isBulk);
        txtMinQuantity.setVisible(isBulk);
    }

    private void registerPromotion() {
        try {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();

            if (id.isEmpty() || name.isEmpty()) {
                showMessage("El ID y el nombre de la promoción son obligatorios.");
                return;
            }

            LocalDate startDate = LocalDate.parse(txtDateStart.getText().trim());
            LocalDate endDate = LocalDate.parse(txtDateEnd.getText().trim());
            if (endDate.isBefore(startDate)) {
                showMessage("La fecha de vencimiento no puede ser anterior a la fecha de inicio.");
                return;
            }

            double percentage = Double.parseDouble(txtPercentage.getText().trim().replace(',', '.'));
            if (percentage < 0 || percentage > 100) {
                showMessage("El porcentaje debe estar entre 0 y 100.");
                return;
            }

            String type = String.valueOf(comboPromotions.getSelectedItem());
            switch (type) {
                case TYPE_PERCENTAGE ->
                    promotionService.registerPercentageDiscount(id, name, startDate, endDate, percentage);
                case TYPE_CATEGORY -> {
                    String targetCategory = comboCategory.getSelectedItem().equals("Consola")
                            ? "CONSOLE" : "VIDEOGAME";
                    promotionService.registerCategoryDiscount(id, name, startDate, endDate, percentage, targetCategory);
                }
                case TYPE_BULK -> {
                    int minQuantity = Integer.parseInt(txtMinQuantity.getText().trim());
                    if (minQuantity <= 0) {
                        showMessage("La cantidad mínima de productos debe ser mayor que cero.");
                        return;
                    }
                    promotionService.registerBulkPurchaseDiscount(id, name, startDate, endDate, minQuantity, percentage);
                }
                default -> {
                    showMessage("Tipo de promoción no válido.");
                    return;
                }
            }

            showMessage("Promoción registrada con éxito.");
            clearForm();
            refreshAllTables();
        } catch (DateTimeParseException e) {
            showMessage("Las fechas deben tener el formato AAAA-MM-DD (por ejemplo, 2026-09-08).");
        } catch (NumberFormatException e) {
            showMessage("El porcentaje y la cantidad mínima deben ser números válidos.");
        } catch (IllegalArgumentException e) {
            showMessage(e.getMessage());
        }
    }

    private void refreshPromotionsTable() {
        fillTable(registerPromotionsTableModel, promotionService.listAllPromotions());
    }

    private void refreshViewTable() {
        fillTable(viewPromotionsTableModel, promotionService.listAllPromotions());
    }

    private void refreshAvailablesTable() {
        fillTable(availablePromotionsTableModel, promotionService.listActivePromotions());
    }

    private void refreshAllTables() {
        refreshPromotionsTable();
        refreshViewTable();
        refreshAvailablesTable();
    }

    private void fillTable(DefaultTableModel model, List<Promotion> promotions) {
        model.setRowCount(0);
        for (Promotion p : promotions) {
            model.addRow(new Object[]{
                p.getPromotionId(), p.getName(), typeLabel(p), p.getStartDate(), p.getEndDate()
            });
        }
    }

    private String typeLabel(Promotion promotion) {
        if (promotion instanceof PercentageDiscount) {
            return TYPE_PERCENTAGE;
        } else if (promotion instanceof CategoryDiscount) {
            return TYPE_CATEGORY;
        } else if (promotion instanceof BulkPurchaseDiscount) {
            return TYPE_BULK;
        }
        return "Desconocido";
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
        txtDateStart.setText("");
        txtDateEnd.setText("");
        txtPercentage.setText("");
        txtMinQuantity.setText("");
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}