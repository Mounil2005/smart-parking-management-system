@echo off
echo Compiling Smart Parking System...
javac -cp ".;sqlite-jdbc-3.46.1.3.jar;slf4j-api-2.0.9.jar;slf4j-simple-2.0.9.jar" DatabaseManager.java DataModels.java UIComponents.java LoginPanel.java AdminDashboard.java UserDashboard.java ParkingSystemMain.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo Running application...
    java -cp ".;sqlite-jdbc-3.46.1.3.jar;slf4j-api-2.0.9.jar;slf4j-simple-2.0.9.jar" ParkingSystemMain
) else (
    echo Compilation failed!
    pause
)
