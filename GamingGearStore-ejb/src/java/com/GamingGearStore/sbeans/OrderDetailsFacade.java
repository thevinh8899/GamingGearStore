/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.GamingGearStore.sbeans;

import com.GamingGearStore.ebeans.OrderDetails;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

/**
 *
 * @author ASUS
 */
@Stateless
public class OrderDetailsFacade extends AbstractFacade<OrderDetails> implements OrderDetailsFacadeLocal {

    @PersistenceContext(unitName = "GamingGearStore-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OrderDetailsFacade() {
        super(OrderDetails.class);
    }

    @Override
    public List<OrderDetails> findByOrderId(int orderId) {
        return em.createQuery("SELECT od FROM OrderDetails od WHERE od.orderID.orderID = :oid", OrderDetails.class)
                .setParameter("oid", orderId)
                .getResultList();
    }
}
