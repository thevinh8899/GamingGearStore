package com.GamingGearStore.mbeans;

import com.GamingGearStore.ebeans.Categories;
import com.GamingGearStore.ebeans.OrderDetails;
import com.GamingGearStore.ebeans.Orders;
import com.GamingGearStore.ebeans.Products;
import com.GamingGearStore.sbeans.CategoriesFacadeLocal;
import com.GamingGearStore.sbeans.OrderDetailsFacadeLocal;
import com.GamingGearStore.sbeans.OrdersFacadeLocal;
import com.GamingGearStore.sbeans.ProductsFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

@Named(value = "adminMB")
@SessionScoped
public class AdminMB implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private ProductsFacadeLocal productsFacade;

    @EJB
    private CategoriesFacadeLocal categoriesFacade;

    @EJB
    private OrdersFacadeLocal ordersFacade;

    @EJB
    private OrderDetailsFacadeLocal orderDetailsFacade;

    // ================= 1. QUẢN LÝ SẢN PHẨM (PRODUCTS CRUD + FIND) =================
    private String productKeyword;
    private Integer productFilterCatId;
    private Products editingProduct = new Products();
    private Integer selectedCatIdForProduct;
    private transient Part uploadedImageFile;
    private String productMessage;

    public List<Products> getAdminProducts() {
        if (productKeyword != null && !productKeyword.trim().isEmpty()) {
            return productsFacade.searchByName(productKeyword.trim());
        }
        if (productFilterCatId != null && productFilterCatId > 0) {
            return productsFacade.findByCategory(productFilterCatId);
        }
        return productsFacade.findAll();
    }

    public String prepareCreateProduct() {
        editingProduct = new Products();
        selectedCatIdForProduct = null;
        uploadedImageFile = null;
        productMessage = null;
        return "/admin/product-form.xhtml?faces-redirect=true";
    }

    public String prepareEditProduct(Products p) {
        this.editingProduct = p;
        if (p.getCategoryID() != null) {
            this.selectedCatIdForProduct = p.getCategoryID().getCategoryID();
        }
        uploadedImageFile = null;
        this.productMessage = null;
        return "/admin/product-form.xhtml?faces-redirect=true";
    }

    public String saveProduct() {
        // Xử lý upload file ảnh nếu người dùng chọn từ máy
        if (uploadedImageFile != null && uploadedImageFile.getSize() > 0) {
            try {
                String submittedName = Paths.get(uploadedImageFile.getSubmittedFileName()).getFileName().toString();
                String cleanFileName = System.currentTimeMillis() + "_" + submittedName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");

                // 1. Ghi vào thư mục deployed runtime để hiển thị ngay lập tức trên web
                String deployedDir = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/images");
                if (deployedDir != null) {
                    File targetDir = new File(deployedDir);
                    if (!targetDir.exists()) {
                        targetDir.mkdirs();
                    }
                    File deployedFile = new File(targetDir, cleanFileName);
                    try (InputStream in = uploadedImageFile.getInputStream();
                         FileOutputStream out = new FileOutputStream(deployedFile)) {
                        in.transferTo(out);
                    }
                }

                // 2. Ghi đồng thời vào thư mục source code để lưu trữ lâu dài không bị mất khi redeploy
                String sourceDir = "D:/Assignment_EJB/GamingGearStore/GamingGearStore-war/web/resources/images";
                File sourceTarget = new File(sourceDir, cleanFileName);
                if (sourceTarget.getParentFile().exists()) {
                    try (InputStream in = uploadedImageFile.getInputStream();
                         FileOutputStream out = new FileOutputStream(sourceTarget)) {
                        in.transferTo(out);
                    }
                }

                // Gán tên file ảnh đã upload vào sản phẩm
                editingProduct.setImage(cleanFileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (selectedCatIdForProduct != null) {
            Categories cat = categoriesFacade.find(selectedCatIdForProduct);
            editingProduct.setCategoryID(cat);
        }

        if (editingProduct.getProductID() == null) {
            editingProduct.setCreatedAt(new Date());
            productsFacade.create(editingProduct);
        } else {
            productsFacade.edit(editingProduct);
        }

        productMessage = "Lưu sản phẩm thành công!";
        return "/admin/products.xhtml?faces-redirect=true";
    }

    public void deleteProduct(Products p) {
        try {
            productsFacade.remove(p);
            productMessage = "Đã xóa sản phẩm thành công!";
        } catch (Exception e) {
            productMessage = "Không thể xóa sản phẩm này vì đã phát sinh trong đơn hàng!";
        }
    }

    // ================= 2. QUẢN LÝ DANH MỤC (CATEGORIES CRUD + FIND) =================
    private String categoryKeyword;
    private Categories editingCategory = new Categories();
    private String categoryMessage;

    public List<Categories> getAdminCategories() {
        if (categoryKeyword != null && !categoryKeyword.trim().isEmpty()) {
            return categoriesFacade.searchByName(categoryKeyword.trim());
        }
        return categoriesFacade.findAll();
    }

    public String prepareCreateCategory() {
        editingCategory = new Categories();
        categoryMessage = null;
        return "/admin/category-form.xhtml?faces-redirect=true";
    }

    public String prepareEditCategory(Categories c) {
        this.editingCategory = c;
        this.categoryMessage = null;
        return "/admin/category-form.xhtml?faces-redirect=true";
    }

    public String saveCategory() {
        if (editingCategory.getCategoryID() == null) {
            editingCategory.setCreatedAt(new Date());
            categoriesFacade.create(editingCategory);
        } else {
            categoriesFacade.edit(editingCategory);
        }
        categoryMessage = "Lưu danh mục thành công!";
        return "/admin/categories.xhtml?faces-redirect=true";
    }

    public void deleteCategory(Categories c) {
        try {
            categoriesFacade.remove(c);
            categoryMessage = "Đã xóa danh mục thành công!";
        } catch (Exception e) {
            categoryMessage = "Không thể xóa danh mục vì đang có sản phẩm thuộc danh mục này!";
        }
    }

    // ================= 3. QUẢN LÝ ĐƠN HÀNG (ORDERS FIND + DETAILS + CONFIRM) =================
    private String orderKeyword;
    private String orderStatusFilter = "ALL";
    private Orders viewingOrder;
    private List<OrderDetails> viewingOrderDetails;
    private String orderMessage;

    public List<Orders> getAdminOrders() {
        return ordersFacade.searchOrders(orderKeyword, orderStatusFilter);
    }

    public String viewOrderDetail(Orders o) {
        this.viewingOrder = o;
        this.viewingOrderDetails = orderDetailsFacade.findByOrderId(o.getOrderID());
        return "/admin/order-detail.xhtml?faces-redirect=true";
    }

    public void confirmOrder(Orders o) {
        ordersFacade.updateStatus(o.getOrderID(), "Đã xác nhận");
        o.setStatus("Đã xác nhận");
        orderMessage = "Đã xác nhận đơn hàng #" + o.getOrderID() + "!";
    }

    public void shipOrder(Orders o) {
        ordersFacade.updateStatus(o.getOrderID(), "Đang giao");
        o.setStatus("Đang giao");
        orderMessage = "Đã chuyển đơn hàng #" + o.getOrderID() + " sang trạng thái Đang giao!";
    }

    public void completeOrder(Orders o) {
        ordersFacade.updateStatus(o.getOrderID(), "Hoàn thành");
        o.setStatus("Hoàn thành");
        orderMessage = "Đơn hàng #" + o.getOrderID() + " đã hoàn tất!";
    }

    public void cancelOrder(Orders o) {
        ordersFacade.updateStatus(o.getOrderID(), "Đã hủy");
        o.setStatus("Đã hủy");
        orderMessage = "Đã hủy đơn hàng #" + o.getOrderID() + "!";
    }

    // Getters & Setters
    public String getProductKeyword() { return productKeyword; }
    public void setProductKeyword(String productKeyword) { this.productKeyword = productKeyword; }

    public Integer getProductFilterCatId() { return productFilterCatId; }
    public void setProductFilterCatId(Integer productFilterCatId) { this.productFilterCatId = productFilterCatId; }

    public Products getEditingProduct() { return editingProduct; }
    public void setEditingProduct(Products editingProduct) { this.editingProduct = editingProduct; }

    public Integer getSelectedCatIdForProduct() { return selectedCatIdForProduct; }
    public void setSelectedCatIdForProduct(Integer selectedCatIdForProduct) { this.selectedCatIdForProduct = selectedCatIdForProduct; }

    public String getProductMessage() { return productMessage; }
    public void setProductMessage(String productMessage) { this.productMessage = productMessage; }

    public String getCategoryKeyword() { return categoryKeyword; }
    public void setCategoryKeyword(String categoryKeyword) { this.categoryKeyword = categoryKeyword; }

    public Categories getEditingCategory() { return editingCategory; }
    public void setEditingCategory(Categories editingCategory) { this.editingCategory = editingCategory; }

    public String getCategoryMessage() { return categoryMessage; }
    public void setCategoryMessage(String categoryMessage) { this.categoryMessage = categoryMessage; }

    public String getOrderKeyword() { return orderKeyword; }
    public void setOrderKeyword(String orderKeyword) { this.orderKeyword = orderKeyword; }

    public String getOrderStatusFilter() { return orderStatusFilter; }
    public void setOrderStatusFilter(String orderStatusFilter) { this.orderStatusFilter = orderStatusFilter; }

    public Orders getViewingOrder() { return viewingOrder; }
    public void setViewingOrder(Orders viewingOrder) { this.viewingOrder = viewingOrder; }

    public List<OrderDetails> getViewingOrderDetails() { return viewingOrderDetails; }
    public void setViewingOrderDetails(List<OrderDetails> viewingOrderDetails) { this.viewingOrderDetails = viewingOrderDetails; }

    public String getOrderMessage() { return orderMessage; }
    public void setOrderMessage(String orderMessage) { this.orderMessage = orderMessage; }

    public Part getUploadedImageFile() { return uploadedImageFile; }
    public void setUploadedImageFile(Part uploadedImageFile) { this.uploadedImageFile = uploadedImageFile; }
}
