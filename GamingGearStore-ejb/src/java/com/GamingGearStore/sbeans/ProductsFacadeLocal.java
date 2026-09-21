/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.GamingGearStore.sbeans;

import com.GamingGearStore.ebeans.Products;
import jakarta.ejb.Local;
import java.util.List;

/**
 *
 * @author ASUS
 */
@Local
public interface ProductsFacadeLocal {

    void create(Products products);

    void edit(Products products);

    void remove(Products products);

    Products find(Object id);

    List<Products> findAll();

    List<Products> findRange(int[] range);

    int count();

    List<Products> findByCategory(int categoryId);

    List<Products> searchByName(String keyword);

    List<Products> findByBrand(String brand);

    List<Products> findFeatured(int limit);

    boolean updateStock(int productId, int quantityToDeduct);
}
