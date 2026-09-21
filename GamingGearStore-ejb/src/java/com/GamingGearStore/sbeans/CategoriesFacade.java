/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.GamingGearStore.sbeans;

import com.GamingGearStore.ebeans.Categories;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

/**
 *
 * @author ASUS
 */
@Stateless
public class CategoriesFacade extends AbstractFacade<Categories> implements CategoriesFacadeLocal {

    @PersistenceContext(unitName = "GamingGearStore-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CategoriesFacade() {
        super(Categories.class);
    }

    @Override
    public List<Categories> searchByName(String keyword) {
        String pattern = "%" + (keyword != null ? keyword.trim().toLowerCase() : "") + "%";
        return em.createQuery("SELECT c FROM Categories c WHERE LOWER(c.categoryName) LIKE :kw ORDER BY c.categoryID ASC", Categories.class)
                .setParameter("kw", pattern)
                .getResultList();
    }
}
