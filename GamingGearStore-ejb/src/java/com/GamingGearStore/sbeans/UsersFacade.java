/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.GamingGearStore.sbeans;

import com.GamingGearStore.ebeans.Users;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 *
 * @author ASUS
 */
@Stateless
public class UsersFacade extends AbstractFacade<Users> implements UsersFacadeLocal {

    @PersistenceContext(unitName = "GamingGearStore-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsersFacade() {
        super(Users.class);
    }

    @Override
    public Users login(String username, String password) {
        try {
            return em.createQuery("SELECT u FROM Users u WHERE u.username = :uname AND u.password = :pwd", Users.class)
                    .setParameter("uname", username)
                    .setParameter("pwd", password)
                    .getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        }
    }

    @Override
    public boolean checkUsernameExists(String username) {
        Long count = em.createQuery("SELECT COUNT(u) FROM Users u WHERE LOWER(u.username) = :uname", Long.class)
                .setParameter("uname", username.trim().toLowerCase())
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public Users findByUsername(String username) {
        try {
            return em.createQuery("SELECT u FROM Users u WHERE u.username = :uname", Users.class)
                    .setParameter("uname", username)
                    .getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        }
    }
}
