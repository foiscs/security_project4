package hyundai_4th.car_service.controller;

import hyundai_4th.car_service.model.dto.RentalDTO;
import hyundai_4th.car_service.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @GetMapping("/rental")
    public String rentalPage() {
        return "forward:/rental.html";
    }

    @PostMapping("/api/v1/rentals")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public RentalDTO.RentalResponse start(@RequestBody RentalDTO.RentalStartRequest req) {
        return rentalService.start(req);
    }

    @PatchMapping("/api/v1/rentals/{rentalId}")
    @ResponseBody
    public RentalDTO.RentalResponse finish(@PathVariable String rentalId,
                                           @RequestBody RentalDTO.RentalReturnRequest req) {
        req.setRentalId(rentalId);
        return rentalService.finish(rentalId, req);
    }
}
