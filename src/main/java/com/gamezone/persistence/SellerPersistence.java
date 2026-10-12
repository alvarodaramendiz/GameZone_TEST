package com.gamezone.persistence;

import com.gamezone.model.Seller;
import com.gamezone.model.Shift;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import utilities.PlainArchives;

/**
  * Data Access Object (DAO) responsible for the persistence of Seller objects.
  * It provides methods to search, save, and list sellers using the project's
  * persistence storage.
  *
  * @author EstefaniaMarquez
  */
public class SellerPersistence {

    /**
     *
     * Saves a single Seller record by appending it to the persistence file.
     * The record is stored in the format: ID|employeeCode|name|shift|contactNumber.
     *
     * @param seller the seller to be saved.
     */
    
    public void saveSeller(Seller seller){
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(PlainArchives.SELLERS, true))) {
            bw.write(seller.toText());
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error: Failed to save the seller record.");
        }
    }
    
    /** 
     * Saves all Seller records by overwriting the persistence file with 
     * the provided list of sellers. 
     * 
     * @param sellers the list of Seller records to be saved. 
     */
    
    private void saveSellers(ArrayList<Seller> sellers){
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(PlainArchives.SELLERS))) {
            for (Seller currentS: sellers){
                bw.write(currentS.toText());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error: Failed to save the sellers records.");
        }
    }
    
    /**
     * Retrieves all Seller records stored in the persistence file. Each
     * record is read and converted into a Seller object.
     *
     * @return an ArrayList containing all stored Seller records.
     */
    
    public ArrayList<Seller> listSellers(){
        ArrayList<Seller> sellers = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new FileReader(PlainArchives.SELLERS))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] infoSeller = line.split("\\|");
                long contactNumber, iD, employeeCode;
                contactNumber = Long.parseLong(infoSeller[4]);
                iD = Long.parseLong(infoSeller[0]);
                employeeCode = Long.parseLong(infoSeller[1]);
                Shift shift = Shift.valueOf(infoSeller[3]);
                sellers.add(new Seller(employeeCode, shift, infoSeller[2], iD, contactNumber));
            }
        } catch (IOException e) {
            System.out.println("Error: Failed to list the sellers record.");
        }
        return sellers;
    }
    
    /** 
     * Updates an existing Seller record by modifying its information and * saving the updated list of sellers to the persistence file. 
     * 
     * @param seller the Seller record containing the updated information. 
     */
    
    public void updateSeller(Seller seller){
        ArrayList<Seller> sellers = listSellers();
        for (Seller currentS : sellers) {
            if (currentS.getEmployeeCode() == seller.getEmployeeCode()) {
                currentS.setiD(seller.getiD());
                currentS.setContactNumber(seller.getContactNumber());
                currentS.setName(seller.getName());
                currentS.setShift(seller.getShift());
                break;
            }
        }
        saveSellers(sellers);
    }
    
    /** 
     * Deletes a Seller record from the persistence file using its 
     * employee code. 
     * 
     * @param employeeCode the employee code of the Seller to be deleted. 
     */
    
    public void deleteSeller(long employeeCode){
        ArrayList<Seller> sellers = listSellers();
        Seller toDelete = null;
        for (Seller currentS : sellers) {
            if (currentS.getEmployeeCode() == employeeCode){
                toDelete = currentS;
            }
        }
        sellers.remove(toDelete);
        saveSellers(sellers);
    }
    
    /**
     * Searches for a Seller by its Employee Code among the stored Seller records.
     *
     * @param employeeCode the employee code of the Seller to search for.
     * @return the Seller with the specified Employee Code, or {@code null} if no
     * matching Seller is found.
     */
    
    public Seller searchSeller(long employeeCode){
        ArrayList<Seller> sellers = listSellers();
        for (Seller currentS : sellers) {
            if (employeeCode == currentS.getEmployeeCode()) {
                return currentS;
            }
        }
        return null;
    }
}
