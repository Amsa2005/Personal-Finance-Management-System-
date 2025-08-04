# 💰 Personal Finance Management System

A Java-based desktop application to manage personal income, expenses, and monthly budgets using a MySQL database and Java Swing UI.

---

## 🧱 Database Setup

### Step 1: Create the Database

```sql
CREATE DATABASE IF NOT EXISTS moneymanager;
USE moneymanager;
```

### Step 2: Create the transactions Table

```sql
CREATE TABLE IF NOT EXISTS transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type ENUM('Income', 'Expense') NOT NULL,
    category VARCHAR(100) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Step 3: Create the budget Table (Optional)

```sql
CREATE TABLE IF NOT EXISTS budget (
    id INT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(10, 2) NOT NULL,
    month_year VARCHAR(20) NOT NULL UNIQUE, -- Example: 'July-2025'
    date_set TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 🚀 How to Run

### ✅ Prerequisites
1. Java JDK installed  
2. MySQL Server running  
3. MySQL Connector JAR added to classpath  

---

### 🧩 Steps to Run

1. **Clone the Repository**  
```bash
git clone https://github.com/amsa2005/Personal-Finance-Management-System-.git
```

2. **Set Up the Database**  
Open MySQL and run the SQL commands provided in the **Database Setup** section.

3. **Update DB Credentials in `dbconnection.java`**  
```java
String url = "jdbc:mysql://localhost:3306/moneymanager";
String username = "your_mysql_username";
String password = "your_mysql_password";
```

4. **Compile and Run the App**  
```bash
javac -cp ".;mysql-connector-j-9.4.0.jar" money.java
java -cp ".;mysql-connector-j-9.4.0.jar" money.java
```

---

## 👤 Author

- Amsa Sankar  
- GitHub: [@amsa2005](https://github.com/amsa2005)

---
📸 Screenshots
<img width="1228" height="643" alt="dbms-2" src="https://github.com/user-attachments/assets/94991741-81a6-4b8d-aab8-8bf00b29f988" />
<img width="1027" height="339" alt="Screenshot 2025-07-31 011339" src="https://github.com/user-attachments/assets/6dbb25d4-106c-47e8-b62a-2fde92d42e95" />
<img width="1517" height="972" alt="output" src="https://github.com/user-attachments/assets/d0b8260c-36f1-43f8-ba6e-acfe9c5f801f" />







