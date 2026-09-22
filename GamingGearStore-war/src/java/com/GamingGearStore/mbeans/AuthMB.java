package com.GamingGearStore.mbeans;

import com.GamingGearStore.ebeans.Users;
import com.GamingGearStore.sbeans.UsersFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Date;

@Named(value = "authMB")
@SessionScoped
public class AuthMB implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private UsersFacadeLocal usersFacade;

    // Login fields
    private String username;
    private String password;

    // Register fields
    private String regUsername;
    private String regPassword;
    private String regConfirmPassword;
    private String regFullName;
    private String regEmail;
    private String regPhone;
    private String regAddress;

    // Logged in user & messages
    private Users currentUser;
    private String loginMessage;
    private String registerMessage;

    public AuthMB() {
    }

    public String login() {
        loginMessage = null;
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            loginMessage = "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!";
            return null;
        }

        Users user = usersFacade.login(username.trim(), password.trim());
        if (user != null) {
            currentUser = user;
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("loggedUser", currentUser);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("loginNotice");

            // Phân quyền chuyển hướng
            if ("admin".equalsIgnoreCase(user.getRole())) {
                return "/admin/products.xhtml?faces-redirect=true";
            }
            return "/index.xhtml?faces-redirect=true";
        } else {
            loginMessage = "Sai tên đăng nhập hoặc mật khẩu. Vui lòng thử lại!";
            return null;
        }
    }

    public String register() {
        registerMessage = null;
        if (regUsername == null || regUsername.trim().isEmpty() ||
            regPassword == null || regPassword.trim().isEmpty() ||
            regFullName == null || regFullName.trim().isEmpty()) {
            registerMessage = "Vui lòng điền đầy đủ các thông tin bắt buộc (*)!";
            return null;
        }

        if (!regPassword.equals(regConfirmPassword)) {
            registerMessage = "Mật khẩu xác nhận không khớp!";
            return null;
        }

        if (usersFacade.checkUsernameExists(regUsername)) {
            registerMessage = "Tên đăng nhập '" + regUsername + "' đã tồn tại. Vui lòng chọn tên khác!";
            return null;
        }

        Users newUser = new Users();
        newUser.setUsername(regUsername.trim());
        newUser.setPassword(regPassword.trim());
        newUser.setFullName(regFullName.trim());
        newUser.setEmail(regEmail != null ? regEmail.trim() : null);
        newUser.setPhone(regPhone != null ? regPhone.trim() : null);
        newUser.setAddress(regAddress != null ? regAddress.trim() : null);
        newUser.setRole("customer");
        newUser.setCreatedAt(new Date());

        usersFacade.create(newUser);

        // Đăng nhập luôn sau khi đăng ký
        currentUser = newUser;
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("loggedUser", currentUser);

        // Reset form
        clearRegisterForm();

        return "/index.xhtml?faces-redirect=true";
    }

    public String logout() {
        currentUser = null;
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }

    private void clearRegisterForm() {
        regUsername = "";
        regPassword = "";
        regConfirmPassword = "";
        regFullName = "";
        regEmail = "";
        regPhone = "";
        regAddress = "";
        registerMessage = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && "admin".equalsIgnoreCase(currentUser.getRole());
    }

    // Getters & Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRegUsername() { return regUsername; }
    public void setRegUsername(String regUsername) { this.regUsername = regUsername; }

    public String getRegPassword() { return regPassword; }
    public void setRegPassword(String regPassword) { this.regPassword = regPassword; }

    public String getRegConfirmPassword() { return regConfirmPassword; }
    public void setRegConfirmPassword(String regConfirmPassword) { this.regConfirmPassword = regConfirmPassword; }

    public String getRegFullName() { return regFullName; }
    public void setRegFullName(String regFullName) { this.regFullName = regFullName; }

    public String getRegEmail() { return regEmail; }
    public void setRegEmail(String regEmail) { this.regEmail = regEmail; }

    public String getRegPhone() { return regPhone; }
    public void setRegPhone(String regPhone) { this.regPhone = regPhone; }

    public String getRegAddress() { return regAddress; }
    public void setRegAddress(String regAddress) { this.regAddress = regAddress; }

    public Users getCurrentUser() { return currentUser; }
    public void setCurrentUser(Users currentUser) { this.currentUser = currentUser; }

    public String getLoginMessage() { return loginMessage; }
    public void setLoginMessage(String loginMessage) { this.loginMessage = loginMessage; }

    public String getRegisterMessage() { return registerMessage; }
    public void setRegisterMessage(String registerMessage) { this.registerMessage = registerMessage; }
}
