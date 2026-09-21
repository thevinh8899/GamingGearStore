/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.GamingGearStore.sbeans;

import com.GamingGearStore.ebeans.Users;
import jakarta.ejb.Local;
import java.util.List;

/**
 *
 * @author ASUS
 */
@Local
public interface UsersFacadeLocal {

    void create(Users users);

    void edit(Users users);

    void remove(Users users);

    Users find(Object id);

    List<Users> findAll();

    List<Users> findRange(int[] range);

    int count();

    Users login(String username, String password);

    boolean checkUsernameExists(String username);

    Users findByUsername(String username);
}
