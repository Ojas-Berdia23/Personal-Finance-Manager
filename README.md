# Personal Finance Management System

## 🏦 Overview
A comprehensive Java console application for managing personal finances, tracking expenses, setting budgets, and monitoring savings goals.

## ✨ Features

### 💰 Transaction Management
- Add income and expense transactions
- Categorize transactions automatically
- View transaction history with filters
- Edit and track transaction details

### 📊 Budget Management
- Set monthly budgets for different categories
- Track spending against budget limits
- Get warnings when approaching budget limits
- Compare actual spending vs budgeted amounts

### 📈 Financial Reports & Analytics
- Monthly and yearly financial reports
- Category-wise spending breakdown
- Spending trends analysis
- Budget vs actual spending comparisons

### 🎯 Savings Goals
- Create and track multiple savings goals
- Monitor progress toward financial targets
- Calculate required monthly contributions
- Get goal achievement notifications

### 💾 Data Persistence
- Automatic data saving to CSV files
- Data backup and restore functionality
- Export transactions to external files
- Load previous data on startup

## 🛠️ Technical Stack
- **Language:** Java 8+
- **Data Storage:** CSV files
- **Architecture:** Object-Oriented Programming
- **Design Patterns:** MVC, Data Access Object

## 📁 Project Structure
```
PersonalFinanceManager/
├── src/
│   ├── FinanceManager.java      # Main application class
│   ├── Transaction.java         # Transaction data model
│   ├── Budget.java             # Budget management
│   ├── SavingsGoal.java        # Savings goals management
│   ├── Category.java           # Category management
│   ├── ReportGenerator.java    # Financial reports
│   └── DataManager.java        # File I/O operations
├── data/                       # Data storage directory
├── README.md
├── compile.bat                 # Windows compilation script
└── compile.sh                  # Unix/Linux compilation script
```

## 🚀 Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Command line access

### Installation & Running

#### Windows:
1. Double-click `compile.bat` to compile and run
2. Or manually:
```bash
cd PersonalFinanceManager
javac -d . src/*.java
java src.FinanceManager
```

#### Unix/Linux/Mac:
1. Run `./compile.sh` to compile and run
2. Or manually:
```bash
cd PersonalFinanceManager
javac -d . src/*.java
java src.FinanceManager
```

## 💡 Usage Examples

### Adding a Transaction
1. Select "Add Transaction" from main menu
2. Choose Income or Expense
3. Enter amount, select category, add description
4. Optionally specify date (defaults to today)

### Setting Up Budget
1. Go to "Budget Management"
2. Select "Set Category Budget"
3. Choose category and enter monthly budget amount
4. System will track spending against this budget

### Creating Savings Goal
1. Navigate to "Savings Goals"
2. Select "Create New Goal"
3. Enter goal name, target amount, and target date
4. Track progress by adding savings regularly

## 📊 Sample Data Categories

### Income Categories:
- Salary
- Freelance
- Business
- Investments
- Gifts
- Other Income

### Expense Categories:
- Food & Dining
- Transportation
- Shopping
- Entertainment
- Bills & Utilities
- Healthcare
- Education
- Travel
- Insurance
- Miscellaneous

## 🎯 Key Programming Concepts Demonstrated

- **Object-Oriented Programming:** Classes, inheritance, encapsulation
- **Collections Framework:** ArrayList, HashMap, Streams
- **File I/O:** Reading/writing CSV files, exception handling
- **Date/Time API:** LocalDate, YearMonth manipulation
- **Data Validation:** Input validation and error handling
- **Menu-Driven Interface:** User interaction design
- **Data Persistence:** CSV-based storage system

## 📈 Resume Highlights

This project demonstrates:
- ✅ Real-world problem solving
- ✅ Full application development lifecycle
- ✅ Data management and persistence
- ✅ User experience design
- ✅ Error handling and validation
- ✅ Object-oriented design principles
- ✅ Business logic implementation
- ✅ File system operations

## 🔧 Future Enhancements
- GUI interface using JavaFX or Swing
- Database integration (MySQL/PostgreSQL)
- Data visualization with charts
- Multi-user support
- Mobile app integration
- Expense receipt scanning
- Investment tracking
- Bill reminders

## 🤝 Contributing
This is a portfolio project. Feel free to fork and create your own enhancements!

## 📄 License
This project is created for educational and portfolio purposes.

---
**Created by:** [Your Name]  
**Contact:** [Your Email]  
**Portfolio:** [Your Portfolio URL]
