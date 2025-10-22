package com.Booking_care.controller;

import java.util.List;
import java.time.LocalDate;
import com.Booking_care.domain.Clinic;
import com.Booking_care.domain.Doctor;
import com.Booking_care.domain.Patient;
import com.Booking_care.domain.Booking;
import com.Booking_care.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;
import com.Booking_care.util.annotation.ApiMessage;
import com.Booking_care.util.error.BusinessException;
import com.Booking_care.util.error.IdInvalidException;
import com.Booking_care.mapper.booking.BookingMapper;
import com.Booking_care.domain.enums.BookingStatusEnum;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.format.annotation.DateTimeFormat;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.domain.dto.BookingDTO.ResBookingDTO;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Booking_care.domain.dto.BookingDTO.BookingClinicCriteriaDTO;
import com.Booking_care.domain.dto.BookingDTO.BookingCriteriaDTO;
import com.Booking_care.domain.dto.BookingDTO.BookingDoctorCriteriaDTO;
import com.Booking_care.domain.dto.BookingDTO.CreateBookingDTO;
import com.Booking_care.domain.dto.BookingDTO.UpdateBookingDTO;
import org.springframework.security.access.prepost.PreAuthorize;

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
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ResBookingDTO> create(@RequestBody CreateBookingDTO req) {
        Booking b = this.bookingService.createBooking(req);
        return ResponseEntity.ok(BookingMapper.toResBookingDTO(b));
    }

    // Get all bookings
    @GetMapping("/bookings")
    @ApiMessage("Fetch all booking")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultPaginationDTO> getAll(Pageable pageable) {
        return ResponseEntity.ok(bookingService.fetchAllBooking(pageable));
    }

    // Get booking by id

    @GetMapping("/bookings/{id}")
    @ApiMessage("Fetch booking by id")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResBookingDTO> getById(@PathVariable Long id) {
        Booking booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(BookingMapper.toResBookingDTO(booking));
    }

    // Cancel booking
    @PutMapping("/bookings/{id}/cancel")
    @ApiMessage("Cancel a booking")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResBookingDTO> cancel(@PathVariable Long id) {
        Booking canceled = bookingService.cancelBooking(id);
        return ResponseEntity.ok(BookingMapper.toResBookingDTO(canceled));
    }

    // Update booking (time_id, clinic_id, AppointmentDate, Description)
    @PutMapping("/bookings")
    @ApiMessage("update a booking")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResBookingDTO> update(@RequestBody UpdateBookingDTO req)
            throws IdInvalidException, BusinessException {
        return ResponseEntity.ok(BookingMapper.toResBookingDTO(this.bookingService.updateBooking(req)));
    }

    // update status booking by id
    @PutMapping("/bookings/{id}/status")
    @ApiMessage("update status booking by id")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'SUPPORT')")
    public ResponseEntity<ResBookingDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam BookingStatusEnum status) throws IdInvalidException {

        Booking updated = bookingService.updateBookingStatus(id, status);
        return ResponseEntity.ok(BookingMapper.toResBookingDTO(updated));
    }

    // get booking by patient id
    @GetMapping("/bookings/patient/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResultPaginationDTO> getBookingsByPatientId(@PathVariable Long id,
            Pageable pageable) throws IdInvalidException {
        Patient p = new Patient();
        ResultPaginationDTO res = this.bookingService.fetchBookingByInstanceId(id, pageable, p);
        return ResponseEntity.ok(res);
    }

    // get booking by doctor id
    @GetMapping("/bookings/doctor/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ResultPaginationDTO> getBookingsByDoctorId(@PathVariable Long id,
            Pageable pageable) throws IdInvalidException {

        Doctor d = new Doctor();
        ResultPaginationDTO res = this.bookingService.fetchBookingByInstanceId(id, pageable, d);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/bookings/doctor/{id}/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ResultPaginationDTO> getBookingsByDoctorIdSearch(@PathVariable Long id,
            BookingDoctorCriteriaDTO bookingDoctorCriteriaDTO,
            Pageable pageable) throws IdInvalidException {
        bookingDoctorCriteriaDTO.setDoctorId(id);
        ResultPaginationDTO res = this.bookingService.fetchAllBookingDoctorSearch(pageable, bookingDoctorCriteriaDTO);
        return ResponseEntity.ok(res);
    }

    // get booking by clinic id
    @GetMapping("/bookings/clinic/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public ResponseEntity<ResultPaginationDTO> getBookingsByClinicId(@PathVariable Long id,
            Pageable pageable) throws IdInvalidException {

        Clinic d = new Clinic();
        ResultPaginationDTO res = this.bookingService.fetchBookingByInstanceId(id, pageable, d);
        return ResponseEntity.ok(res);
    }

    // get booking by clinic id and search
    @GetMapping("/bookings/clinic/{id}/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public ResponseEntity<ResultPaginationDTO> getBookingsByClinicIdSearch(@PathVariable Long id,
            Pageable pageable, BookingClinicCriteriaDTO bookingClinicCriteriaDTO) throws IdInvalidException {

        bookingClinicCriteriaDTO.setClinicId(id);
        ResultPaginationDTO res = this.bookingService.fetchAllBookingClinicSearch(pageable, bookingClinicCriteriaDTO);

        return ResponseEntity.ok(res);
    }

    // get availability time of doctor empty ( kiểm tra xem ngày này bác sĩ còn bao
    // nhiêu time trống )
    @GetMapping("/bookings/doctor/{doctorId}/available-times")
    public ResponseEntity<List<ResBookingDTO.ResTimeDTO>> getAvailableTimes(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appointmentDate) {

        return ResponseEntity.ok(bookingService.getAvailableTimes(doctorId, appointmentDate));
    }

    // Get all bookings
    @GetMapping("/bookings/search")
    @ApiMessage("Fetch all booking")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultPaginationDTO> getAllBookingSearch(Pageable pageable,
            BookingCriteriaDTO bookingCriteriaDTO) {
        return ResponseEntity.ok(bookingService.fetchAllBookingSearch(pageable, bookingCriteriaDTO));
    }

}
