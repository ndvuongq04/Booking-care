package com.Booking_care.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.Booking_care.domain.Booking;
import com.Booking_care.domain.Clinic;
import com.Booking_care.domain.Doctor;
import com.Booking_care.domain.Patient;
import com.Booking_care.domain.Time;
import com.Booking_care.domain.dto.BookingDTO.CreateBookingDTO;
import com.Booking_care.domain.dto.BookingDTO.ResBookingDTO;
import com.Booking_care.domain.dto.BookingDTO.UpdateBookingDTO;
import com.Booking_care.domain.enums.BookingStatusEnum;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.BookingRepository;
import com.Booking_care.util.error.BusinessException;
import com.Booking_care.util.error.IdInvalidException;

@Service
public class BookingService {

    private final AccountService accountService;
    private final BookingRepository bookingRepository;
    private final TimeService timeService;
    private final DoctorService doctorService;
    private final ClinicService clinicService;
    private final PatientService patientService;

    public BookingService(BookingRepository bookingRepository,
            TimeService timeService,
            DoctorService doctorService,
            ClinicService clinicService,
            AccountService accountService,
            PatientService patientService) {
        this.bookingRepository = bookingRepository;
        this.timeService = timeService;
        this.doctorService = doctorService;
        this.clinicService = clinicService;
        this.accountService = accountService;
        this.patientService = patientService;

    }

    public Booking createBooking(Booking b) {
        return this.bookingRepository.save(b);
    }

    public ResultPaginationDTO fetchAllBooking(Pageable pageable) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Booking> page = this.bookingRepository.findAll(pageable);

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        // convert
        List<ResBookingDTO> listBooking = page.getContent().stream()
                .map(item -> this.convertToBookingDTO(item))
                .collect(Collectors.toList());

        res.setResult(listBooking);
        res.setMeta(meta);

        return res;
    }

    public Booking getBookingById(long id) {
        return this.bookingRepository.findById(id).orElse(null);
    }

    public Booking createBooking(CreateBookingDTO dto) throws IdInvalidException, BusinessException {
        Doctor doctor = doctorService.fetchDoctorById(dto.getDoctorId());
        if (doctor == null)
            throw new IdInvalidException("Doctor với id " + dto.getDoctorId() + " không tồn tại");

        Clinic clinic = clinicService.fetchClinicById(dto.getClinicId());
        if (clinic == null)
            throw new IdInvalidException("Clinic với id " + dto.getClinicId() + " không tồn tại");

        Time time = timeService.fetchTimeById(dto.getTimeId());
        if (time == null)
            throw new IdInvalidException("Time với id " + dto.getTimeId() + " không tồn tại");

        Patient patient = patientService.fetchPatientById(dto.getPatientId());
        if (patient == null)
            throw new IdInvalidException("Patient với id " + dto.getPatientId() + " không tồn tại");

        // Check ngày hợp lệ
        this.validateBookingDate(dto.getAppointmentDate(), time);

        // Check bác sĩ đã có lịch
        boolean doctorBusy = bookingRepository.existsByDoctorIdAndAppointmentDateAndTimeIdAndStatusNot(
                doctor.getId(),
                dto.getAppointmentDate(),
                time.getId(),
                BookingStatusEnum.CANCELLED);

        if (doctorBusy) {
            throw new BusinessException("Bác sĩ đã có lịch tại khung giờ này");
        }

        // Check bệnh nhân đã có lịch
        boolean patientBusy = bookingRepository.existsByPatientIdAndAppointmentDateAndTimeIdAndStatusNot(
                patient.getId(),
                dto.getAppointmentDate(),
                time.getId(),
                BookingStatusEnum.CANCELLED);

        if (patientBusy) {
            throw new BusinessException("Bệnh nhân đã có lịch tại khung giờ này");
        }

        Booking booking = new Booking();
        booking.setAppointmentDate(dto.getAppointmentDate());
        booking.setDescription(dto.getDescription());
        booking.setStatus(BookingStatusEnum.PENDING);
        booking.setDoctor(doctor);
        booking.setClinic(clinic);
        booking.setTime(time);
        booking.setPatient(patient);

        return bookingRepository.save(booking);
    }

    public Booking updateBooking(UpdateBookingDTO dto) throws IdInvalidException, BusinessException {
        Booking existing = bookingRepository.findById(dto.getId()).orElse(null);
        if (existing == null)
            throw new IdInvalidException("Booking với id " + dto.getId() + " không tồn tại");

        Clinic clinic = clinicService.fetchClinicById(dto.getClinicId());
        if (clinic == null)
            throw new IdInvalidException("Clinic với id " + dto.getClinicId() + " không tồn tại");

        Time time = timeService.fetchTimeById(dto.getTimeId());
        if (time == null)
            throw new IdInvalidException("Time với id " + dto.getTimeId() + " không tồn tại");

        // Check ngày hợp lệ
        this.validateBookingDate(dto.getAppointmentDate(), time);

        // Check conflict bác sĩ (ngoại trừ chính booking này)
        boolean doctorBusy = bookingRepository.existsByDoctorIdAndAppointmentDateAndTimeIdAndStatusNot(
                existing.getDoctor().getId(),
                dto.getAppointmentDate(),
                time.getId(),
                BookingStatusEnum.CANCELLED);

        if (doctorBusy && !Objects.equals(existing.getTime().getId(), time.getId())) {
            throw new BusinessException("Bác sĩ đã có lịch tại khung giờ này");
        }

        existing.setClinic(clinic);
        existing.setTime(time);
        existing.setAppointmentDate(dto.getAppointmentDate());
        existing.setDescription(dto.getDescription());

        // reset status
        existing.setStatus(BookingStatusEnum.PENDING);

        return bookingRepository.save(existing);
    }

    private void validateBookingDate(LocalDate appointmentDate, Time timeSlot) throws BusinessException {
        LocalDate today = LocalDate.now();

        if (appointmentDate.isBefore(today)) {
            throw new BusinessException("Ngày đặt lịch không được trong quá khứ");
        }

        if (appointmentDate.isEqual(today)) {
            LocalTime now = LocalTime.now();
            LocalTime slotStart = LocalTime.parse(timeSlot.getStart());
            if (now.isAfter(slotStart)) {
                throw new BusinessException("Ca khám này đã trôi qua, vui lòng chọn khung giờ khác");
            }
        }

        // Giới hạn đặt trước tối đa 6 tháng
        LocalDate maxDate = today.plusMonths(6);
        if (appointmentDate.isAfter(maxDate)) {
            throw new BusinessException("Không được đặt lịch xa quá 6 tháng");
        }
    }

    public Booking cancelBooking(long id) {
        Booking b = this.bookingRepository.findById(id).orElse(null);

        if (b != null) {
            b.setStatus(BookingStatusEnum.CANCELLED);
            this.bookingRepository.save(b);
        }

        return b;
    }

    public ResBookingDTO convertToBookingDTO(Booking booking) {
        if (booking == null)
            return null;

        ResBookingDTO dto = new ResBookingDTO();
        dto.setId(booking.getId());
        dto.setAppointmentDate(booking.getAppointmentDate());
        dto.setDescription(booking.getDescription());
        dto.setCreateAt(booking.getCreateAt());
        dto.setUpdateAt(booking.getUpdateAt());
        dto.setStatus(booking.getStatus());

        if (booking.getDoctor() != null) {
            dto.setDoctor(this.doctorService.convertToDoctorDTO(booking.getDoctor()));
        }

        if (booking.getPatient() != null) {
            dto.setPatient(this.patientService.convertToResPatientDTO(booking.getPatient()));
        }

        if (booking.getClinic() != null) {
            dto.setClinic(this.clinicService.convertToClinicDTO(booking.getClinic()));
        }

        if (booking.getTime() != null) {
            dto.setTime(new ResBookingDTO.ResTimeDTO(
                    booking.getTime().getId(),
                    booking.getTime().getStart(),
                    booking.getTime().getEnd()));
        }

        return dto;
    }

}
