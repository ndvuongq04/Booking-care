package com.Booking_care.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.Booking_care.domain.Account;
import com.Booking_care.domain.Booking;
import com.Booking_care.domain.Clinic;
import com.Booking_care.domain.Doctor;
import com.Booking_care.domain.Patient;
import com.Booking_care.domain.Time;
import com.Booking_care.domain.dto.BookingDTO.BookingClinicCriteriaDTO;
import com.Booking_care.domain.dto.BookingDTO.BookingCriteriaDTO;
import com.Booking_care.domain.dto.BookingDTO.BookingDoctorCriteriaDTO;
import com.Booking_care.domain.dto.BookingDTO.CreateBookingDTO;
import com.Booking_care.domain.dto.BookingDTO.ResBookingDTO;
import com.Booking_care.domain.dto.BookingDTO.UpdateBookingDTO;
import com.Booking_care.domain.enums.BookingStatusEnum;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.BookingRepository;
import com.Booking_care.service.specification.BookingSpecs;
import com.Booking_care.util.error.BusinessException;
import com.Booking_care.util.error.IdInvalidException;
import com.Booking_care.mapper.booking.BookingMapper;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TimeService timeService;
    private final DoctorService doctorService;
    private final ClinicService clinicService;
    private final PatientService patientService;
    private final EmailService emailService;
    private final String templateBookingSuccess = "templateBookingSuccess";
    private final String templateBookingCancel = "templateBookingCancel";

    public BookingService(BookingRepository bookingRepository,
            TimeService timeService,
            DoctorService doctorService,
            ClinicService clinicService,
            PatientService patientService,
            EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.timeService = timeService;
        this.doctorService = doctorService;
        this.clinicService = clinicService;
        this.patientService = patientService;
        this.emailService = emailService;
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
                .map(BookingMapper::toResBookingDTO)
                .collect(Collectors.toList());

        res.setResult(listBooking);
        res.setMeta(meta);

        return res;
    }

    public Booking getBookingById(long id) {
        return this.bookingRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Booking với id " + id + " không tồn tại"));
    }

    public Booking createBooking(CreateBookingDTO dto) {
        // Validation: check entities exist (will throw if not found)
        Doctor doctor = doctorService.fetchDoctorById(dto.getDoctorId());
        Clinic clinic = clinicService.fetchClinicById(dto.getClinicId());
        Time time = timeService.fetchTimeById(dto.getTimeId());
        Patient patient = patientService.fetchPatientById(dto.getPatientId());

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

        try {
            Booking booking = new Booking();
            booking.setAppointmentDate(dto.getAppointmentDate());
            booking.setDescription(dto.getDescription());
            booking.setStatus(BookingStatusEnum.PENDING);
            booking.setDoctor(doctor);
            booking.setClinic(clinic);
            booking.setTime(time);
            booking.setPatient(patient);

            Booking b = bookingRepository.save(booking);

            // send email
            this.sendEmailBooking(patient.getAccount(), b, "Xác nhận đặt lịch khám thành công", templateBookingSuccess);

            return b;
        } catch (Exception e) {
            throw new BusinessException("Không thể tạo booking: " + e.getMessage());
        }
    }

    public void sendEmailBooking(Account a, Booking b, String subTitle, String template) {
        this.emailService.sendEmailFromTemplateSync(
                a.getEmail(),
                subTitle,
                template,
                a.getName() == null ? null : a.getName(),
                b);
    }

    public Booking updateBooking(UpdateBookingDTO dto) {
        // Validation: check entities exist (will throw if not found)
        Booking existing = this.getBookingById(dto.getId());
        Clinic clinic = clinicService.fetchClinicById(dto.getClinicId());
        Time time = timeService.fetchTimeById(dto.getTimeId());

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

        try {
            existing.setClinic(clinic);
            existing.setTime(time);
            existing.setAppointmentDate(dto.getAppointmentDate());
            existing.setDescription(dto.getDescription());

            // reset status
            existing.setStatus(BookingStatusEnum.PENDING);

            return bookingRepository.save(existing);
        } catch (Exception e) {
            throw new BusinessException("Không thể cập nhật booking: " + e.getMessage());
        }
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
        // Validation: check booking exists (will throw if not found)
        Booking b = this.getBookingById(id);

        try {
            b.setStatus(BookingStatusEnum.CANCELLED);
            this.bookingRepository.save(b);

            Patient p = this.patientService.fetchPatientById(b.getPatient().getId());
            // send email
            this.sendEmailBooking(p.getAccount(), b, "Thông báo hủy lịch khám", templateBookingCancel);

            return b;
        } catch (Exception e) {
            throw new BusinessException("Không thể hủy booking: " + e.getMessage());
        }
    }

    public Booking updateBookingStatus(long id, BookingStatusEnum status) {
        // Validation: check booking exists (will throw if not found)
        Booking b = this.getBookingById(id);

        try {
            b.setStatus(status);
            b.setUpdateAt(Instant.now());
            return this.bookingRepository.save(b);
        } catch (Exception e) {
            throw new BusinessException("Không thể cập nhật trạng thái booking: " + e.getMessage());
        }
    }

    public ResultPaginationDTO fetchBookingByInstanceId(long id, Pageable pageable, Object obj) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Booking> page = Page.empty();

        if (obj instanceof Patient) {
            page = this.bookingRepository.findByPatientId(id, pageable);
        }

        if (obj instanceof Doctor) {
            page = this.bookingRepository.findByDoctorId(id, pageable);
        }

        if (obj instanceof Clinic) {
            page = this.bookingRepository.findByClinicId(id, pageable);
        }

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        // convert
        List<ResBookingDTO> listBooking = page.getContent().stream()
                .map(BookingMapper::toResBookingDTO)
                .collect(Collectors.toList());

        res.setResult(listBooking);
        res.setMeta(meta);

        return res;
    }

    public ResultPaginationDTO getBookingsByDoctorAndDate(Long doctorId, LocalDate appointmentDate, Pageable pageable) {
        Page<Booking> page = bookingRepository.findByDoctorIdAndAppointmentDate(doctorId, appointmentDate, pageable);

        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        res.setMeta(meta);
        res.setResult(page.getContent().stream().map(BookingMapper::toResBookingDTO).toList());

        return res;
    }

    public List<ResBookingDTO.ResTimeDTO> getAvailableTimes(Long doctorId, LocalDate appointmentDate) {
        // Tất cả slot trong hệ thống
        List<Time> allTimes = this.timeService.getAllTimes();

        // Các booking đã tồn tại
        List<Booking> booked = bookingRepository.findByDoctorIdAndAppointmentDate(doctorId, appointmentDate);

        // Lấy id time đã được đặt
        Set<Long> bookedTimeIds = booked.stream()
                .map(b -> b.getTime().getId())
                .collect(Collectors.toSet());

        // Lọc ra các time chưa bị đặt
        return allTimes.stream()
                .filter(t -> !bookedTimeIds.contains(t.getId()))
                .map(t -> new ResBookingDTO.ResTimeDTO(t.getId(), t.getStart(), t.getEnd()))
                .toList();
    }

    Page<Booking> getBookingWithSpecs(Pageable pageable, BookingCriteriaDTO bookingCriteriaDTO) {
        Specification<Booking> combinedSpec = Specification.where(null);

        if (bookingCriteriaDTO.getAccountName() != null && !bookingCriteriaDTO.getAccountName().trim().isEmpty()) {
            Specification<Booking> currentSpec = BookingSpecs
                    .patientAccountNameLikeIgnoreCase(bookingCriteriaDTO.getAccountName());
            combinedSpec = combinedSpec.and(currentSpec);
        }

        if (bookingCriteriaDTO.getPhoneNumber() != null && !bookingCriteriaDTO.getPhoneNumber().trim().isEmpty()) {
            Specification<Booking> currentSpec = BookingSpecs
                    .patientAccountPhoneNumberLikeIgnoreCase(bookingCriteriaDTO.getPhoneNumber());
            combinedSpec = combinedSpec.and(currentSpec);
        }

        if (bookingCriteriaDTO.getDate() != null) {
            LocalDate date = bookingCriteriaDTO.getDate();
            Specification<Booking> currentSpec = BookingSpecs.appointmentDateEqual(date);
            combinedSpec = combinedSpec.and(currentSpec);
        }

        return this.bookingRepository.findAll(combinedSpec, pageable);
    }

    public ResultPaginationDTO fetchAllBookingSearch(Pageable pageable, BookingCriteriaDTO bookingCriteriaDTO) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Booking> page = this.getBookingWithSpecs(pageable, bookingCriteriaDTO);

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        // convert
        List<ResBookingDTO> listBooking = page.getContent().stream()
                .map(BookingMapper::toResBookingDTO)
                .collect(Collectors.toList());

        res.setResult(listBooking);
        res.setMeta(meta);

        return res;
    }

    public Page<Booking> getBookingDoctorWithSpecs(
            Pageable pageable, BookingDoctorCriteriaDTO dto) {
        Specification<Booking> spec = Specification
                .where(BookingSpecs.doctorIdEqual(dto.getDoctorId()));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            spec = spec.and(BookingSpecs.patientAccountNameLikeIgnoreCase(dto.getName()));
        }

        if (dto.getDate() != null) {
            LocalDate date = dto.getDate();
            Specification<Booking> currentSpec = BookingSpecs.appointmentDateEqual(date);
            spec = spec.and(currentSpec);
        }

        return bookingRepository.findAll(spec, pageable);
    }

    public ResultPaginationDTO fetchAllBookingDoctorSearch(Pageable pageable,
            BookingDoctorCriteriaDTO bookingDoctorCriteriaDTO) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Booking> page = this.getBookingDoctorWithSpecs(pageable, bookingDoctorCriteriaDTO);

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        // convert
        List<ResBookingDTO> listBooking = page.getContent().stream()
                .map(BookingMapper::toResBookingDTO)
                .collect(Collectors.toList());

        res.setResult(listBooking);
        res.setMeta(meta);

        return res;
    }

    public Page<Booking> getBookingClinicWithSpecs(Pageable pageable, BookingClinicCriteriaDTO dto) {
        Specification<Booking> spec = Specification
                .where(BookingSpecs.clinicIdEqual(dto.getClinicId()));

        if (dto.getDoctorName() != null && !dto.getDoctorName().isBlank()) {
            spec = spec.and(BookingSpecs.doctorIdLikeIgnoreCase(dto.getDoctorName()));
        }

        if (dto.getPatientName() != null && !dto.getPatientName().isBlank()) {
            spec = spec.and(BookingSpecs.patientAccountNameLikeIgnoreCase(dto.getPatientName()));
        }

        if (dto.getDate() != null) {
            LocalDate date = dto.getDate();
            Specification<Booking> currentSpec = BookingSpecs.appointmentDateEqual(date);
            spec = spec.and(currentSpec);
        }

        return bookingRepository.findAll(spec, pageable);
    }

    public ResultPaginationDTO fetchAllBookingClinicSearch(Pageable pageable,
            BookingClinicCriteriaDTO bookingClinicCriteriaDTO) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Booking> page = this.getBookingClinicWithSpecs(pageable, bookingClinicCriteriaDTO);

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        // convert
        List<ResBookingDTO> listBooking = page.getContent().stream()
                .map(BookingMapper::toResBookingDTO)
                .collect(Collectors.toList());

        res.setResult(listBooking);
        res.setMeta(meta);

        return res;
    }

}
