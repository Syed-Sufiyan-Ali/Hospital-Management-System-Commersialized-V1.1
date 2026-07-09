# 🏥 Sufiyan Health Clinic Management System (Version 1.1)

A desktop-based Clinic Management System developed using **Java Swing** and **MySQL**, designed to simplify day-to-day clinic operations through a clean, responsive, and easy-to-use interface.

This project demonstrates the implementation of a complete Java desktop application with database connectivity, configuration management, authentication, modular architecture, and Windows deployment.

---

# 📌 Overview

Managing patient records manually becomes difficult as a clinic grows. This application provides a centralized system for handling clinic operations while maintaining a simple workflow for receptionists and healthcare staff.

The project focuses on writing clean, maintainable code while following a modular architecture that separates the user interface, database layer, utility classes, and business logic.

---

# ✨ Features

* Secure Login System
* MySQL Database Integration
* Automatic Database Creation *(New in V1.1)*
* Automatic Database Initialization
* First-Time Database Configuration Wizard
* Persistent Database Configuration
* Patient Record Management
* Billing Module
* Modular Java Architecture
* Windows Installer Support
* Desktop Shortcut Integration

---

# 🛠 Tech Stack

| Technology | Purpose                       |
| ---------- | ----------------------------- |
| Java 26    | Application Development       |
| Java Swing | Desktop User Interface        |
| Maven      | Dependency & Build Management |
| MySQL      | Database                      |
| JDBC       | Database Connectivity         |
| JPackage   | Windows Installer Creation    |

---

# 📂 Project Structure

```text
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

```text
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

```text
%LOCALAPPDATA%\Sufiyan Health Clinic\db.properties
```

This approach:

* avoids hardcoded credentials
* follows Windows best practices
* removes the need for administrator permissions
* allows configuration updates without rebuilding the application

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

or launch the generated Windows installer created using **JPackage**.

---

# 🗄 Database Setup (Updated in Version 1.1)

The application requires:

* MySQL Server 8+
* MySQL Connector/J

**Version 1.1 introduces a much simpler setup process.**

Unlike **Version 1.0**, users no longer need to manually create a MySQL database before launching the application.

During the first launch, the Configuration Window will guide the user through the initial setup by requesting:

* Host
* Port
* Database Name
* Username
* Password

After validating the connection, the application will automatically:

* Create the database if it does not already exist.
* Initialize all required tables.
* Save the database configuration for future launches.

Once the setup is complete, the application is ready to use immediately without any additional database configuration.

---

# 🆕 Version 1.1 Update

### What's New

* Automatic database creation during first-time setup.
* Improved Configuration Window.
* Automatic database schema initialization.
* Simplified installation process.
* No manual database creation required.
* Better first-time user experience.

### Changes from Version 1.0

| Version 1.0                                                         | Version 1.1                                                                             |
| ------------------------------------------------------------------- | --------------------------------------------------------------------------------------- |
| Database had to be created manually before running the application. | Database can be created directly from the Configuration Window during first-time setup. |
| Manual setup before first launch.                                   | Guided setup with automatic database initialization.                                    |
| More installation steps.                                            | Faster and more beginner-friendly installation.                                         |

---

# 💡 Design Goals

This project was built with the following objectives:

* Clean and readable code
* Modular architecture
* Easy maintenance
* Reusable components
* Separation of concerns
* Beginner-friendly project structure
* Production-ready Windows deployment

---

# 📈 Future Improvements

Planned enhancements include:

* Appointment Scheduling
* Prescription Management
* Inventory Management
* PDF Invoice Generation
* Backup & Restore
* User Roles & Permissions
* Dashboard Analytics
* Dark Mode
* Multi-language Support

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

* Java Desktop Applications
* Database Systems
* Shopify Development
* Graphic Design
* Video Editing

**GitHub:**

https://github.com/Syed-Sufiyan-Ali

**LinkedIn:**

https://www.linkedin.com/in/syed-sufiyan-ali-2502ba397

---

# 📄 License

© 2026 Syed Sufiyan Ali

All Rights Reserved.

This software and its source code are the intellectual property of Syed Sufiyan Ali.

No part of this project may be copied, modified, distributed, or used for commercial purposes without prior written permission from the author.
