/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.GamingGearStore.sbeans;

import com.GamingGearStore.ebeans.Categories;
import jakarta.ejb.Local;
import java.util.List;

/**
 *
 * @author ASUS
 */
@Local
public interface CategoriesFacadeLocal {

    void create(Categories categories);

    void edit(Categories categories);

    void remove(Categories categories);

    Categories find(Object id);

    List<Categories> findAll();

    List<Categories> findRange(int[] range);

    int count();

    List<Categories> searchByName(String keyword);
}
