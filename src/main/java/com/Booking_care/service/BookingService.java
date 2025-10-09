package com.Booking_care.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
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
import com.Booking_care.domain.dto.BookingDTO.BookingCriteriaDTO;
import com.Booking_care.domain.dto.BookingDTO.BookingDoctorCriteriaDTO;
import com.Booking_care.domain.dto.BookingDTO.CreateBookingDTO;
import com.Booking_care.domain.dto.BookingDTO.ResBookingDTO;
import com.Booking_care.domain.dto.BookingDTO.UpdateBookingDTO;
import com.Booking_care.domain.enums.BookingStatusEnum;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.BookingRepository;
import com.Booking_care.service.specification.BookingSpecs;
import com.Booking_care.service.specification.ClinicSpecs;
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
    private final EmailService emailService;
    private final String templateBookingSuccess = "templateBookingSuccess";
    private final String templateBookingCancel = "templateBookingCancel";

    public BookingService(BookingRepository bookingRepository,
            TimeService timeService,
            DoctorService doctorService,
            ClinicService clinicService,
            AccountService accountService,
            PatientService patientService,
            EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.timeService = timeService;
        this.doctorService = doctorService;
        this.clinicService = clinicService;
        this.accountService = accountService;
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

        Booking b = bookingRepository.save(booking);

        // send email
        this.sendEmailBooking(patient.getAccount(), b, "Xác nhận đặt lịch khám thành công", templateBookingSuccess);

        return b;
    }

    public void sendEmailBooking(Account a, Booking b, String subTitle, String template) {
        this.emailService.sendEmailFromTemplateSync(
                a.getEmail(),
                subTitle,
                template,
                a.getName() == null ? null : a.getName(),
                b);
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

    private void validateBookingDate(Instant appointmentInstant, Time timeSlot) throws BusinessException {
        ZoneId zone = ZoneId.of("Asia/Ho_Chi_Minh");

        LocalDate today = LocalDate.now(zone);
        LocalDate apptDate = appointmentInstant.atZone(zone).toLocalDate();

        if (apptDate.isBefore(today)) {
            throw new BusinessException("Ngày đặt lịch không được trong quá khứ");
        }

        if (apptDate.isEqual(today)) {
            LocalTime now = LocalTime.now(zone);
            LocalTime slotStart = LocalTime.parse(timeSlot.getStart()); // "HH:mm"
            if (now.isAfter(slotStart)) {
                throw new BusinessException("Ca khám này đã trôi qua, vui lòng chọn khung giờ khác");
            }

            Instant slotStartInstant = apptDate.atTime(slotStart).atZone(zone).toInstant();
            if (Instant.now().isAfter(slotStartInstant)) {
                throw new BusinessException("Ca khám này đã trôi qua, vui lòng chọn khung giờ khác");
            }
        }

        LocalDate maxDate = today.plusMonths(6);
        if (apptDate.isAfter(maxDate)) {
            throw new BusinessException("Không được đặt lịch xa quá 6 tháng");
        }
    }

    public Booking cancelBooking(long id) {
        Booking b = this.bookingRepository.findById(id).orElse(null);

        if (b != null) {
            b.setStatus(BookingStatusEnum.CANCELLED);
            this.bookingRepository.save(b);

            Patient p = this.patientService.fetchPatientById(b.getPatient().getId());
            // send email
            this.sendEmailBooking(p.getAccount(), b, "Thông báo hủy lịch khám", templateBookingCancel);
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

    public Booking updateBookingStatus(long id, BookingStatusEnum status) {
        Booking b = this.getBookingById(id);

        if (b != null) {
            b.setStatus(status);
            b.setUpdateAt(Instant.now());

            this.bookingRepository.save(b);
        }
        return b;
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
                .map(item -> this.convertToBookingDTO(item))
                .collect(Collectors.toList());

        res.setResult(listBooking);
        res.setMeta(meta);

        return res;
    }

    public ResultPaginationDTO getBookingsByDoctorAndDate(Long doctorId, Instant appointmentDate, Pageable pageable) {
        Page<Booking> page = bookingRepository.findByDoctorIdAndAppointmentDate(doctorId, appointmentDate, pageable);

        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        res.setMeta(meta);
        res.setResult(page.getContent().stream().map(this::convertToBookingDTO).toList());

        return res;
    }

    public List<ResBookingDTO.ResTimeDTO> getAvailableTimes(Long doctorId, Instant appointmentDate) {
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

        if (bookingCriteriaDTO.getMonthYear() != null) {
            YearMonth monthYear = bookingCriteriaDTO.getMonthYear();

            Instant from = monthYear.atDay(1)
                    .atStartOfDay(ZoneOffset.UTC) // mốc 00:00 ngày đầu tháng
                    .toInstant();

            Instant to = monthYear.plusMonths(1).atDay(1)
                    .atStartOfDay(ZoneOffset.UTC) // mốc 00:00 ngày đầu tháng kế tiếp
                    .toInstant();

            Specification<Booking> currentSpec = BookingSpecs.dateBetween(from, to);
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
                .map(item -> this.convertToBookingDTO(item))
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

        if (dto.getMonthYear() != null) {
            YearMonth monthYear = dto.getMonthYear();

            Instant from = monthYear.atDay(1)
                    .atStartOfDay(ZoneOffset.UTC) // mốc 00:00 ngày đầu tháng
                    .toInstant();

            Instant to = monthYear.plusMonths(1).atDay(1)
                    .atStartOfDay(ZoneOffset.UTC) // mốc 00:00 ngày đầu tháng kế tiếp
                    .toInstant();

            Specification<Booking> currentSpec = BookingSpecs.dateBetween(from, to);
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
                .map(item -> this.convertToBookingDTO(item))
                .collect(Collectors.toList());

        res.setResult(listBooking);
        res.setMeta(meta);

        return res;
    }

}
