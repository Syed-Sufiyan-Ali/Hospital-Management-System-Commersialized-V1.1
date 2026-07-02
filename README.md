# 🏥 Sufiyan Health Clinic Management System

A desktop-based Clinic Management System developed using **Java Swing** and **MySQL**, designed to simplify day-to-day clinic operations through a clean, responsive, and easy-to-use interface.

This project demonstrates the implementation of a complete Java desktop application with database connectivity, configuration management, authentication, modular architecture, and Windows deployment.

---

# 📌 Overview

Managing patient records manually becomes difficult as a clinic grows. This application provides a centralized system for handling clinic operations while maintaining a simple workflow for receptionists and healthcare staff.

The project focuses on writing clean, maintainable code while following a modular architecture that separates the user interface, database layer, utility classes, and business logic.

---

# ✨ Features

- Secure Login System
- MySQL Database Integration
- Automatic Database Initialization
- First-Time Database Configuration Wizard
- Persistent Database Configuration
- Patient Record Management
- Billing Module
- Modular Java Architecture
- Windows Installer Support
- Desktop Shortcut Integration

---

# 🛠 Tech Stack

| Technology | Purpose |
|------------|----------|
| Java 26 | Application Development |
| Java Swing | Desktop User Interface |
| Maven | Dependency & Build Management |
| MySQL | Database |
| JDBC | Database Connectivity |
| JPackage | Windows Installer Creation |

---

# 📂 Project Structure

```
SHC
│
├── src
│   └── shc
│       ├── db
│       │     Database Connection
│       │     Schema Initialization
│       │
│       ├── ui
│       │     Login
│       │     Configuration
│       │     Dashboard
│       │
│       ├── util
│       │     Configuration Manager
│       │
│       └── Main.java
│
├── dist
├── lib
├── target
├── run.bat
└── pom.xml
```

---

# 🧩 Architecture

The application follows a layered structure.

```
User Interface
        │
        ▼
Business Logic
        │
        ▼
Database Layer
        │
        ▼
MySQL
```

Configuration management is isolated from the database layer, making it easier to maintain and extend.

---

# ⚙ Configuration Management

Instead of storing database credentials inside the application, the project uses an external configuration file.

The application automatically creates and reads the configuration from the user's local application data directory.

Example location:

```
%LOCALAPPDATA%\Sufiyan Health Clinic\db.properties
```

This approach:

- avoids hardcoded credentials
- follows Windows best practices
- removes the need for administrator permissions
- allows configuration updates without rebuilding the application

---

# 🚀 Getting Started

## Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/SufiyanHealthClinic.git
```

---

## Navigate to the Project

```bash
cd SHC
```

---

## Build the Project

```bash
mvn clean package
```

---

## Run the Application

```bash
run.bat
```

---

# 🗄 Database Requirements

The application requires:

- MySQL Server 8+
- MySQL Connector/J

During the first launch, the application allows the user to configure:

- Host
- Port
- Database Name
- Username
- Password

The connection is validated before being saved.

---

# 💡 Design Goals

This project was built with the following objectives:

- Clean and readable code
- Modular architecture
- Easy maintenance
- Reusable components
- Separation of concerns
- Beginner-friendly project structure
- Production-ready Windows deployment

---

# 📈 Future Improvements

Planned enhancements include:

- Appointment Scheduling
- Prescription Management
- Inventory Management
- PDF Invoice Generation
- Backup & Restore
- User Roles & Permissions
- Dashboard Analytics
- Dark Mode
- Multi-language Support

---

# 🤝 Contributing

Contributions, suggestions, and improvements are always welcome.

If you'd like to improve this project:

1. Fork the repository
2. Create a new branch
3. Commit your changes
4. Open a Pull Request

---

# 👨‍💻 Developer

**Syed Sufiyan Ali**

Software Developer

Specializing in:

- Java Desktop Applications
- Database Systems
- Shopify Development
- Graphic Design
- Video Editing

GitHub:
https://github.com/YOUR_USERNAME
linkedin:
www.linkedin.com/in/syed-sufiyan-ali-2502ba397
---

# 📄 License

© 2026 Syed Sufiyan Ali

All Rights Reserved.

This software and its source code are the intellectual property of Syed Sufiyan Ali.

No part of this project may be copied, modified, distributed, or used for commercial purposes without prior written permission from the author.
