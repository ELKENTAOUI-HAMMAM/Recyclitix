# Recylitix

Recylitix is a complete smart recycling management solution, consisting of an Android mobile application and a Java Spring Boot backend.

## 📐 Project Architecture

The overall project architecture is illustrated here:
![Project Architecture](https://raw.githubusercontent.com/ELKENTAOUI-HAMMAM/assets/refs/heads/main/1.png)

- **recylitix_frontend/**: Android mobile application (front-end)
- **recylitix_backend/**: Java Spring Boot backend application (REST API, database)

## 📁 Folder Structure

```
Smart Recycle/
├── recylitix_frontend/    # Android mobile application
│   ├── app/               # Android source code, layouts, resources
│   └── ...
├── recylitix_backend/     # Java Spring Boot backend
│   ├── src/               # Backend source code
│   └── ...
└── README.md              # This file
```

## 🚀 Main Features

### Mobile Application (Android)
- **Object Scanning**: Take a photo, automatic classification, points assignment, save to history
- **History**: List of analyses/scans, deletion, scan details
- **User Profile**: Dynamic information, statistics (total scans, points), profile photo
- **Chatbot**: Interactive recycling assistant
- **Recycling Points Map**: Display of nearby sorting centers
- **Authentication**: Registration, login, session management

### Backend (Spring Boot)
- **Secure REST API**: User, scan, history, and recycling point management
- **Image Storage**: Upload and public access to scanned images
- **Score and statistics management**
- **Endpoints for chatbot and map**

## 🛠️ Technologies Used
- **Mobile**: Android (Java), Retrofit, Glide
- **Backend**: Java, Spring Boot, Spring Security, JPA/Hibernate, MySQL (or H2 for dev)
- **Other**: JWT, Multipart upload, REST, Google Maps

## ⚙️ Build and Run Instructions

### 1. Backend (Spring Boot)
```bash
cd recylitix_backend
./mvnw spring-boot:run
```
- API access: http://localhost:8080
- Uploaded images are accessible via `/uploads/`

### 2. Mobile Application (Android)
- Open the `SmartRecycle` folder in Android Studio
- Run on an emulator or physical device
- Configure the backend URL in the configuration files if needed

## 🧠 AI Model
The project uses an artificial intelligence model for **automatic waste classification from images**. This model, in TensorFlow Lite format (`.tflite`), is integrated into the mobile application and can recognize different types of waste (plastic, paper, glass, metal, etc.) directly on the device, without requiring an internet connection.

![AI Model](https://raw.githubusercontent.com/ELKENTAOUI-HAMMAM/assets/refs/heads/main/WhatsApp%20Image%202025-07-02%20%C3%A0%2017.35.24_c39a41c9.jpg)

- **Usage**: The model is used when scanning an object to predict its category and provide appropriate recycling instructions.
- **Format**: TensorFlow Lite (`.tflite`)
- **Access**: [Download the model on Google Drive](https://drive.google.com/file/d/11_KGBsRbR3qAA4yVAxbcp04DZCaxmi8n/view?usp=sharing)

## 📸 Screenshots

You can download a set of screenshots of the application (mobile and web) here:

- [Download screenshots (screenshot.rar)](./screenshot.rar)

<<<<<<< HEAD
- https://drive.google.com/file/d/1tr0vHzQrun4xuOo0fVZ2yXP46pZCgE7R/view?usp=sharing
=======
This file contains several images illustrating the user interface, main features, and user experience of Smart Recycle.
>>>>>>> f764d63 (fin)

## 👥 Authors
- Hammam Elkentaoui
- Abdelmounaim Salhi

## 📫 Contact
For any questions, contact: elkentaoui.ha@gmail.com 
