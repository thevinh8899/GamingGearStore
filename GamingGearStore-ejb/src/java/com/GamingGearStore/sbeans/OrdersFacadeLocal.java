/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.GamingGearStore.sbeans;

import com.GamingGearStore.ebeans.Orders;
import jakarta.ejb.Local;
import java.util.List;

/**
 *
 * @author ASUS
 */
@Local
public interface OrdersFacadeLocal {

    void create(Orders orders);

    void edit(Orders orders);

    void remove(Orders orders);

    Orders find(Object id);

    List<Orders> findAll();

    List<Orders> findRange(int[] range);

    int count();

    List<Orders> findByUser(int userId);

    List<Orders> searchOrders(String keyword, String status);

    void updateStatus(int orderId, String newStatus);
}
