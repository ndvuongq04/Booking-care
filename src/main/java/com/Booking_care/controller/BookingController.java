package com.Booking_care.controller;

import com.Booking_care.domain.Booking;
import com.Booking_care.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;

import com.Booking_care.util.annotation.ApiMessage;
import com.Booking_care.util.error.BusinessException;
import com.Booking_care.util.error.IdInvalidException;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.Booking_care.domain.response.ResultPaginationDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Booking_care.domain.dto.BookingDTO.CreateBookingDTO;
import com.Booking_care.domain.dto.BookingDTO.UpdateBookingDTO;

@RestController
@RequestMapping("/api/v1")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Create booking
    @PostMapping("/bookings")
    @ApiMessage("Create new booking")
    public ResponseEntity<Booking> create(@RequestBody CreateBookingDTO req)
            throws IdInvalidException, BusinessException {
        return ResponseEntity.ok(this.bookingService.createBooking(req));
    }

    // Get all bookings
    @GetMapping("/bookings")
    @ApiMessage("Fetch all booking")
    public ResponseEntity<ResultPaginationDTO> getAll(Pageable pageable) {
        return ResponseEntity.ok(bookingService.fetchAllBooking(pageable));
    }

    // Get booking by id

    @GetMapping("/bookings/{id}")
    @ApiMessage("Fetch booking by id")
    public ResponseEntity<Booking> getById(@PathVariable Long id) throws IdInvalidException {
        Booking booking = bookingService.getBookingById(id);
        if (booking == null) {
            throw new IdInvalidException("Booking với id " + id + " không tồn tại");
        }
        return ResponseEntity.ok(booking);
    }

    // Cancel booking
    @PutMapping("/bookings/{id}/cancel")
    @ApiMessage("Cancel a booking")
    public ResponseEntity<Booking> cancel(@PathVariable Long id) throws IdInvalidException {
        Booking canceled = bookingService.cancelBooking(id);
        if (canceled == null) {
            throw new IdInvalidException("Booking với id " + id + " không tồn tại");
        }
        return ResponseEntity.ok(canceled);
    }

    // Update booking (time_id, clinic_id, AppointmentDate, Description)
    @PutMapping("/bookings")
    @ApiMessage("update a booking")
    public ResponseEntity<Booking> update(@RequestBody UpdateBookingDTO req)
            throws IdInvalidException, BusinessException {
        return ResponseEntity.ok(this.bookingService.updateBooking(req));
    }

    // update status booking by id
    // get booking by patient id
    // get booking by doctor id
    // get booking by clinic id
    // get booing by doctor and appointmentDate
    // Check availability ( kiểm tra bác sĩ + ngày + time có tồn tại không ) ( giống
    // với dưới thì phải )
    // get availability time of doctor empty ( kiểm tra xem ngày này bác sĩ còn bao
    // nhiêu time trống )

}
