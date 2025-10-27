<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Result</title>
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
        h1 {
            color: #333;
        }
        .result {
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
        <h1>Submission Result</h1>

        <div class="result">
            <p><strong>Name:</strong> ${greeting.name}</p>
            <p><strong>Message:</strong> ${greeting.message}</p>
        </div>

        <p><a href="${pageContext.request.contextPath}/greeting">← Back to Form</a></p>
    </div>
</body>
</html>
