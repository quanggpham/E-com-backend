# API Documentation - E-commerce Backend

**Base URL:** `http://localhost:8080/api/v1`

**Chung:** Mọi API cần xác thực (trừ Auth, Products GET, Categories GET) đều cần header:
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Format Response:**
```json
{
  "status": 200,
  "message": "Thông báo",
  "data": { ... },
  "timestamp": "2026-03-12T10:00:00"
}
```

---

## 1. Authentication (`/api/v1/auth`)

### 1.1 Đăng ký
**POST** `/auth/register`

| Auth | Body |
|------|------|
| Không | Có |

**Request:**
```json
{
  "name": "Nguyễn Văn A",
  "email": "user@example.com",
  "password": "123456"
}
```

**Response (201):**
```json
{
  "status": 201,
  "message": "Đăng ký thành công",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

---

### 1.2 Đăng nhập
**POST** `/auth/login`

| Auth | Body |
|------|------|
| Không | Có |

**Request:**
```json
{
  "email": "user@example.com",
  "password": "123456"
}
```

**Response (200):**
```json
{
  "status": 200,
  "message": "Đăng nhập thành công",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

---

## 2. Profile (`/api/v1/profile`)

### 2.1 Lấy thông tin cá nhân
**GET** `/profile`

**Response (200):**
```json
{
  "status": 200,
  "message": "Lấy thông tin cá nhân thành công",
  "data": {
    "id": 1,
    "username": "nguyenvana",
    "email": "user@example.com",
    "fullName": "Nguyễn Văn A",
    "phone": "0901234567",
    "imageUrl": null,
    "address": null,
    "role": "USER",
    "createAt": "2026-03-12T10:00:00"
  }
}
```

---

### 2.2 Cập nhật thông tin cá nhân
**PUT** `/profile`

**Request:**
```json
{
  "username": "nguyenvana",
  "fullName": "Nguyễn Văn A",
  "phone": "0901234567"
}
```

**Response (200):**
```json
{
  "status": 200,
  "message": "Cập nhật thông tin cá nhân thành công",
  "data": {
    "id": 1,
    "username": "nguyenvana",
    "email": "user@example.com",
    "fullName": "Nguyễn Văn A",
    "phone": "0901234567",
    "role": "USER",
    "createAt": "2026-03-12T10:00:00"
  }
}
```

---

## 3. Địa chỉ (`/api/v1/addresses`)

### 3.1 Danh sách địa chỉ
**GET** `/addresses`

**Response (200):**
```json
{
  "status": 200,
  "message": "Lấy danh sách địa chỉ thành công",
  "data": [
    {
      "id": 1,
      "recipientName": "Nguyễn Văn A",
      "phone": "0901234567",
      "addressLine": "123 Đường ABC, Phường XYZ",
      "district": "Quận 1",
      "city": "TP. Hồ Chí Minh",
      "isDefault": true,
      "fullAddress": "123 Đường ABC, Phường XYZ, Quận 1, TP. Hồ Chí Minh"
    }
  ]
}
```

---

### 3.2 Chi tiết địa chỉ
**GET** `/addresses/{id}`

**Response (200):**
```json
{
  "status": 200,
  "message": "Lấy thông tin địa chỉ thành công",
  "data": {
    "id": 1,
    "recipientName": "Nguyễn Văn A",
    "phone": "0901234567",
    "addressLine": "123 Đường ABC, Phường XYZ",
    "district": "Quận 1",
    "city": "TP. Hồ Chí Minh",
    "isDefault": true,
    "fullAddress": "123 Đường ABC, Phường XYZ, Quận 1, TP. Hồ Chí Minh"
  }
}
```

---

### 3.3 Thêm địa chỉ
**POST** `/addresses`

**Request:**
```json
{
  "recipientName": "Nguyễn Văn A",
  "phone": "0901234567",
  "addressLine": "123 Đường ABC, Phường XYZ",
  "district": "Quận 1",
  "city": "TP. Hồ Chí Minh",
  "isDefault": false
}
```

**Response (201):**
```json
{
  "status": 201,
  "message": "Thêm địa chỉ thành công",
  "data": {
    "id": 2,
    "recipientName": "Nguyễn Văn A",
    "phone": "0901234567",
    "addressLine": "123 Đường ABC, Phường XYZ",
    "district": "Quận 1",
    "city": "TP. Hồ Chí Minh",
    "isDefault": false,
    "fullAddress": "123 Đường ABC, Phường XYZ, Quận 1, TP. Hồ Chí Minh"
  }
}
```

---

### 3.4 Cập nhật địa chỉ
**PUT** `/addresses/{id}`

**Request:** (tất cả trường optional)
```json
{
  "recipientName": "Trần Văn B",
  "phone": "0912345678",
  "addressLine": "456 Đường XYZ",
  "district": "Quận 7",
  "city": "TP. Hồ Chí Minh",
  "isDefault": true
}
```

**Response (200):** Giống 3.2

---

### 3.5 Đặt địa chỉ mặc định
**PUT** `/addresses/{id}/default`

| Body |
|------|
| Không cần |

**Response (200):** Giống 3.2

---

### 3.6 Xóa địa chỉ
**DELETE** `/addresses/{id}`

**Response (204):**
```json
{
  "status": 204,
  "message": "Xóa địa chỉ thành công",
  "data": null
}
```

---

## 4. Giỏ hàng (`/api/v1/carts`)

### 4.1 Thêm vào giỏ
**POST** `/carts/add`

**Request:**
```json
{
  "productId": 1,
  "quantity": 2
}
```

**Response (200):**
```json
{
  "status": 200,
  "message": "Thêm vào giỏ hàng thành công!"
}
```

---

### 4.2 Lấy giỏ hàng
**GET** `/carts`

**Response (200):**
```json
{
  "status": 200,
  "message": "Get giỏ hàng thành công",
  "data": {
    "cartId": 1,
    "items": [
      {
        "itemId": 1,
        "productId": 1,
        "productName": "Phở bò",
        "imageUrl": null,
        "price": 45000,
        "quantity": 2,
        "subTotal": 90000
      }
    ],
    "totalAmt": 90000
  }
}
```

---

### 4.3 Cập nhật số lượng
**PUT** `/carts/item`

**Request:**
```json
{
  "productId": 1,
  "quantity": 3
}
```

**Response (200):**
```json
{
  "status": 200,
  "message": "Cập nhật số lượng thành công"
}
```

---

### 4.4 Xóa sản phẩm khỏi giỏ
**DELETE** `/carts/item/{productId}`

**Response (200):**
```json
{
  "status": 200,
  "message": "Xóa sản phẩm thành công"
}
```

---

### 4.5 Xóa toàn bộ giỏ
**DELETE** `/carts/clear`

**Response (200):**
```json
{
  "status": 200,
  "message": "Giỏ hàng đã được dọn sạch"
}
```

---

## 5. Đơn hàng (`/api/v1/orders`)

### 5.1 Đặt hàng (Checkout)
**POST** `/orders`

**Request:**
```json
{
  "fullName": "Nguyễn Văn A",
  "phoneNumber": "0901234567",
  "shippingAddress": "123 Đường ABC, Quận 1, TP.HCM",
  "note": "Giao giờ hành chính",
  "paymentMethod": "COD",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 1
    }
  ],
  "code": "GIAM50K"
}
```

**paymentMethod:** `COD` | `BANK_TRANSFER` | `CREDIT` | `VNPAY` | `MOMO` | `SEPAY`

**Response (201):**
```json
{
  "status": 201,
  "message": "Đặt đơn hàng thành công",
  "data": {
    "id": 1,
    "fullName": "Nguyễn Văn A",
    "phoneNumber": "0901234567",
    "shippingAddress": "123 Đường ABC, Quận 1, TP.HCM",
    "note": "Giao giờ hành chính",
    "status": "PENDING",
    "paymentMethod": "COD",
    "subTotal": 150000,
    "discountAmount": 50000,
    "totalMoney": 100000,
    "createdAt": "2026-03-12 10:00:00",
    "orderDetails": [
      {
        "id": 1,
        "name": "Phở bò",
        "price": 45000,
        "quantity": 2
      }
    ]
  }
}
```

**status:** `PENDING` | `CONFIRMED` | `SHIPPING` | `COMPLETED` | `CANCELLED`

---

### 5.2 Danh sách đơn hàng của tôi
**GET** `/orders?page=0&size=10&sort=createdAt,desc`

**Response (200):**
```json
{
  "status": 200,
  "message": "Xem danh sách đơn hàng thành công",
  "data": {
    "currentPage": 1,
    "totalPages": 2,
    "pageSize": 10,
    "totalElements": 15,
    "items": [
      {
        "id": 1,
        "fullName": "Nguyễn Văn A",
        "status": "COMPLETED",
        "totalMoney": 100000,
        "createdAt": "2026-03-12 10:00:00",
        "orderDetails": [...]
      }
    ]
  }
}
```

---

### 5.3 Chi tiết đơn hàng
**GET** `/orders/{id}`

**Response (200):** Giống object trong `items` của 5.2

---

### 5.4 Hủy đơn hàng
**PUT** `/orders/cancel/{id}`

| Body |
|------|
| Không cần |

**Response (200):**
```json
{
  "status": 200,
  "message": "Hủy đơn hàng thành công"
}
```

---

## 6. Mã giảm giá (`/api/v1/coupons`)

### 6.1 Tính tiền giảm giá (User)
**GET** `/coupons/calculate?code=GIAM50K&amount=200000`

**Query params:** `code`, `amount` (BigDecimal)

**Response (200):**
```json
{
  "status": 200,
  "message": "Tính toán số tiền giảm giá thành công",
  "data": 50000
}
```

---

## 7. Danh mục (`/api/v1/categories`)

### 7.1 Danh sách danh mục
**GET** `/categories`

| Auth |
|------|
| Không |

**Response (200):**
```json
{
  "status": 200,
  "message": "Lấy danh sách thành công",
  "data": [
    {
      "id": 1,
      "name": "Đồ ăn nhanh",
      "description": null
    }
  ]
}
```

---

### 7.2 Chi tiết danh mục
**GET** `/categories/{id}`

| Auth |
|------|
| Không |

**Response (200):** Giống object trong mảng 7.1

---

## 8. Sản phẩm (`/api/v1/products`)

### 8.1 Tìm kiếm / Danh sách
**GET** `/products?page=0&size=10&sort=id,asc&name=phở&categoryId=1&minPrice=10000&maxPrice=100000`

**Query params:**
- `page` (default: 0)
- `size` (default: 10)
- `sort` (vd: `id,asc`, `price,desc`)
- `name` - tìm theo tên
- `categoryId` - lọc theo danh mục
- `minPrice` - giá tối thiểu
- `maxPrice` - giá tối đa

| Auth |
|------|
| Không |

**Response (200):**
```json
{
  "status": 200,
  "message": "Tìm kiếm thành công",
  "data": {
    "currentPage": 1,
    "totalPages": 3,
    "pageSize": 10,
    "totalElements": 25,
    "items": [
      {
        "id": 1,
        "name": "Phở bò",
        "description": "Phở bò đặc biệt",
        "price": 45000,
        "stockQuantity": 100,
        "imageUrl": null,
        "isActive": true,
        "category": {
          "id": 1,
          "name": "Đồ ăn nhanh",
          "description": null
        },
        "createdAt": "2026-03-12T10:00:00"
      }
    ]
  }
}
```

---

### 8.2 Chi tiết sản phẩm
**GET** `/products/{id}`

| Auth |
|------|
| Không |

**Response (200):** Giống object trong `items` của 8.1

---

## 9. AI Chat (`/api/v1/ai`)

### 9.1 Chat (JSON)
**GET** `/ai/chat?message=Quán có món gì ngon?`

| Auth |
|------|
| Không |

**Response (200):**
```json
{
  "status": 200,
  "message": null,
  "data": "Dựa trên thực đơn, quán có các món..."
}
```

---

### 9.2 Chat Stream (SSE)
**GET** `/ai/chat/stream?message=Quán có món gì ngon?`

**Response:** `text/event-stream` - Server-Sent Events

---

## 10. Enums tham khảo

### PaymentMethod
`COD` | `BANK_TRANSFER` | `CREDIT` | `VNPAY` | `MOMO` | `SEPAY`

### OrderStatus
`PENDING` | `CONFIRMED` | `SHIPPING` | `COMPLETED` | `CANCELLED`

### Role
`USER` | `ADMIN`

---

## 11. Phân quyền

| Endpoint | Quyền |
|----------|-------|
| `/auth/**` | Public |
| `GET /categories/**`, `GET /products/**` | Public |
| `/ai/**`, `/payments/**` | Public |
| `/profile/**`, `/addresses/**`, `/carts/**`, `/orders/**`, `GET /coupons/calculate` | Đăng nhập |
| `/users/**`, `/admin/**`, `POST|PUT|DELETE /categories/**`, `/coupons/**` (trừ calculate) | ADMIN |

---

## 12. Mã lỗi thường gặp

| HTTP | Ý nghĩa |
|------|---------|
| 400 | Bad Request - Dữ liệu không hợp lệ |
| 401 | Unauthorized - Chưa đăng nhập / Token hết hạn |
| 403 | Forbidden - Không có quyền |
| 404 | Not Found - Không tìm thấy tài nguyên |
| 500 | Server Error - Lỗi hệ thống |
