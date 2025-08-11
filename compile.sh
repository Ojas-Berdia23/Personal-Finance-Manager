#!/bin/bash

echo
echo "========================================"
echo "  Personal Finance Manager - Setup"  
echo "========================================"
echo

echo "Compiling Java files..."
javac -d . src/*.java

if [ $? -ne 0 ]; then
    echo
    echo "❌ Compilation failed! Please check for errors."
    echo "Make sure you have Java JDK installed and in PATH."
    exit 1
fi

echo
echo "✅ Compilation successful!"
echo
echo "Starting Personal Finance Manager..."
echo

java src.FinanceManager

echo
echo "Thanks for using Personal Finance Manager!"
