package com.gamezone.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import com.gamezone.model.Customer;
import com.gamezone.exceptions.PersistenceException;
import utilities.PlainArchives;

/**
 * Data Access Object (DAO) responsible for the persistence of Customer objects.
 * It provides methods to search, save a single customer,
 * all of the customers, update, delete and list customers using the project's
 * persistence storage.
 *
 * @author EstefaniaMarquez
 */
public class CustomerPersistence {

    /**
     * Saves a single Customer record by appending it to the persistence file.
     * The record is stored in the format: ID|name|email|contactNumber.
     *
     * @param customer the Customer record to be saved.
     */
    public void saveCustomer(Customer customer) {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(PlainArchives.CUSTOMERS, true))) {
            bw.write(customer.toText());
            bw.newLine();
        } catch (IOException e) {
            throw new PersistenceException("Error al escribir en el archivo " + PlainArchives.CUSTOMERS, e);
        }
    }

    /**
     * Saves all Customer records by overwriting the persistence file with the
     * provided list of customers.
     *
     * @param customers the list of Customer records to be saved.
     */
    private void saveCustomers(ArrayList<Customer> customers) {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(PlainArchives.CUSTOMERS))) {
            for (Customer currentC : customers) {
                bw.write(currentC.toText());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new PersistenceException("Error al escribir en el archivo " + PlainArchives.CUSTOMERS, e);
        }
    }

    /**
     * Retrieves all Customer records stored in the persistence file. Each
     * record is read and converted into a Customer object.
     *
     * @return an ArrayList containing all stored Customer records.
     */
    public ArrayList<Customer> listCustomers() {
        ArrayList<Customer> customers = new ArrayList<>();
        if (!new File(PlainArchives.CUSTOMERS).exists()) {
            return customers;
        }
        try (BufferedReader br = new BufferedReader(
                new FileReader(PlainArchives.CUSTOMERS))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] infoCustomer = line.split("\\|");
                long contactNumber, iD;
                contactNumber = Long.parseLong(infoCustomer[3]);
                iD = Long.parseLong(infoCustomer[0]);
                customers.add(new Customer(infoCustomer[2], infoCustomer[1], iD, contactNumber));
            }
        } catch (IOException e) {
            throw new PersistenceException("Error al leer el archivo " + PlainArchives.CUSTOMERS, e);
        }
        return customers;
    }

    /**
     * Updates an existing Customer record by modifying its information and
     * saving the updated list of customers to the persistence file.
     *
     * @param customer the Customer record containing the updated information.
     */
    public void updateCustomer(Customer customer) {
        ArrayList<Customer> customers = listCustomers();
        for (Customer currentC : customers) {
            if (currentC.getiD() == customer.getiD()) {
                currentC.seteMail(customer.geteMail());
                currentC.setContactNumber(customer.getContactNumber());
                currentC.setName(customer.getName());
                break;
            }
        }
        saveCustomers(customers);
    }

    /**
     * Deletes a Customer record from the persistence file using its
     * identification number.
     *
     * @param iD the identification number of the Customer to be deleted.
     */
    public void deleteCustomer(long iD) {
        ArrayList<Customer> customers = listCustomers();
        Customer toDelete = null;
        for (Customer currentC : customers) {
            if (currentC.getiD() == iD) {
                toDelete = currentC;
                break;
            }
        }
        customers.remove(toDelete);
        saveCustomers(customers);
    }

    /**
     * Searches for a Customer by its ID among the stored Customer records.
     *
     * @param id the ID of the Customer to search for.
     * @return the Customer with the specified ID, or {@code null} if no
     * matching Customer is found.
     */
    public Customer searchCustomer(long id) {
        ArrayList<Customer> customers = listCustomers();
        for (Customer currentC : customers) {
            if (id == currentC.getiD()) {
                return currentC;
            }
        }
        return null;
    }

}
