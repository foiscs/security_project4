<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>차량 검색 - Spring4Shell Vulnerable</title>
    <style>
        body {
            font-family: 'Malgun Gothic', Arial, sans-serif;
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            background-color: white;
            padding: 30px;
            border-radius: 5px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            border-bottom: 2px solid #007bff;
            padding-bottom: 10px;
        }
        .warning {
            background-color: #fff3cd;
            border: 1px solid #ffc107;
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 4px;
            color: #856404;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        input[type="text"] {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        button {
            background-color: #007bff;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        button:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="warning">
            <strong>⚠️ 경고:</strong> 이 페이지는 Spring4Shell (CVE-2022-22965) 취약점 테스트용입니다.
        </div>

        <h1>🔍 차량 검색</h1>

        <form method="GET">
            <div class="form-group">
                <label for="pickupLocationId">픽업 위치 ID:</label>
                <input id="pickupLocationId" name="pickupLocationId" type="text"/>
            </div>

            <div class="form-group">
                <label for="brand">브랜드:</label>
                <input id="brand" name="brand" type="text" placeholder="예: 현대, 기아"/>
            </div>

            <div class="form-group">
                <label for="model">모델:</label>
                <input id="model" name="model" type="text" placeholder="예: 아이오닉5, EV6"/>
            </div>

            <div class="form-group">
                <label for="availableFrom">사용 가능 시작 시간:</label>
                <input id="availableFrom" name="availableFrom" type="text" placeholder="2024-01-01T10:00:00Z"/>
            </div>

            <div class="form-group">
                <label for="availableTo">사용 가능 종료 시간:</label>
                <input id="availableTo" name="availableTo" type="text" placeholder="2024-01-02T10:00:00Z"/>
            </div>

            <button type="submit">검색</button>
        </form>

        <hr style="margin: 30px 0;">

        <p>
            <a href="${pageContext.request.contextPath}/api/v1/rentals/start">렌탈 시작하기</a> |
            <a href="${pageContext.request.contextPath}/greeting">기본 테스트 페이지</a>
        </p>
    </div>
</body>
</html>
