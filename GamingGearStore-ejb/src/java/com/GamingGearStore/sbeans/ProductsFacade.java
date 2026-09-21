/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.GamingGearStore.sbeans;

import com.GamingGearStore.ebeans.Products;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

/**
 *
 * @author ASUS
 */
@Stateless
public class ProductsFacade extends AbstractFacade<Products> implements ProductsFacadeLocal {

    @PersistenceContext(unitName = "GamingGearStore-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProductsFacade() {
        super(Products.class);
    }

    @Override
    public List<Products> findByCategory(int categoryId) {
        return em.createQuery("SELECT p FROM Products p WHERE p.categoryID.categoryID = :catId ORDER BY p.productID DESC", Products.class)
                .setParameter("catId", categoryId)
                .getResultList();
    }

    @Override
    public List<Products> searchByName(String keyword) {
        String pattern = "%" + (keyword != null ? keyword.trim().toLowerCase() : "") + "%";
        return em.createQuery("SELECT p FROM Products p WHERE LOWER(p.productName) LIKE :kw OR LOWER(p.brand) LIKE :kw ORDER BY p.productID DESC", Products.class)
                .setParameter("kw", pattern)
                .getResultList();
    }

    @Override
    public List<Products> findByBrand(String brand) {
        return em.createQuery("SELECT p FROM Products p WHERE LOWER(p.brand) = :brand ORDER BY p.productID DESC", Products.class)
                .setParameter("brand", brand.toLowerCase())
                .getResultList();
    }

    @Override
    public List<Products> findFeatured(int limit) {
        return em.createQuery("SELECT p FROM Products p ORDER BY p.productID DESC", Products.class)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public boolean updateStock(int productId, int quantityToDeduct) {
        Products p = find(productId);
        if (p != null && p.getQuantity() != null && p.getQuantity() >= quantityToDeduct) {
            p.setQuantity(p.getQuantity() - quantityToDeduct);
            edit(p);
            return true;
        }
        return false;
    }
}
