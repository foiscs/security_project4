<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>렌탈 처리 완료</title>
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
            color: #28a745;
        }
        .info {
            background-color: #d4edda;
            border: 1px solid #c3e6cb;
            padding: 15px;
            margin: 20px 0;
            border-radius: 4px;
            color: #155724;
        }
        a {
            color: #007bff;
            text-decoration: none;
        }
        a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>✅ 렌탈 처리 완료</h1>

        <div class="info">
            <p><strong>${message}</strong></p>
        </div>

        <p>
            <a href="${pageContext.request.contextPath}/api/v1/rentals/start">← 렌탈 시작 폼으로 돌아가기</a><br/>
            <a href="${pageContext.request.contextPath}/api/v1/rentals/return">차량 반납하기 →</a><br/>
            <a href="${pageContext.request.contextPath}/api/v1/rentals/search">차량 검색하기 →</a>
        </p>
    </div>
</body>
</html>
