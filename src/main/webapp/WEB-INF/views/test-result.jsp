<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Test Result</title>
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
        .result {
            background-color: #d1ecf1;
            border: 1px solid #bee5eb;
            padding: 15px;
            margin: 20px 0;
            border-radius: 4px;
            color: #0c5460;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Test Submission Result</h1>

        <div class="result">
            <p><strong>Field 1:</strong> ${testData.field1}</p>
            <p><strong>Field 2:</strong> ${testData.field2}</p>
        </div>

        <p><a href="${pageContext.request.contextPath}/test">← Back to Test Form</a></p>
    </div>
</body>
</html>
