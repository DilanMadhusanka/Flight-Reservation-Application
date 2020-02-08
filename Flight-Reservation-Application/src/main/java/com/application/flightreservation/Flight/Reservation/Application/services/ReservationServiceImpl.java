package com.application.flightreservation.Flight.Reservation.Application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.flightreservation.Flight.Reservation.Application.dto.ReservationRequest;
import com.application.flightreservation.Flight.Reservation.Application.entities.Flight;
import com.application.flightreservation.Flight.Reservation.Application.entities.Passenger;
import com.application.flightreservation.Flight.Reservation.Application.entities.Reservation;
import com.application.flightreservation.Flight.Reservation.Application.repo.FlightRepository;
import com.application.flightreservation.Flight.Reservation.Application.repo.PassengerRepository;
import com.application.flightreservation.Flight.Reservation.Application.repo.ReservationRepository;
import com.application.flightreservation.Flight.Reservation.Application.util.EmailUtil;
import com.application.flightreservation.Flight.Reservation.Application.util.PDFGenerator;

@Service
public class ReservationServiceImpl implements ReservationService {

	@Autowired
	FlightRepository flightRepository;
	
	@Autowired
	PassengerRepository passengerRepository;
	
	@Autowired
	ReservationRepository reservationRepository;
	
	@Autowired
	PDFGenerator pdfGenerator;
	
	@Autowired
	EmailUtil emailUtil;
	
	@Override
	public Reservation bookFlight(ReservationRequest request) {
		Long flightId = request.getFlightId();
		Flight flight = flightRepository.findById(flightId).get();
		
		Passenger passenger = new Passenger();
		passenger.setFirstName(request.getPassengerFirstName());
		passenger.setLastName(request.getPassengerLastName());
		passenger.setPhone(request.getpassengerPhone());
		passenger.setEmail(request.getPassengerEmail());
		Passenger savedPassenger = passengerRepository.save(passenger);
		
		Reservation reservation = new Reservation();
		reservation.setFlight(flight);
		reservation.setPassenger(savedPassenger);
		reservation.setCheckedIn(false);
		
		Reservation savedReservation = reservationRepository.save(reservation);
		
		String filePath = "F:\\Spring(Java) Pactical\\Flight-Reservation-Application\\document\\reservation"+savedReservation.getId()+".pdf";
		pdfGenerator.generateItinerary(savedReservation, filePath);
		
		emailUtil.sendItinerary(passenger.getEmail(), filePath);
		
		return savedReservation;
	}

}
