FROM eclipse-temurin:21-jdk-alpine

# Tạo thư mục làm việc
WORKDIR /app

# Copy file pom.xml và source code
COPY pom.xml .
COPY src ./src

# Build ứng dụng (Bỏ qua test để nhanh hơn)
RUN ./mvnw clean package -DskipTests

# Expose port 8080 (hoặc port bạn config trong application.properties)
EXPOSE 8080

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "target/*.jar"]