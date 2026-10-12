package com.gamezone.persistence;

import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;
import com.gamezone.model.Console;
import utilities.PlainArchives;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the persistence of Product objects (video games and consoles)
 * in separate text files.
 *
 * File format for videogames.txt:
 * VIDEOGAME|productId|title|price|stock|platform|genre|ageRating
 *
 * File format for consoles.txt:
 * CONSOLE|productId|title|price|stock|brand|model|generation
 *
 * @author jahdiel
 */
public class ProductPersistence {

    /**
     * Saves a product to its corresponding file (videogames.txt or consoles.txt),
     * appending it as a new line
     *
     * @param product the product to save
     */
    public void saveProduct(Product product) {
        String file = (product instanceof VideoGame) ? PlainArchives.VIDEOGAMES : PlainArchives.CONSOLES;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(product.toText());
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error: Failed to save the product record.");
        }
    }

    /**
     * Returns the list of all products, combining video games and consoles.
     *
     * @return an ArrayList with every stored product
     */
    public List<Product> listProducts() {
        List<Product> products = new ArrayList<>();
        products.addAll(loadVideoGames());
        products.addAll(loadConsoles());
        return products;
    }

    /**
     * Searches for a product by its ID among all stored products.
     *
     * @param productId the ID of the product to search for
     * @return the matching Product, or null if none is found
     */
    public Product searchProduct(String productId) {
        for (Product p : listProducts()) {
            if (productId.equals(p.getProductId())) {
                return p;
            }
        }
        return null;
    }

    /**
     * Checks whether a product with the given ID already exists.
     *
     * @param productId the ID to check
     * @return true if a product with that ID exists, false otherwise
     */
    public boolean existsProduct(String productId) {
        return searchProduct(productId) != null;
    }

    /**
     * Updates a stored product, matching it by ID, and next rewrites the
     * product files so the change is persisted.
     *
     * @param product the product with the updated information
     */
    public void updateProduct(Product product) {
        List<Product> products = listProducts();
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getProductId().equals(product.getProductId())) {
                products.set(i, product);
                break;
            }
        }
        try (BufferedWriter vg = new BufferedWriter(new FileWriter(PlainArchives.VIDEOGAMES));
             BufferedWriter co = new BufferedWriter(new FileWriter(PlainArchives.CONSOLES))) {
            for (Product p : products) {
                BufferedWriter bw = (p instanceof VideoGame) ? vg : co;
                bw.write(p.toText());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error: Failed to update the product record.");
        }
    }

    /**
     * Loads all video games stored in videogames.txt.
     *
     * @return a list of VideoGame objects
     */
    private List<Product> loadVideoGames() {
        List<Product> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(PlainArchives.VIDEOGAMES))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] i = line.split("\\|");
                String productId = i[1]; // i[0] is the videogame tag
                String title = i[2];
                double price = Double.parseDouble(i[3]);
                int stock = Integer.parseInt(i[4]);
                String platform = i[5];
                String genre = i[6];
                String ageRating = i[7];
                list.add(new VideoGame(platform, genre, ageRating, productId, title, price, stock));
            }
        } catch (IOException e) {
            System.out.println("Error: Failed to list video games.");
        }
        return list;
    }

    /**
     * Loads all consoles stored in consoles.txt.
     *
     * @return a list of Console objects
     */
    private List<Product> loadConsoles() {
        List<Product> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(PlainArchives.CONSOLES))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] i = line.split("\\|");
                String productId = i[1]; // i[0] is the console tag
                String title = i[2];
                double price = Double.parseDouble(i[3]);
                int stock = Integer.parseInt(i[4]);
                String brand = i[5];
                String model = i[6];
                String generation = i[7];
                list.add(new Console(brand, model, generation, productId, title, price, stock));
            }
        } catch (IOException e) {
            System.out.println("Error: Failed to list consoles.");
        }
        return list;
    }
}
