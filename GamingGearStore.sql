CREATE DATABASE GamingGearStore;
GO

USE GamingGearStore;
GO

-- =============================================
-- 1. USERS
-- =============================================
CREATE TABLE Users (
    UserID INT IDENTITY(1,1) PRIMARY KEY,
    Username VARCHAR(50),
    Password VARCHAR(255),
    Email VARCHAR(100),
    FullName NVARCHAR(100),
    Phone VARCHAR(20),
    Address NVARCHAR(255),
    Role VARCHAR(20),
    CreatedAt DATETIME
);
GO


-- =============================================
-- 2. CATEGORIES
-- =============================================
CREATE TABLE Categories (
    CategoryID INT IDENTITY(1,1) PRIMARY KEY,
    CategoryName NVARCHAR(100),
    Description NVARCHAR(500),
    CreatedAt DATETIME
);
GO


-- =============================================
-- 3. PRODUCTS
-- =============================================
CREATE TABLE Products (
    ProductID INT IDENTITY(1,1) PRIMARY KEY,
    ProductName NVARCHAR(150),
    Description NVARCHAR(1000),
    Price DECIMAL(18,2),
    Quantity INT,
    Brand NVARCHAR(100),
    Image VARCHAR(255),
    CategoryID INT,
    CreatedAt DATETIME,

    FOREIGN KEY (CategoryID)
        REFERENCES Categories(CategoryID)
);
GO


-- =============================================
-- 4. ORDERS
-- =============================================
CREATE TABLE Orders (
    OrderID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT,
    OrderDate DATETIME,

    CustomerName NVARCHAR(100),
    Phone VARCHAR(20),
    Email VARCHAR(100),
    ShippingAddress NVARCHAR(255),

    TotalAmount DECIMAL(18,2),
    Status NVARCHAR(30),

    FOREIGN KEY (UserID)
        REFERENCES Users(UserID)
);
GO


-- =============================================
-- 5. ORDER DETAILS
-- =============================================
CREATE TABLE OrderDetails (
    OrderDetailID INT IDENTITY(1,1) PRIMARY KEY,
    OrderID INT,
    ProductID INT,

    Quantity INT,
    UnitPrice DECIMAL(18,2),
    Discount DECIMAL(5,2),

    FOREIGN KEY (OrderID)
        REFERENCES Orders(OrderID),

    FOREIGN KEY (ProductID)
        REFERENCES Products(ProductID)
);
GO


-- =============================================
-- 6. PAYMENTS
-- =============================================
CREATE TABLE Payments (
    PaymentID INT IDENTITY(1,1) PRIMARY KEY,
    OrderID INT,

    PaymentMethod NVARCHAR(50),
    PaymentStatus NVARCHAR(30),
    PaymentDate DATETIME,

    FOREIGN KEY (OrderID)
        REFERENCES Orders(OrderID)
);
GO


-- =============================================
-- 7. FEEDBACKS
-- =============================================
CREATE TABLE Feedbacks (
    FeedbackID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT,
    ProductID INT,

    Content NVARCHAR(1000),
    Rating INT,
    CreatedAt DATETIME,

    FOREIGN KEY (UserID)
        REFERENCES Users(UserID),

    FOREIGN KEY (ProductID)
        REFERENCES Products(ProductID)
);
GO


-- =============================================
-- SEED DATA (DỮ LIỆU MẪU)
-- =============================================

-- 1. SEED USERS
-- Mật khẩu mặc định: 123456 (admin/customer)
INSERT INTO Users (Username, Password, Email, FullName, Phone, Address, Role, CreatedAt) VALUES
('admin', '123456', 'admin@gearstore.vn', N'Quản Trị Viên', '0901234567', N'123 Đường Điện Biên Phủ, Q. Bình Thạnh, TP. HCM', 'admin', GETDATE()),
('staff', '123456', 'staff@gearstore.vn', N'Trần Văn Nhân Viên', '0909876543', N'456 Lê Lợi, Q.1, TP. HCM', 'staff', GETDATE()),
('gamer1', '123456', 'nguyenlong@gmail.com', N'Nguyễn Hoàng Long', '0912334455', N'789 Cách Mạng Tháng 8, Q.10, TP. HCM', 'customer', GETDATE()),
('gamer2', '123456', 'thanhmai@gmail.com', N'Trần Thanh Mai', '0988776655', N'101 Nguyễn Đình Chiểu, Q.3, TP. HCM', 'customer', GETDATE());
GO

-- 2. SEED CATEGORIES
INSERT INTO Categories (CategoryName, Description, CreatedAt) VALUES
(N'Bàn phím cơ', N'Bàn phím cơ gaming RGB cao cấp, switch cơ học chuẩn xác, hotswap, wireless', GETDATE()),
(N'Chuột Gaming', N'Chuột gaming siêu nhẹ, cảm biến quang học DPI cao, độ trễ cực thấp', GETDATE()),
(N'Tai nghe Gaming', N'Tai nghe âm thanh vòm 7.1, driver định hướng âm thanh, đệm tai êm ái', GETDATE()),
(N'Lót chuột & Phụ kiện', N'Bàn di chuột speed/control cỡ lớn, kê cổ tay, keycap cá tính', GETDATE()),
(N'Tay cầm chơi game', N'Tay cầm chơi game PC/Console có rung phản hồi, analog chính xác', GETDATE());
GO

-- 3. SEED PRODUCTS
-- Danh mục 1: Bàn phím cơ
-- Danh mục 2: Chuột Gaming
-- Danh mục 3: Tai nghe Gaming
-- Danh mục 4: Lót chuột & Phụ kiện
-- Danh mục 5: Tay cầm chơi game
INSERT INTO Products (ProductName, Description, Price, Quantity, Brand, Image, CategoryID, CreatedAt) VALUES
-- Bàn phím cơ (CategoryID = 1)
(N'Bàn phím cơ Logitech G Pro X TKL Lightspeed', 
 N'Bàn phím cơ không dây chuẩn thi đấu Esports, switch GX Tactile, RGB LIGHTSYNC, kết nối LIGHTSPEED siêu nhạy.', 
 4290000, 25, 'Logitech', 'https://images.unsplash.com/photo-1618384887929-16ec33fab9ef?w=600', 1, GETDATE()),

(N'Bàn phím cơ Razer BlackWidow V4 Pro', 
 N'Bàn phím cơ cao cấp switch Razer Green, núm xoay đa năng Razer Command Dial, led gầm Underglow RGB rực rỡ.', 
 5490000, 15, 'Razer', 'https://images.unsplash.com/photo-1595225476474-87563907a212?w=600', 1, GETDATE()),

(N'Bàn phím cơ Corsair K70 MAX RGB Magnetic', 
 N'Công nghệ Switch từ tính Corsair MGX có thể điều chỉnh cự ly nhận phím từ 0.4mm đến 3.6mm, khung nhôm bền bỉ.', 
 4890000, 12, 'Corsair', 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=600', 1, GETDATE()),

-- Chuột Gaming (CategoryID = 2)
(N'Chuột Logitech G Pro X Superlight 2', 
 N'Chuột gaming không dây chỉ 60g, cảm biến HERO 2 độ phân giải 32.000 DPI, tần số phản hồi 2000Hz.', 
 3590000, 30, 'Logitech', 'https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=600', 2, GETDATE()),

(N'Chuột Razer DeathAdder V3 Pro Wireless', 
 N'Form cầm công thái học huyền thoại, switch quang Razer Gen-3, cảm biến Focus Pro 30K Optical Sensor.', 
 3490000, 18, 'Razer', 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=600', 2, GETDATE()),

(N'Chuột SteelSeries Aerox 3 Wireless', 
 N'Thiết kế tổ ong siêu nhẹ 68g chống nước bụi chuẩn IP54, đèn PrismSync RGB 3 vùng đẹp mắt.', 
 2190000, 20, 'SteelSeries', 'https://images.unsplash.com/photo-1626785774573-4b799315345d?w=600', 2, GETDATE()),

-- Tai nghe Gaming (CategoryID = 3)
(N'Tai nghe HyperX Cloud III Wireless', 
 N'Pin trâu lên tới 120 giờ, âm thanh không gian DTS Headphone:X, mic đàm thoại chống ồn lọc tạp âm cực tốt.', 
 3990000, 14, 'HyperX', 'https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=600', 3, GETDATE()),

(N'Tai nghe Razer BlackShark V2 Pro 2023', 
 N'Driver Razer TriForce Titanium 50mm, mic Razer HyperClear Super Wideband đạt chuẩn streamer/esports.', 
 4590000, 10, 'Razer', 'https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=600', 3, GETDATE()),

(N'Tai nghe Logitech G733 Lightspeed Wireless RGB', 
 N'Thiết kế thời trang 278g siêu nhẹ, dải led RGB mặt trước, thời lượng pin 29 giờ, màng loa PRO-G 40mm.', 
 2890000, 8, 'Logitech', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600', 3, GETDATE()),

-- Lót chuột & Phụ kiện (CategoryID = 4)
(N'Lót chuột SteelSeries QcK Prism Cloth XL RGB', 
 N'Bàn di chuột kích thước 900 x 300mm viền LED RGB 2 vùng, bề mặt vải dệt micro độc quyền tối ưu sensor.', 
 1390000, 40, 'SteelSeries', 'https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=600', 4, GETDATE()),

(N'Lót chuột Razer Strider Chroma Extended', 
 N'Sự kết hợp hoàn hảo giữa bề mặt cứng và mềm, chuẩn trượt mượt mà, LED Chroma RGB 19 vùng chiếu sáng.', 
 2990000, 15, 'Razer', 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=600', 4, GETDATE()),

-- Tay cầm chơi game (CategoryID = 5)
(N'Tay cầm Microsoft Xbox Wireless Controller Carbon Black', 
 N'Tay cầm tiêu chuẩn công thái học tốt nhất cho PC, kết nối Bluetooth/USB-C, nút cò Hybrid D-pad nhạy bén.', 
 1490000, 22, 'Microsoft', 'https://images.unsplash.com/photo-1600080972464-8e5f35f63d08?w=600', 5, GETDATE()),

(N'Tay cầm Sony DualSense Wireless Controller PS5/PC', 
 N'Cò thích ứng Adaptive Triggers và phản hồi xúc giác Haptic Feedback chân thực từng cử chỉ trong game.', 
 1890000, 16, 'Sony', 'https://images.unsplash.com/photo-1606318801954-d46846fe3ecd?w=600', 5, GETDATE());
GO

-- 4. SEED ORDERS MẪU (Cho khách gamer1)
INSERT INTO Orders (UserID, OrderDate, CustomerName, Phone, Email, ShippingAddress, TotalAmount, Status) VALUES
(3, DATEADD(DAY, -2, GETDATE()), N'Nguyễn Hoàng Long', '0912334455', 'nguyenlong@gmail.com', N'789 Cách Mạng Tháng 8, Q.10, TP. HCM', 7880000, N'Chờ xác nhận'),
(4, DATEADD(DAY, -1, GETDATE()), N'Trần Thanh Mai', '0988776655', 'thanhmai@gmail.com', N'101 Nguyễn Đình Chiểu, Q.3, TP. HCM', 3590000, N'Đã giao');
GO

-- 5. SEED ORDER DETAILS
INSERT INTO OrderDetails (OrderID, ProductID, Quantity, UnitPrice, Discount) VALUES
-- Đơn hàng 1: Bàn phím Logitech G Pro X TKL (4,290,000) + Chuột Logitech Superlight 2 (3,590,000) = 7,880,000
(1, 1, 1, 4290000, 0),
(1, 4, 1, 3590000, 0),

-- Đơn hàng 2: Chuột Logitech Superlight 2 (3,590,000)
(2, 4, 1, 3590000, 0);
GO

-- 6. SEED PAYMENTS
INSERT INTO Payments (OrderID, PaymentMethod, PaymentStatus, PaymentDate) VALUES
(1, N'Thanh toán khi nhận hàng (COD)', N'Chưa thanh toán', GETDATE()),
(2, N'Chuyển khoản VNPAY/Ngân hàng', N'Đã thanh toán', DATEADD(DAY, -1, GETDATE()));
GO

-- 7. SEED FEEDBACKS
INSERT INTO Feedbacks (UserID, ProductID, Content, Rating, CreatedAt) VALUES
(3, 4, N'Chuột cầm rất nhẹ, vẩy tâm FPS cực kỳ đầm tay, pin dùng cả tuần chưa hết!', 5, GETDATE()),
(4, 1, N'Phím cơ gõ rất êm, LED RGB đẹp lung linh, kết nối không dây không hề bị delay.', 5, GETDATE());
GO

