# 📱 SimpleChat

SimpleChat là một ứng dụng chat realtime 1-1 đơn giản, được xây dựng với mục đích học tập và thực hành. Ứng dụng tập trung vào các chức năng cơ bản như gửi/nhận tin nhắn theo thời gian thực, với giao diện tối giản, dễ hiểu — phù hợp cho người mới bắt đầu tìm hiểu về lập trình mobile và backend realtime.

---

## 🚀 Tính năng chính

* 💬 Chat realtime 1-1
* 🔐 Xác thực người dùng bằng Firebase Authentication
* ☁️ Lưu trữ và đồng bộ tin nhắn bằng Firebase Realtime Database
* 📱 Giao diện đơn giản, dễ sử dụng

---

## 🛠️ Công nghệ sử dụng

* **Android (Java)**
* **Firebase Authentication**
* **Firebase Realtime Database**

---

## ⚙️ Hướng dẫn cài đặt & chạy dự án

### Bước 1: Clone dự án

```bash
git clone <link-github-repository>
```

---

### Bước 2: Cấu hình Firebase

1. Truy cập: https://console.firebase.google.com/
2. Tạo một project mới
3. Thêm ứng dụng Android vào project
4. Kích hoạt các dịch vụ:

   * **Authentication** (chọn Email/Password)
   * **Realtime Database**

---

### Bước 3: Thêm file cấu hình Firebase

* Tải file `google-services.json` từ Firebase Console
* Copy file này vào thư mục:

```
app/google-services.json
```

---

### Bước 4: Mở project

* Mở **Android Studio**
* Chọn **Open an existing project**
* Sync Gradle và chờ build hoàn tất

---

### Bước 5: Chạy ứng dụng

* Kết nối thiết bị Android hoặc dùng Emulator
* Nhấn **Run ▶️** để chạy app

---

## 📌 Lưu ý

* Đảm bảo đã bật Internet trên thiết bị
* Kiểm tra cấu hình Firebase đúng package name
* Nếu gặp lỗi, hãy thử:

  * Clean Project
  * Rebuild Project
  * Sync Gradle lại

---

## 🎯 Mục tiêu dự án

Dự án này không nhằm mục đích thương mại, mà để:

* Luyện tập kỹ năng lập trình Android
* Hiểu cách hoạt động của hệ thống realtime
* Làm nền tảng để phát triển các ứng dụng phức tạp hơn (chat nhóm, gửi ảnh, gọi video...)

---

## 📄 License

Dự án sử dụng cho mục đích học tập và tham khảo.

---

## 🤝 Đóng góp

Mọi đóng góp đều được hoan nghênh!
Bạn có thể fork repo và tạo pull request.

---
Chúc tất cả các bạn học code vui vẻ

Chúc bạn học tốt và code vui vẻ 🚀
