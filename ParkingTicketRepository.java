package com.example.parking.repository;

import com.example.parking.entity.ParkingTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParkingTicketRepository extends JpaRepository<ParkingTicket, Long> {

    // Tìm vé chưa check-out gần nhất của xe
    Optional<ParkingTicket> findFirstByVehicleIdAndCheckOutTimeIsNullOrderByIdDesc(Long vehicleId);
}
