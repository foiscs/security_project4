<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Spring4Shell Test Page</title>
    <style>
        body {
            font-family: Arial, sans-serif;
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
        .warning {
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 4px;
            color: #721c24;
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
            background-color: #dc3545;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        button:hover {
            background-color: #c82333;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="warning">
            <strong>⚠️ 위험:</strong> 이 엔드포인트는 Spring4Shell 취약점에 노출되어 있습니다!
        </div>

        <h1>Test Form (Vulnerable)</h1>

        <form:form method="POST" modelAttribute="testData">
            <div class="form-group">
                <label for="field1">Field 1:</label>
                <form:input path="field1" id="field1" />
            </div>

            <div class="form-group">
                <label for="field2">Field 2:</label>
                <form:input path="field2" id="field2" />
            </div>

            <button type="submit">Submit</button>
        </form:form>
    </div>
</body>
</html>
