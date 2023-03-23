start BuildAndStart.bat
timeout /t 65
newman run Postman/ewm-stat-service.json && start newman run Postman/ewm-main-service.json