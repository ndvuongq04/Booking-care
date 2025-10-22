# 🏥 Hệ Thống Đặt Lịch Khám Bệnh - Booking Care

Một hệ thống quản lý đặt lịch khám bệnh toàn diện được xây dựng với Spring Boot, cung cấp các API mạnh mẽ cho việc đặt lịch khám, xác thực người dùng, quản lý thanh toán, đánh giá phản hồi, và quản lý lịch hẹn với bác sĩ.

## 📋 Tổng Quan

Ứng dụng này là một hệ thống quản lý đặt lịch khám bệnh hiện đại với tính bảo mật cao, tích hợp email thông báo, quản lý người dùng dựa trên vai trò, hệ thống phản hồi của bệnh nhân, và quản lý lịch khám nâng cao được thiết kế cho cả bệnh nhân, bác sĩ và quản trị viên.

## ✨ Tính Năng Chính

- 🔐 **Xác Thực & Phân Quyền** - Xác thực an toàn dựa trên JWT
- 📅 **Quản Lý Đặt Lịch Khám** - Quản lý toàn bộ vòng đời đặt lịch hẹn
- 💳 **Quản Lý Thanh Toán** - Xử lý thanh toán trực tuyến an toàn
- 👥 **Kiểm Soát Truy Cập Theo Vai Trò** - Phân quyền Admin, Doctor, Support và Client
- ⭐ **Hệ Thống Đánh Giá** - Phản hồi và đánh giá của bệnh nhân
- 🏥 **Quản Lý Phòng Khám** - Quản lý phòng khám và chuyên khoa
- 👨‍⚕️ **Quản Lý Bác Sĩ** - Hồ sơ bác sĩ và lịch làm việc
- 📧 **Thông Báo Email** - Gửi email tự động với template
- 🔒 **OTP Verification** - Xác thực OTP qua email
- 📊 **Báo Cáo Thống Kê** - Thống kê doanh thu và hoạt động
- ☁️ **Lưu Trữ Hình Ảnh** - Tích hợp Cloudinary cho quản lý hình ảnh
- 📚 **Tài Liệu API** - Tài liệu tương tác với Swagger/OpenAPI
- 🛡️ **Bảo Mật Ưu Tiên** - Thực hành bảo mật theo tiêu chuẩn ngành

## 🛠️ Công Nghệ Sử Dụng

| Danh Mục | Công Nghệ |
|----------|-----------|
| Backend | Java 17+, Spring Boot 3.5.5 |
| Bảo Mật | Spring Security, JWT Authentication |
| Cơ Sở Dữ Liệu | MySQL 8.0+, Spring Data JPA |
| Email | Spring Mail, Thymeleaf Templates |
| Lưu Trữ | Cloudinary Cloud Storage |
| Validation | Spring Validation |
| Build Tool | Maven 3.6+ |
| Dev Tools | Lombok, Spring DevTools |

## 📋 Yêu Cầu Hệ Thống

- ☕ **Java 17** hoặc cao hơn
- 🗄️ **MySQL 8.0** hoặc cao hơn
- 📦 **Maven 3.6** hoặc cao hơn
- 💻 **IDE** (Khuyến nghị IntelliJ IDEA hoặc Eclipse)

## 🚀 Hướng Dẫn Cài Đặt

### 1. Clone Repository

```bash
git https://github.com/ndvuongq04/Booking-care.git
cd booking-care
```

### 2. Cấu Hình Môi Trường

Cập nhật file `src/main/resources/application.properties`:

```properties
spring.application.name=Booking-care

# Cấu Hình Database
spring.datasource.url=jdbc:mysql://localhost:3306/booking_care?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Cấu Hình JPA
spring.jpa.hibernate.ddl-auto=update
spring.data.web.pageable.one-indexed-parameters=true

# Cấu Hình JWT
booking-care.jwt.base-secret=your_secret_key_here
booking-care.jwt.access-token-validity-in-seconds=8640000
booking-care.jwt.refresh-token-validity-in-seconds=8640000

# Cấu Hình Cloudinary
booking-care.cloudinary.cloud_name=your_cloud_name
booking-care.cloudinary.api_key=your_api_key
booking-care.cloudinary.api_secret=your_api_secret

# Cấu Hình Email (Gmail)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Cấu Hình OTP
spring.email.otp.expiry-time-seconds=60
```

### 3. Tạo Cơ Sở Dữ Liệu

Database sẽ tự động được tạo khi chạy ứng dụng lần đầu tiên nhờ `createDatabaseIfNotExist=true`. Hoặc tạo thủ công:

```sql
CREATE DATABASE booking_care CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 4. Build & Chạy Ứng Dụng

```bash
# Cài đặt dependencies
mvn clean install

# Chạy ứng dụng
mvn spring-boot:run
```

🌐 **URL Ứng Dụng:** http://localhost:8080

## 📖 Tài Liệu API

## 🔑 API Endpoints Xác Thực (/api/v1/auth)

| Method | Endpoint | Mô Tả | Quyền Truy Cập |
|--------|----------|-------|----------------|
| POST | `/auth/login` | Đăng nhập người dùng | Public |
| POST | `/auth/register` | Đăng ký tài khoản mới (gửi OTP) | Public |
| POST | `/auth/create-verify-otp` | Xác thực OTP và tạo tài khoản | Public |
| GET | `/auth/account` | Lấy thông tin tài khoản hiện tại | Authenticated |
| GET | `/auth/refresh` | Làm mới access token | Authenticated |
| POST | `/auth/logout` | Đăng xuất | Authenticated |
| GET | `/auth/forgot-password-send-email` | Gửi OTP quên mật khẩu | Public |
| POST | `/auth/forgot-verify-otp` | Xác thực OTP quên mật khẩu | Public |
| POST | `/auth/forgot-password` | Đặt lại mật khẩu | Public |
| PUT | `/auth/reset-password/{id}` | Đổi mật khẩu | Authenticated |

### Luồng Đăng Ký

1. 📧 **Gọi** `/auth/register` với email, tên và mật khẩu
2. ✉️ **Nhận** OTP qua email
3. ✅ **Gọi** `/auth/create-verify-otp` để xác thực và tạo tài khoản

### Luồng Quên Mật Khẩu

1. 📧 **Gọi** `/auth/forgot-password-send-email` với email
2. ✉️ **Nhận** OTP qua email
3. ✅ **Gọi** `/auth/forgot-verify-otp` để xác thực OTP
4. 🔐 **Gọi** `/auth/forgot-password` để đặt lại mật khẩu

## 👤 API Quản Lý Tài Khoản (/api/v1/accounts)

| Method | Endpoint | Mô Tả | Quyền Truy Cập |
|--------|----------|-------|----------------|
| GET | `/accounts` | Lấy tất cả tài khoản | Admin Only |
| GET | `/accounts/{id}` | Lấy tài khoản theo ID | Admin/Self |
| PUT | `/accounts/{id}` | Cập nhật tài khoản | Admin/Self |
| DELETE | `/accounts/{id}` | Xóa tài khoản | Admin Only |

## 🏥 API Quản Lý Phòng Khám (/api/v1/clinics)

| Method | Endpoint | Mô Tả | Quyền Truy Cập |
|--------|----------|-------|----------------|
| GET | `/clinics` | Lấy tất cả phòng khám | Public |
| GET | `/clinics/{id}` | Lấy phòng khám theo ID | Public |
| POST | `/clinics` | Tạo phòng khám mới | Admin Only |
| PUT | `/clinics/{id}` | Cập nhật phòng khám | Admin Only |
| DELETE | `/clinics/{id}` | Xóa phòng khám | Admin Only |

## 👨‍⚕️ API Quản Lý Bác Sĩ (/api/v1/doctors)

| Method | Endpoint | Mô Tả | Quyền Truy Cập |
|--------|----------|-------|----------------|
| GET | `/doctors` | Lấy tất cả bác sĩ (có phân trang) | Public |
| GET | `/doctors/{id}` | Lấy bác sĩ theo ID | Public |
| POST | `/doctors` | Tạo hồ sơ bác sĩ mới | Admin Only |
| PUT | `/doctors/{id}` | Cập nhật hồ sơ bác sĩ | Admin/Doctor |
| DELETE | `/doctors/{id}` | Xóa bác sĩ | Admin Only |
| GET | `/doctors/specialty/{specialtyId}` | Lấy bác sĩ theo chuyên khoa | Public |

## 🩺 API Quản Lý Chuyên Khoa (/api/v1/specialties)

| Method | Endpoint | Mô Tả | Quyền Truy Cập |
|--------|----------|-------|----------------|
| GET | `/specialties` | Lấy tất cả chuyên khoa | Public |
| GET | `/specialties/{id}` | Lấy chuyên khoa theo ID | Public |
| POST | `/specialties` | Tạo chuyên khoa mới | Admin Only |
| PUT | `/specialties/{id}` | Cập nhật chuyên khoa | Admin Only |
| DELETE | `/specialties/{id}` | Xóa chuyên khoa | Admin Only |

## 📅 API Quản Lý Đặt Lịch (/api/v1/bookings)

| Method | Endpoint | Mô Tả | Quyền Truy Cập |
|--------|----------|-------|----------------|
| POST | `/bookings` | Tạo lịch hẹn mới | Authenticated |
| GET | `/bookings` | Lấy tất cả lịch hẹn (có phân trang) | Admin Only |
| GET | `/bookings/{id}` | Lấy lịch hẹn theo ID | Owner/Admin |
| PUT | `/bookings` | Cập nhật lịch hẹn | Owner/Admin |
| PUT | `/bookings/{id}/cancel` | Hủy lịch hẹn | Owner/Admin |
| PUT | `/bookings/{id}/status` | Cập nhật trạng thái lịch hẹn | Admin/Doctor |
| GET | `/bookings/patient/{id}` | Lấy lịch hẹn theo bệnh nhân | Patient/Admin |
| GET | `/bookings/doctor/{id}` | Lấy lịch hẹn theo bác sĩ | Doctor/Admin |
| GET | `/bookings/doctor/{id}/search` | Tìm kiếm lịch hẹn của bác sĩ | Doctor/Admin |
| GET | `/bookings/clinic/{id}` | Lấy lịch hẹn theo phòng khám | Admin Only |
| GET | `/bookings/clinic/{id}/search` | Tìm kiếm lịch hẹn phòng khám | Admin Only |
| GET | `/bookings/doctor/{doctorId}/available-times` | Lấy khung giờ trống của bác sĩ | Public |
| GET | `/bookings/search` | Tìm kiếm lịch hẹn nâng cao | Admin Only |

### Trạng Thái Đặt Lịch

- `PENDING` - Chờ xác nhận
- `CONFIRMED` - Đã xác nhận
- `CANCELLED` - Đã hủy
- `COMPLETED` - Hoàn thành

## 💳 API Quản Lý Thanh Toán (/api/v1/bills)

| Method | Endpoint | Mô Tả | Quyền Truy Cập |
|--------|----------|-------|----------------|
| POST | `/bills` | Tạo hóa đơn mới | Admin Only |
| GET | `/bills` | Lấy tất cả hóa đơn | Admin Only |
| GET | `/bills/{id}` | Lấy hóa đơn theo ID | Owner/Admin |
| PUT | `/bills/{id}` | Cập nhật hóa đơn | Admin Only |
| DELETE | `/bills/{id}` | Xóa hóa đơn | Admin Only |
| GET | `/bills/booking/{bookingId}` | Lấy hóa đơn theo lịch hẹn | Owner/Admin |

### Trạng Thái Thanh Toán

- `PAID` - Đã thanh toán

## ⭐ API Quản Lý Phản Hồi (/api/v1/feedbacks)

| Method | Endpoint | Mô Tả | Quyền Truy Cập |
|--------|----------|-------|----------------|
| POST | `/feedbacks` | Tạo phản hồi mới | Authenticated |
| GET | `/feedbacks` | Lấy tất cả phản hồi | Admin Only |
| GET | `/feedbacks/{id}` | Lấy phản hồi theo ID | Public |
| PUT | `/feedbacks/{id}` | Cập nhật phản hồi | Owner/Admin |
| DELETE | `/feedbacks/{id}` | Xóa phản hồi | Owner/Admin |
| GET | `/feedbacks/doctor/{doctorId}` | Lấy phản hồi của bác sĩ | Public |

## 📊 API Báo Cáo Thống Kê (/api/v1/statistics)

| Method | Endpoint | Mô Tả | Quyền Truy Cập |
|--------|----------|-------|----------------|
| GET | `/statistics/revenue` | Thống kê doanh thu | Admin Only |
| GET | `/statistics/bookings` | Thống kê lịch hẹn | Admin Only |

## 🏗️ Cấu Trúc Dự Án

```
src/
├── main/
│   ├── java/com/Booking_care/
│   │   ├── 🔧 config/           # Cấu hình hệ thống
│   │   │   ├── CloudinaryConfig.java
│   │   │   ├── CorsConfig.java
│   │   │   ├── SecurityConfig.java
│   │   │   ├── DatabaseInitialize.java
│   │   │   └── CustomAuthenticationEntryPoint.java
│   │   ├── 🎮 controller/       # REST API endpoints
│   │   │   ├── AuthController.java
│   │   │   ├── AccountController.java
│   │   │   ├── BookingController.java
│   │   │   ├── BillController.java
│   │   │   ├── ClinicController.java
│   │   │   ├── DoctorController.java
│   │   │   ├── FeedbackController.java
│   │   │   ├── PatientController.java
│   │   │   ├── SpecialtyController.java
│   │   │   ├── ServiceController.java
│   │   │   ├── StatisticController.java
│   │   │   └── ...
│   │   ├── 📝 domain/           # Entities & DTOs
│   │   │   ├── Account.java
│   │   │   ├── Booking.java
│   │   │   ├── Bill.java
│   │   │   ├── Clinic.java
│   │   │   ├── Doctor.java
│   │   │   ├── Patient.java
│   │   │   ├── Specialty.java
│   │   │   ├── Feedback.java
│   │   │   ├── dto/            # Data Transfer Objects
│   │   │   ├── enums/          # Enumerations
│   │   │   └── response/       # Response models
│   │   ├── 🗄️ repository/      # Data repositories
│   │   ├── 🔧 service/          # Business logic
│   │   │   ├── AccountService.java
│   │   │   ├── BookingService.java
│   │   │   ├── BillService.java
│   │   │   ├── EmailService.java
│   │   │   ├── OtpService.java
│   │   │   ├── CloudinaryService.java
│   │   │   └── specification/   # JPA Specifications
│   │   ├── 🔄 mapper/           # Object mappers
│   │   └── 🛠️ util/             # Utilities
│   │       ├── SecurityUtil.java
│   │       ├── FileUpload.java
│   │       ├── annotation/
│   │       └── error/          # Custom exceptions
│   └── resources/
│       ├── application.properties
│       └── templates/          # Email templates
│           ├── templateBookingSuccess.html
│           ├── templateBookingCancel.html
│           ├── templateForgotPassword.html
│           └── templateVerifyEmail.html
```

## 🔐 Cấu Hình Bảo Mật

### Cấp Độ Kiểm Soát Truy Cập

#### 🌍 Endpoints Công Khai (Không Yêu Cầu Xác Thực)

- Đăng ký người dùng
- Đăng nhập
- Quên mật khẩu
- Tìm kiếm bác sĩ và phòng khám
- Xem thông tin chuyên khoa
- Xem phản hồi

#### 🔒 Endpoints Được Bảo Vệ (Yêu Cầu JWT)

- Quản lý đặt lịch
- Quản lý thanh toán
- Quản lý hồ sơ cá nhân
- Tạo và quản lý phản hồi

#### 👑 Endpoints Dành Cho Admin (Yêu Cầu Vai Trò ADMIN)

- Quản lý người dùng
- Quản lý phòng khám
- Quản lý bác sĩ
- Quản lý chuyên khoa
- Xem báo cáo thống kê
- Cấu hình hệ thống

#### 👨‍⚕️ Endpoints Dành Cho Bác Sĩ (Yêu Cầu Vai Trò DOCTOR)

- Xem lịch hẹn của mình
- Cập nhật trạng thái lịch hẹn
- Quản lý hồ sơ bệnh án

#### 🙋 Endpoints Dành Cho Support (Yêu Cầu Vai Trò SUPPORT)

- Hỗ trợ khách hàng
- Xử lý yêu cầu

## 📧 Tích Hợp Email

### Cấu Hình

Hệ thống sử dụng Gmail SMTP để gửi email. Cần tạo **App Password** từ Google Account:

1. Truy cập Google Account Settings
2. Vào Security → 2-Step Verification
3. Tạo App Password
4. Sử dụng password này trong cấu hình

### Templates Email

- ✉️ **Xác thực đăng ký** - Gửi OTP khi đăng ký
- 🔐 **Quên mật khẩu** - Gửi OTP để reset mật khẩu
- ✅ **Đặt lịch thành công** - Thông báo khi đặt lịch thành công
- ❌ **Hủy lịch hẹn** - Thông báo khi hủy lịch

## ☁️ Tích Hợp Cloudinary

Hệ thống sử dụng Cloudinary để lưu trữ và quản lý hình ảnh:

- 👤 Ảnh đại diện người dùng
- 🏥 Ảnh phòng khám
- 👨‍⚕️ Ảnh bác sĩ
- 🩺 Ảnh chuyên khoa

### Cấu Hình Cloudinary

```properties
booking-care.cloudinary.cloud_name=your_cloud_name
booking-care.cloudinary.api_key=your_api_key
booking-care.cloudinary.api_secret=your_api_secret
```

## 🔧 Cấu Hình Mặc Định

### Tài Khoản Admin

Được tạo tự động khi khởi động ứng dụng lần đầu (xem `DatabaseInitialize.java`):

- **Username:** superAdmin01@gmail.com
- **Password:** 123456
- **Role:** ADMIN

### Cài Đặt Ứng Dụng

- **Base URL:** http://localhost:8080
- **Port Mặc Định:** 8080
- **Database:** booking_care
- **JWT Access Token:** 1 ngày
- **JWT Refresh Token:** 10 ngày
- **OTP Expiry:** 60 giây


## 📞 Liên Hệ

- **Email:** bookingcare67@gmail.com
- **Project Link:** [https://github.com/ndvuongq04/Booking-care](https://github.com/ndvuongq04/Booking-care)

## 🙏 Cảm Ơn

Cảm ơn bạn đã sử dụng Booking Care! Nếu có bất kỳ câu hỏi hoặc vấn đề nào, vui lòng tạo issue trên GitHub.

---

**Made with ❤️ by Booking Care Team**

