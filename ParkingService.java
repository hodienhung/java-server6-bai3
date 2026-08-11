package com.example.parking.service;

import com.example.parking.dto.request.TicketRequest;
import com.example.parking.dto.response.TicketResponse;

public interface ParkingService {
    TicketResponse checkIn(TicketRequest req);
    TicketResponse checkOut(Long vehicleId);
}
