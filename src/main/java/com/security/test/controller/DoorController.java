package com.security.test.controller;

import com.security.test.model.dto.DoorCommandRequest;
import com.security.test.model.dto.DoorStatusResponse;
import com.security.test.service.DoorService;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@Service
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
