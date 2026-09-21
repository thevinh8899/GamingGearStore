/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.GamingGearStore.sbeans;

import com.GamingGearStore.ebeans.Orders;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

/**
 *
 * @author ASUS
 */
@Stateless
public class OrdersFacade extends AbstractFacade<Orders> implements OrdersFacadeLocal {

    @PersistenceContext(unitName = "GamingGearStore-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OrdersFacade() {
        super(Orders.class);
    }

    @Override
    public List<Orders> findByUser(int userId) {
        return em.createQuery("SELECT o FROM Orders o WHERE o.userID.userID = :uid ORDER BY o.orderID DESC", Orders.class)
                .setParameter("uid", userId)
                .getResultList();
    }

    @Override
    public List<Orders> searchOrders(String keyword, String status) {
        StringBuilder jpql = new StringBuilder("SELECT o FROM Orders o WHERE 1=1 ");
        if (keyword != null && !keyword.trim().isEmpty()) {
            jpql.append(" AND (LOWER(o.customerName) LIKE :kw OR LOWER(o.phone) LIKE :kw) ");
        }
        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL")) {
            jpql.append(" AND o.status = :status ");
        }
        jpql.append(" ORDER BY o.orderID DESC");

        var query = em.createQuery(jpql.toString(), Orders.class);
        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("kw", "%" + keyword.trim().toLowerCase() + "%");
        }
        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL")) {
            query.setParameter("status", status);
        }
        return query.getResultList();
    }

    @Override
    public void updateStatus(int orderId, String newStatus) {
        Orders order = find(orderId);
        if (order != null) {
            order.setStatus(newStatus);
            edit(order);
        }
    }
}
