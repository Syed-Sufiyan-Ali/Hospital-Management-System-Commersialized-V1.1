@echo off
echo ============================================
echo  Sufiyan Health Clinic — Compile
echo ============================================

REM Compile all Java source files
javac -cp "lib\mysql-connector.jar" -d out -sourcepath src ^
  src\shc\Main.java ^
  src\shc\db\DBConnection.java ^
  src\shc\db\Schema.java ^
  src\shc\db\DAO.java ^
  src\shc\util\Security.java ^
  src\shc\util\Theme.java ^
  src\shc\ui\components\UI.java ^
  src\shc\ui\LoginFrame.java ^
  src\shc\ui\MainFrame.java ^
  src\shc\ui\dialogs\PatientDialog.java ^
  src\shc\ui\dialogs\InvoiceDialog.java ^
  src\shc\ui\dialogs\ChangePasswordDialog.java ^
  src\shc\ui\panels\DashboardPanel.java ^
  src\shc\ui\panels\PatientsPanel.java ^
  src\shc\ui\panels\AppointmentsPanel.java ^
  src\shc\ui\panels\BillingPanel.java ^
  src\shc\ui\panels\MedicinePanel.java ^
  src\shc\ui\panels\UsersPanel.java

IF %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed. Check the error messages above.
    pause
    exit /b 1
)

echo.
echo [OK] Compiled successfully! Run run.bat to launch.
pause
