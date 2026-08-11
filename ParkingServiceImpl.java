package com.example.parking.service.impl;

import com.example.parking.dto.request.TicketRequest;
import com.example.parking.dto.response.TicketResponse;
import com.example.parking.entity.ParkingTicket;
import com.example.parking.entity.Vehicle;
import com.example.parking.entity.Zone;
import com.example.parking.repository.ParkingTicketRepository;
import com.example.parking.repository.VehicleRepository;
import com.example.parking.repository.ZoneRepository;
import com.example.parking.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ParkingServiceImpl implements ParkingService {

    private final ParkingTicketRepository ticketRepository;
    private final VehicleRepository vehicleRepository;
    private final ZoneRepository zoneRepository;

    @Override
    @Transactional
    public TicketResponse checkIn(TicketRequest req) {
        // 1. Kiểm tra Vehicle có tồn tại không
        Vehicle vehicle = vehicleRepository.findById(req.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + req.getVehicleId()));

        // 2. Kiểm tra Zone có tồn tại không
        Zone zone = zoneRepository.findById(req.getZoneId())
                .orElseThrow(() -> new RuntimeException("Zone not found with id: " + req.getZoneId()));

        // 3. Kiểm tra Zone còn trống chỗ không
        if (zone.getOccupiedSpots() >= zone.getCapacity()) {
            throw new RuntimeException("Zone " + zone.getName() + " is full");
        }

        // 4. Tạo mới ParkingTicket
        ParkingTicket ticket = ParkingTicket.builder()
                .vehicle(vehicle)
                .zone(zone)
                .checkInTime(LocalDateTime.now())
                .build();

        // 5. Cập nhật số chỗ đang đỗ trong Zone
        zone.setOccupiedSpots(zone.getOccupiedSpots() + 1);
        zoneRepository.save(zone);

        // 6. Lưu vé gửi xe
        ParkingTicket savedTicket = ticketRepository.save(ticket);

        return TicketResponse.builder()
                .id(savedTicket.getId())
                .licensePlate(vehicle.getLicensePlate())
                .vehicleType(vehicle.getType())
                .zoneName(zone.getName())
                .checkInTime(savedTicket.getCheckInTime())
                .checkOutTime(savedTicket.getCheckOutTime())
                .build();
    }

    @Override
    @Transactional
    public TicketResponse checkOut(Long vehicleId) {
        // 1. Tìm vé chưa check-out gần nhất của xe
        ParkingTicket ticket = ticketRepository.findFirstByVehicleIdAndCheckOutTimeIsNullOrderByIdDesc(vehicleId)
                .orElseThrow(() -> new RuntimeException("No active parking ticket found for vehicle id: " + vehicleId));

        // 2. Cập nhật thời gian check-out
        ticket.setCheckOutTime(LocalDateTime.now());
        ParkingTicket updatedTicket = ticketRepository.save(ticket);

        // 3. Cập nhật lại số chỗ đang đỗ trong Zone
        Zone zone = ticket.getZone();
        if (zone.getOccupiedSpots() > 0) {
            zone.setOccupiedSpots(zone.getOccupiedSpots() - 1);
            zoneRepository.save(zone);
        }

        // 4. Trả về kết quả
        return TicketResponse.builder()
                .id(updatedTicket.getId())
                .licensePlate(ticket.getVehicle().getLicensePlate())
                .vehicleType(ticket.getVehicle().getType())
                .zoneName(zone.getName())
                .checkInTime(updatedTicket.getCheckInTime())
                .checkOutTime(updatedTicket.getCheckOutTime())
                .build();
    }
}
