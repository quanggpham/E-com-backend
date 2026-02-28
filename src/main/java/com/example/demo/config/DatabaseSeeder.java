package com.example.demo.config;

import com.example.demo.entity.*;
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

    @Override
    public void run(String... args) throws Exception {

        Faker faker = new Faker(new Locale("vi"));
        Random random = new Random();

        // 1. CHỈ CHẠY KHI DATABASE CÒN TRỐNG (Tránh việc mỗi lần restart server lại đẻ thêm data)
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

            log.info("Đã tạo xong dữ liệu mẫu! Bạn có thể bắt đầu test API.");

            log.info("Bắt đầu tạo Giỏ hàng và Đơn hàng giả...");

            // Lấy toàn bộ User và Product vừa tạo lên để làm nguyên liệu
            List<User> allUsers = userRepository.findAll();
            List<Product> allProducts = productRepository.findAll();

            // 4. DUYỆT QUA TỪNG USER ĐỂ TẠO DATA GIAO DỊCH
            for (User user : allUsers) {
                // Bỏ qua Admin, Admin thì không đi mua hàng
                if (user.getRole() == Role.ADMIN) continue;

                // ==========================================
                // 4.1 FAKE GIỎ HÀNG (Mỗi user có 1-3 món đang chờ thanh toán)
                // ==========================================

                Cart cart = cartRepository.findByUser(user)
                        .orElseGet(() -> {
                            Cart newCart = new Cart();
                            newCart.setUser(user);
                            user.setCart(newCart);
                            return cartRepository.save(newCart);
                        });

                int cartItemCount = faker.number().numberBetween(1, 4);
                for (int i = 0; i < cartItemCount; i++) {
                    // Bốc đại 1 sản phẩm
                    Product randomProduct = allProducts.get(random.nextInt(allProducts.size()));

                    // Lưu ý: Tùy thiết kế của bạn, nếu bạn có Entity Cart riêng thì lấy Cart của user ra trước.
                    // Ở đây mình ví dụ logic tạo CartItem:

                CartItem cartItem = CartItem.builder()
                        .cart(user.getCart())
                        .product(randomProduct)
                        .quantity(faker.number().numberBetween(1, 3))
                        .build();
                cartItemRepository.save(cartItem);

                }

                // ==========================================
                // 4.2 FAKE ĐƠN HÀNG LỊCH SỬ (Mỗi user đã từng đặt 1-5 đơn)
                // ==========================================
                int orderCount = faker.number().numberBetween(1, 6);
                for (int i = 0; i < orderCount; i++) {

                    // Lấy random 1 trạng thái đơn hàng (PENDING, COMPLETED, CANCELLED...)
                    OrderStatus randomStatus = OrderStatus.values()[random.nextInt(OrderStatus.values().length)];

                    Order order = Order.builder()
                            .user(user)
                            .fullName(user.getFullName()) // Lấy luôn tên user
                            .phoneNumber(faker.phoneNumber().cellPhone())
                            .shippingAddress(faker.address().fullAddress()) // Datafaker tự bịa địa chỉ rất xịn
                            .note(faker.lorem().sentence(5)) // Bịa câu ghi chú
                            .status(randomStatus)
                            .paymentMethod(PaymentMethod.COD)
                            .totalMoney(BigDecimal.ZERO) // Sẽ cộng dồn ở dưới
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
                    orderRepository.save(order);
                }
            }
            log.info("Khởi tạo dữ liệu thành công! Sẵn sàng cho FE lên thớt.");
        }
    }
}