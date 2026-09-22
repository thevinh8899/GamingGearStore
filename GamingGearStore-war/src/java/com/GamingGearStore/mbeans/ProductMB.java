package com.GamingGearStore.mbeans;

import com.GamingGearStore.ebeans.Categories;
import com.GamingGearStore.ebeans.Feedbacks;
import com.GamingGearStore.ebeans.Products;
import com.GamingGearStore.ebeans.Users;
import com.GamingGearStore.sbeans.CategoriesFacadeLocal;
import com.GamingGearStore.sbeans.FeedbacksFacadeLocal;
import com.GamingGearStore.sbeans.ProductsFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Named(value = "productMB")
@SessionScoped
public class ProductMB implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private ProductsFacadeLocal productsFacade;

    @EJB
    private CategoriesFacadeLocal categoriesFacade;

    @EJB
    private FeedbacksFacadeLocal feedbacksFacade;

    // Filters
    private Integer selectedCategoryId;
    private String searchKeyword;
    private String selectedBrand;

    // Current viewing product for detail page
    private Products currentProduct;

    // Feedback fields
    private String feedbackContent;
    private Integer feedbackRating = 5;
    private String feedbackMessage;

    public ProductMB() {
    }

    public String getImageUrl(String img) {
        if (img == null || img.trim().isEmpty()) {
            return "https://images.unsplash.com/photo-1618384887929-16ec33fab9ef?w=600";
        }
        if (img.startsWith("http://") || img.startsWith("https://")) {
            return img;
        }
        if (img.startsWith("/")) {
            return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + img;
        }
        return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/resources/images/" + img;
    }

    public List<Products> getAllProducts() {
        return productsFacade.findAll();
    }

    public List<Products> getFeaturedProducts() {
        return productsFacade.findFeatured(8);
    }

    public List<Categories> getAllCategories() {
        return categoriesFacade.findAll();
    }

    public List<Products> getFilteredProducts() {
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            return productsFacade.searchByName(searchKeyword.trim());
        }
        if (selectedCategoryId != null && selectedCategoryId > 0) {
            return productsFacade.findByCategory(selectedCategoryId);
        }
        if (selectedBrand != null && !selectedBrand.trim().isEmpty() && !selectedBrand.equalsIgnoreCase("ALL")) {
            return productsFacade.findByBrand(selectedBrand.trim());
        }
        return productsFacade.findAll();
    }

    public String viewDetail(int productId) {
        currentProduct = productsFacade.find(productId);
        feedbackMessage = null;
        feedbackContent = "";
        return "/product-detail.xhtml?faces-redirect=true";
    }

    public String viewDetail(Object productId) {
        if (productId instanceof Number) {
            return viewDetail(((Number) productId).intValue());
        }
        if (productId != null) {
            try {
                return viewDetail(Integer.parseInt(productId.toString()));
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public List<Feedbacks> getCurrentProductFeedbacks() {
        if (currentProduct != null) {
            return feedbacksFacade.findByProductId(currentProduct.getProductID());
        }
        return null;
    }

    public String submitFeedback() {
        Users loggedUser = (Users) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loggedUser");
        if (loggedUser == null) {
            feedbackMessage = "Bạn cần đăng nhập để gửi đánh giá sản phẩm!";
            return null;
        }
        if (currentProduct == null) {
            feedbackMessage = "Lỗi: Không tìm thấy sản phẩm!";
            return null;
        }
        if (feedbackContent == null || feedbackContent.trim().isEmpty()) {
            feedbackMessage = "Vui lòng nhập nội dung đánh giá!";
            return null;
        }

        Feedbacks fb = new Feedbacks();
        fb.setContent(feedbackContent.trim());
        fb.setRating(feedbackRating != null ? feedbackRating : 5);
        fb.setProductID(currentProduct);
        fb.setUserID(loggedUser);
        fb.setCreatedAt(new Date());

        feedbacksFacade.create(fb);

        feedbackContent = "";
        feedbackRating = 5;
        feedbackMessage = "Cảm ơn bạn đã đánh giá sản phẩm!";
        return null;
    }

    public void filterByCategory(Integer catId) {
        this.selectedCategoryId = catId;
        this.searchKeyword = null;
        this.selectedBrand = null;
    }

    public void filterByCategory(Object catId) {
        if (catId instanceof Number) {
            this.selectedCategoryId = ((Number) catId).intValue();
        } else if (catId != null) {
            try {
                this.selectedCategoryId = Integer.parseInt(catId.toString());
            } catch (Exception ignored) {
                this.selectedCategoryId = null;
            }
        } else {
            this.selectedCategoryId = null;
        }
        this.searchKeyword = null;
        this.selectedBrand = null;
    }

    public void resetFilter() {
        this.selectedCategoryId = null;
        this.searchKeyword = null;
        this.selectedBrand = null;
    }

    // Getters & Setters
    public Integer getSelectedCategoryId() { return selectedCategoryId; }
    public void setSelectedCategoryId(Integer selectedCategoryId) { this.selectedCategoryId = selectedCategoryId; }

    public String getSearchKeyword() { return searchKeyword; }
    public void setSearchKeyword(String searchKeyword) { this.searchKeyword = searchKeyword; }

    public String getSelectedBrand() { return selectedBrand; }
    public void setSelectedBrand(String selectedBrand) { this.selectedBrand = selectedBrand; }

    public Products getCurrentProduct() { return currentProduct; }
    public void setCurrentProduct(Products currentProduct) { this.currentProduct = currentProduct; }

    public String getFeedbackContent() { return feedbackContent; }
    public void setFeedbackContent(String feedbackContent) { this.feedbackContent = feedbackContent; }

    public Integer getFeedbackRating() { return feedbackRating; }
    public void setFeedbackRating(Integer feedbackRating) { this.feedbackRating = feedbackRating; }

    public String getFeedbackMessage() { return feedbackMessage; }
    public void setFeedbackMessage(String feedbackMessage) { this.feedbackMessage = feedbackMessage; }
}
