package com.Booking_care.controller;

import java.util.List;
import java.time.Instant;
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

import com.Booking_care.domain.dto.BookingDTO.BookingCriteriaDTO;
import com.Booking_care.domain.dto.BookingDTO.BookingDoctorCriteriaDTO;
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
    public ResponseEntity<ResBookingDTO> create(@RequestBody CreateBookingDTO req)
            throws IdInvalidException, BusinessException {

        Booking b = this.bookingService.createBooking(req);
        return ResponseEntity.ok(this.bookingService.convertToBookingDTO(b));
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
    public ResponseEntity<ResBookingDTO> getById(@PathVariable Long id) throws IdInvalidException {
        Booking booking = bookingService.getBookingById(id);
        if (booking == null) {
            throw new IdInvalidException("Booking với id " + id + " không tồn tại");
        }
        return ResponseEntity.ok(this.bookingService.convertToBookingDTO(booking));
    }

    // Cancel booking
    @PutMapping("/bookings/{id}/cancel")
    @ApiMessage("Cancel a booking")
    public ResponseEntity<ResBookingDTO> cancel(@PathVariable Long id) throws IdInvalidException {
        Booking canceled = bookingService.cancelBooking(id);
        if (canceled == null) {
            throw new IdInvalidException("Booking với id " + id + " không tồn tại");
        }
        return ResponseEntity.ok(this.bookingService.convertToBookingDTO(canceled));
    }

    // Update booking (time_id, clinic_id, AppointmentDate, Description)
    @PutMapping("/bookings")
    @ApiMessage("update a booking")
    public ResponseEntity<ResBookingDTO> update(@RequestBody UpdateBookingDTO req)
            throws IdInvalidException, BusinessException {
        return ResponseEntity.ok(this.bookingService.convertToBookingDTO(this.bookingService.updateBooking(req)));
    }

    // update status booking by id
    @PutMapping("/bookings/{id}/status")
    @ApiMessage("update status booking by id")
    public ResponseEntity<ResBookingDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam BookingStatusEnum status) throws IdInvalidException {

        Booking updated = bookingService.updateBookingStatus(id, status);
        if (updated == null) {
            throw new IdInvalidException("Id : " + id + " không tồn tại");
        }
        return ResponseEntity.ok(this.bookingService.convertToBookingDTO(updated));
    }

    // get booking by patient id
    @GetMapping("/bookings/patient/{id}")
    public ResponseEntity<ResultPaginationDTO> getBookingsByPatientId(@PathVariable Long id,
            Pageable pageable) throws IdInvalidException {
        Patient p = new Patient();
        ResultPaginationDTO res = this.bookingService.fetchBookingByInstanceId(id, pageable, p);

        if (res.getResult() instanceof List<?> list && list.isEmpty()) {
            throw new IdInvalidException("Id : " + id + " không tồn tại hoặc chưa có booking nào");
        }

        return ResponseEntity.ok(res);
    }

    // get booking by doctor id
    @GetMapping("/bookings/doctor/{id}")
    public ResponseEntity<ResultPaginationDTO> getBookingsByDoctorId(@PathVariable Long id,
            Pageable pageable) throws IdInvalidException {

        Doctor d = new Doctor();
        ResultPaginationDTO res = this.bookingService.fetchBookingByInstanceId(id, pageable, d);

        if (res.getResult() instanceof List<?> list && list.isEmpty()) {
            throw new IdInvalidException("Id : " + id + " không tồn tại hoặc chưa có booking nào");
        }

        return ResponseEntity.ok(res);
    }

    @GetMapping("/bookings/doctor/{id}/search")
    public ResponseEntity<ResultPaginationDTO> getBookingsByDoctorIdSearch(@PathVariable Long id,
            BookingDoctorCriteriaDTO bookingDoctorCriteriaDTO,
            Pageable pageable) throws IdInvalidException {
        bookingDoctorCriteriaDTO.setDoctorId(id);
        ResultPaginationDTO res = this.bookingService.fetchAllBookingDoctorSearch(pageable, bookingDoctorCriteriaDTO);
        return ResponseEntity.ok(res);
    }

    // get booking by clinic id
    @GetMapping("/bookings/clinic/{id}")
    public ResponseEntity<ResultPaginationDTO> getBookingsByClinicId(@PathVariable Long id,
            Pageable pageable) throws IdInvalidException {

        Clinic d = new Clinic();
        ResultPaginationDTO res = this.bookingService.fetchBookingByInstanceId(id, pageable, d);

        if (res.getResult() instanceof List<?> list && list.isEmpty()) {
            throw new IdInvalidException("Id : " + id + " không tồn tại hoặc chưa có booking nào");
        }

        return ResponseEntity.ok(res);
    }

    // get availability time of doctor empty ( kiểm tra xem ngày này bác sĩ còn bao
    // nhiêu time trống )
    @GetMapping("/bookings/doctor/{doctorId}/available-times")
    public ResponseEntity<List<ResBookingDTO.ResTimeDTO>> getAvailableTimes(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Instant appointmentDate) {

        return ResponseEntity.ok(bookingService.getAvailableTimes(doctorId, appointmentDate));
    }

    // Get all bookings
    @GetMapping("/bookings/search")
    @ApiMessage("Fetch all booking")
    public ResponseEntity<ResultPaginationDTO> getAllBookingSearch(Pageable pageable,
            BookingCriteriaDTO bookingCriteriaDTO) {
        return ResponseEntity.ok(bookingService.fetchAllBookingSearch(pageable, bookingCriteriaDTO));
    }

    // get booing by doctor and appointmentDate => sẽ dùng specification

}
