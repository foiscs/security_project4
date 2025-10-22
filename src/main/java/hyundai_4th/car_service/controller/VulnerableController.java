package hyundai_4th.car_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/vulnerable")
public class VulnerableController {

    // 취약점 1: POJO 기반 데이터 바인딩
    @PostMapping("/exploit")
    @ResponseBody
    public String exploit(VulnerableBean bean) {
        return "Data received: " + bean.getName();
    }

    // 테스트용 GET 엔드포인트
    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "Vulnerable endpoint is ready for Spring4Shell testing";
    }

    // 취약한 Bean 클래스
    public static class VulnerableBean {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}