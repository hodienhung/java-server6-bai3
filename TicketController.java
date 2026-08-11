package com.example.parking.controller;

import com.example.parking.dto.request.TicketRequest;
import com.example.parking.dto.response.ApiResponse;
import com.example.parking.dto.response.TicketResponse;
import com.example.parking.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final ParkingService parkingService;

    @PostMapping("/check-in")
    public ResponseEntity<ApiResponse<TicketResponse>> checkIn(@RequestBody TicketRequest request) {
        TicketResponse data = parkingService.checkIn(request);

        ApiResponse<TicketResponse> response = ApiResponse.<TicketResponse>builder()
                .success(true)
                .message("Checked in successfully")
                .data(data)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/check-out/{vehicleId}")
    public ResponseEntity<ApiResponse<TicketResponse>> checkOut(@PathVariable Long vehicleId) {
        TicketResponse data = parkingService.checkOut(vehicleId);

        ApiResponse<TicketResponse> response = ApiResponse.<TicketResponse>builder()
                .success(true)
                .message("Checked out successfully")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }
}
