start BuildAndStart.bat
timeout /t 65
newman run postman/ewm-stat-service.json & start newman run postman/ewm-main-service.json & start newman run postman/feature.json