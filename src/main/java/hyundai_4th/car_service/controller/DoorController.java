package hyundai_4th.car_service.controller;

import hyundai_4th.car_service.model.dto.DoorCommandRequest;
import hyundai_4th.car_service.model.dto.DoorStatusResponse;
import hyundai_4th.car_service.service.DoorService;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vehicles")
public class DoorController {

    private final DoorService doorService;
    public DoorController(DoorService doorService) { this.doorService = doorService; }

    @GetMapping("/{vehicleId}/door")
    public DoorStatusResponse getDoorStatus(@PathVariable String vehicleId) {
        return doorService.getStatus(vehicleId);
    }

    @PostMapping(value="/{vehicleId}/door",
            consumes=MediaType.APPLICATION_JSON_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public DoorStatusResponse commandDoor(@PathVariable String vehicleId,
                                          @Valid @RequestBody DoorCommandRequest req) {
        boolean open = "OPEN".equalsIgnoreCase(req.getAction());
        return doorService.setOpen(vehicleId, open);
    }
}
