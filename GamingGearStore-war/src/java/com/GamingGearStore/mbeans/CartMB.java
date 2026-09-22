package com.GamingGearStore.mbeans;

import com.GamingGearStore.ebeans.OrderDetails;
import com.GamingGearStore.ebeans.Orders;
import com.GamingGearStore.ebeans.Payments;
import com.GamingGearStore.ebeans.Products;
import com.GamingGearStore.ebeans.Users;
import com.GamingGearStore.sbeans.OrderDetailsFacadeLocal;
import com.GamingGearStore.sbeans.OrdersFacadeLocal;
import com.GamingGearStore.sbeans.PaymentsFacadeLocal;
import com.GamingGearStore.sbeans.ProductsFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named(value = "cartMB")
@SessionScoped
public class CartMB implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private OrdersFacadeLocal ordersFacade;

    @EJB
    private OrderDetailsFacadeLocal orderDetailsFacade;

    @EJB
    private PaymentsFacadeLocal paymentsFacade;

    @EJB
    private ProductsFacadeLocal productsFacade;

    // Cart Items
    private List<CartItem> items = new ArrayList<>();

    // Checkout form fields
    private String customerName;
    private String phone;
    private String email;
    private String shippingAddress;
    private String paymentMethod = "COD";
    private String checkoutError;

    // Result after placing order
    private Orders lastPlacedOrder;

    public CartMB() {
    }

    // ================= CART OPERATIONS =================
    public String addToCart(Products product) {
        return addToCart(product, 1);
    }

    public String addToCart(Products product, Long qty) {
        return addToCart(product, qty != null ? qty.intValue() : 1);
    }

    public String addToCart(Products product, long qty) {
        return addToCart(product, (int) qty);
    }

    public String addToCart(Products product, Integer qty) {
        return addToCart(product, qty != null ? qty.intValue() : 1);
    }

    public String addToCart(Products product, int qty) {
        if (product == null || qty <= 0) return null;

        // Kiểm tra xem đã đăng nhập chưa
        Users loggedUser = (Users) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loggedUser");
        if (loggedUser == null) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("loginNotice", "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng và đặt hàng!");
            return "/login.xhtml?faces-redirect=true";
        }

        for (CartItem item : items) {
            if (item.getProduct().getProductID().equals(product.getProductID())) {
                item.setQuantity(item.getQuantity() + qty);
                updateSessionCartCount();
                return "/cart.xhtml?faces-redirect=true";
            }
        }

        items.add(new CartItem(product, qty));
        updateSessionCartCount();
        return "/cart.xhtml?faces-redirect=true";
    }

    public void updateQuantity(Object productId, Object qty) {
        int pId = parseNumber(productId);
        int q = parseNumber(qty);
        updateQuantity(pId, q);
    }

    public void updateQuantity(int productId, int qty) {
        if (qty <= 0) {
            removeItem(productId);
            return;
        }
        for (CartItem item : items) {
            if (item.getProduct().getProductID().equals(productId)) {
                item.setQuantity(qty);
                break;
            }
        }
        updateSessionCartCount();
    }

    public void removeItem(Object productId) {
        removeItem(parseNumber(productId));
    }

    public void removeItem(int productId) {
        items.removeIf(item -> item.getProduct().getProductID().equals(productId));
        updateSessionCartCount();
    }

    private int parseNumber(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        if (obj != null) {
            try {
                return Integer.parseInt(obj.toString());
            } catch (Exception ignored) {
            }
        }
        return 0;
    }

    public void clearCart() {
        items.clear();
        updateSessionCartCount();
    }

    private void updateSessionCartCount() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("cart", items);
    }

    public int getTotalQuantity() {
        int total = 0;
        for (CartItem item : items) {
            total += item.getQuantity();
        }
        return total;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) {
            total = total.add(item.getSubTotal());
        }
        return total;
    }

    // ================= CHECKOUT =================
    public String proceedToCheckout() {
        if (items.isEmpty()) {
            return "/cart.xhtml?faces-redirect=true";
        }

        Users loggedUser = (Users) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loggedUser");
        if (loggedUser == null) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("loginNotice", "Vui lòng đăng nhập để tiến hành thanh toán đơn hàng!");
            return "/login.xhtml?faces-redirect=true";
        }

        this.customerName = loggedUser.getFullName();
        this.phone = loggedUser.getPhone();
        this.email = loggedUser.getEmail();
        this.shippingAddress = loggedUser.getAddress();

        checkoutError = null;
        return "/checkout.xhtml?faces-redirect=true";
    }

    public String placeOrder() {
        checkoutError = null;
        if (items.isEmpty()) {
            checkoutError = "Giỏ hàng của bạn đang trống!";
            return null;
        }

        if (customerName == null || customerName.trim().isEmpty() ||
            phone == null || phone.trim().isEmpty() ||
            shippingAddress == null || shippingAddress.trim().isEmpty()) {
            checkoutError = "Vui lòng nhập đầy đủ Tên, Số điện thoại và Địa chỉ giao hàng!";
            return null;
        }

        // 1. Tạo bản ghi Orders
        Orders order = new Orders();
        Users loggedUser = (Users) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loggedUser");
        order.setUserID(loggedUser);
        order.setCustomerName(customerName.trim());
        order.setPhone(phone.trim());
        order.setEmail(email != null ? email.trim() : null);
        order.setShippingAddress(shippingAddress.trim());
        order.setTotalAmount(getTotalAmount());
        order.setStatus("Chờ xác nhận");
        order.setOrderDate(new Date());

        ordersFacade.create(order);

        // 2. Lưu từng món vào OrderDetails & Trừ tồn kho
        for (CartItem item : items) {
            OrderDetails od = new OrderDetails();
            od.setOrderID(order);
            od.setProductID(item.getProduct());
            od.setQuantity(item.getQuantity());
            od.setUnitPrice(item.getProduct().getPrice());
            od.setDiscount(BigDecimal.ZERO);

            orderDetailsFacade.create(od);

            // Trừ số lượng tồn kho tự động
            productsFacade.updateStock(item.getProduct().getProductID(), item.getQuantity());
        }

        // 3. Tạo bản ghi Payments
        Payments payment = new Payments();
        payment.setOrderID(order);
        payment.setPaymentMethod(paymentMethod != null ? paymentMethod : "COD");
        payment.setPaymentStatus("Chưa thanh toán");
        payment.setPaymentDate(new Date());
        paymentsFacade.create(payment);

        this.lastPlacedOrder = order;

        // Xóa giỏ hàng sau khi đặt thành công
        clearCart();

        return "/order-success.xhtml?faces-redirect=true";
    }

    // ================= INNER CLASS CART ITEM =================
    public static class CartItem implements Serializable {
        private static final long serialVersionUID = 1L;
        private Products product;
        private int quantity;

        public CartItem(Products product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Products getProduct() { return product; }
        public void setProduct(Products product) { this.product = product; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public BigDecimal getSubTotal() {
            if (product != null && product.getPrice() != null) {
                return product.getPrice().multiply(BigDecimal.valueOf(quantity));
            }
            return BigDecimal.ZERO;
        }
    }

    // Getters & Setters
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getCheckoutError() { return checkoutError; }
    public void setCheckoutError(String checkoutError) { this.checkoutError = checkoutError; }

    public Orders getLastPlacedOrder() { return lastPlacedOrder; }
    public void setLastPlacedOrder(Orders lastPlacedOrder) { this.lastPlacedOrder = lastPlacedOrder; }
}
