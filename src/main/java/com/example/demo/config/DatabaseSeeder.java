package com.example.demo.config;

import com.example.demo.entity.*;
import com.example.demo.enums.DiscountType;
import com.example.demo.enums.OrderStatus;
import com.example.demo.enums.PaymentMethod;
import com.example.demo.enums.Role;
import com.example.demo.repository.*;
import com.example.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;

    @Override
    public void run(String... args) throws Exception {

        Faker faker = new Faker(new Locale("vi"));
        Random random = new Random();

        // 1. CHỈ CHẠY KHI DATABASE CÒN TRỐNG
        if (userRepository.count() == 0 && categoryRepository.count() == 0) {
            log.info("Bắt đầu tự động tạo dữ liệu mẫu (Seeding)...");

            // 1. TẠO 1 TÀI KHOẢN ADMIN MẶC ĐỊNH
            User admin = User.builder()
                    .fullName("Quản Trị Viên")
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("123456"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);

            // 2. TẠO 10 USER KHÁCH HÀNG GIẢ
            for (int i = 0; i < 10; i++) {
                User user = User.builder()
                        .fullName(faker.name().fullName()) // Tự bịa tên: Nguyễn Văn A, Trần Thị B...
                        .email(faker.internet().emailAddress())
                        .password(passwordEncoder.encode("123456"))
                        .role(Role.USER)
                        .build();
                userRepository.save(user);
            }

            // 3. TẠO DANH MỤC VÀ SẢN PHẨM MẪU
            List<String> categoryNames = List.of("Đồ ăn nhanh", "Đồ uống", "Tráng miệng");

            for (String categoryName : categoryNames) {
                Category category = Category.builder().name(categoryName).build();
                categoryRepository.save(category);

                // Mỗi danh mục tạo ngẫu nhiên 5 sản phẩm
                for (int j = 0; j < 5; j++) {
                    Product product = Product.builder()
                            .category(category)
                            .name(faker.food().dish()) // Tự bịa tên món ăn!
                            .description(faker.lorem().sentence(10))
                            .price(BigDecimal.valueOf(faker.number().numberBetween(20000, 150000)))
                            .stockQuantity((long) faker.number().numberBetween(10, 100))
                            .build();
                    productRepository.save(product);
                }
            }

            // 4. BỔ SUNG: TẠO MÃ GIẢM GIÁ (COUPON)
            List<Coupon> coupons = new ArrayList<>();
            // Mã giảm giá thẳng 50k cho đơn từ 150k
            coupons.add(Coupon.builder()
                    .code("GIAM50K")
                    .discountType(DiscountType.FIXED_AMOUNT)
                    .discountValue(new BigDecimal("50000"))
                    .minOrderValue(new BigDecimal("150000"))
                    .usageLimit(100)
                    .usedCount(0)
                    .startDate(LocalDate.now().minusDays(5))
                    .expirationDate(LocalDate.now().plusDays(30))
                    .active(true)
                    .build());

            // Mã giảm 20%, tối đa 30k cho đơn từ 100k
            coupons.add(Coupon.builder()
                    .code("GIAM20PT")
                    .discountType(DiscountType.PERCENTAGE)
                    .discountValue(new BigDecimal("20")) // 20%
                    .maxDiscountAmount(new BigDecimal("30000"))
                    .minOrderValue(new BigDecimal("100000"))
                    .usageLimit(50)
                    .usedCount(0)
                    .startDate(LocalDate.now().minusDays(10))
                    .expirationDate(LocalDate.now().plusDays(15))
                    .active(true)
                    .build());
            couponRepository.saveAll(coupons);

            log.info("Đã tạo xong dữ liệu mẫu!");

            log.info("Bắt đầu tạo Giỏ hàng và Đơn hàng giả...");

            List<User> allUsers = userRepository.findAll();
            List<Product> allProducts = productRepository.findAll();

            for (User user : allUsers) {
                // Bỏ qua Admin, Admin thì không đi mua hàng
                if (user.getRole() == Role.ADMIN) continue;

                Cart cart = cartRepository.findByUser(user)
                        .orElseGet(() -> {
                            Cart newCart = new Cart();
                            newCart.setUser(user);
                            user.setCart(newCart);
                            return cartRepository.save(newCart);
                        });

                int cartItemCount = faker.number().numberBetween(1, 4);
                for (int i = 0; i < cartItemCount; i++) {

                    Product randomProduct = allProducts.get(random.nextInt(allProducts.size()));

                CartItem cartItem = CartItem.builder()
                        .cart(user.getCart())
                        .product(randomProduct)
                        .quantity(faker.number().numberBetween(1, 3))
                        .build();
                cartItemRepository.save(cartItem);

                }

                int orderCount = faker.number().numberBetween(1, 6);
                for (int i = 0; i < orderCount; i++) {

                    // Lấy random 1 trạng thái đơn hàng (PENDING, COMPLETED, CANCELLED...)
                    OrderStatus randomStatus = OrderStatus.values()[random.nextInt(OrderStatus.values().length)];

                    Order order = Order.builder()
                            .user(user)
                            .fullName(user.getFullName())
                            .phoneNumber(faker.phoneNumber().cellPhone())
                            .shippingAddress(faker.address().fullAddress())
                            .note(faker.lorem().sentence(5))
                            .status(randomStatus)
                            .paymentMethod(PaymentMethod.COD)
                            .totalMoney(BigDecimal.ZERO)
                            .build();
                    BigDecimal totalMoney = BigDecimal.ZERO;

                    // Mỗi đơn hàng khách mua 1-4 món khác nhau
                    int orderDetailCount = faker.number().numberBetween(1, 5);
                    for (int j = 0; j < orderDetailCount; j++) {
                        Product randomProduct = allProducts.get(random.nextInt(allProducts.size()));
                        int qty = faker.number().numberBetween(1, 4);

                        // Chụp ảnh giá giống hệt như logic thật
                        OrderDetail detail = OrderDetail.builder()
                                .product(randomProduct)
                                .price(randomProduct.getPrice())
                                .quantity(qty)
                                .build();

                        order.addOrderDetail(detail);

                        // Tính tiền
                        BigDecimal itemTotal = randomProduct.getPrice().multiply(BigDecimal.valueOf(qty));
                        totalMoney = totalMoney.add(itemTotal);
                    }

                    // Chốt tổng tiền và lưu
                    order.setTotalMoney(totalMoney);
                    order.setSubTotal(totalMoney);

                    order.setDiscountAmount(BigDecimal.ZERO);
                    // Ép đơn hàng có ngày tạo ngẫu nhiên trong 30 ngày qua để test thống kê
                    order.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));

                    OrderStatus status = (random.nextInt(10) < 7) ? OrderStatus.COMPLETED : OrderStatus.CANCELLED;
                    order.setStatus(status);
                    orderRepository.save(order);
                }
            }
            log.info("Khởi tạo dữ liệu thành công!");
        }
    }
}