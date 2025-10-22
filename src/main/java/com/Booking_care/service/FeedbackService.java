package com.Booking_care.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.Booking_care.domain.Booking;
import com.Booking_care.domain.Doctor;
import com.Booking_care.domain.Feedback;
import com.Booking_care.domain.Patient;
import com.Booking_care.domain.dto.ResFeedbackDTO;
import com.Booking_care.domain.dto.FeedbackDTO.ReqFeedbackDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.BookingRepository;
import com.Booking_care.repository.FeedbackRepository;
import com.Booking_care.util.error.IdInvalidException;
import com.Booking_care.mapper.feedback.FeedbackMapper;

@Service
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, DoctorService doctorService,
            PatientService patientService, BookingService bookingService, BookingRepository bookingRepository) {
        this.feedbackRepository = feedbackRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
    }

    public Doctor fetchDoctorById(long id) {
        return this.doctorService.fetchDoctorById(id);
    }

    public boolean isFeedbackExits(long id) {
        return this.feedbackRepository.existsByDoctorId(id);
    }

    public ResultPaginationDTO fetchAllFeedback(Pageable pageable) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Feedback> page = this.feedbackRepository.findAll(pageable);

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        // convert
        List<ResFeedbackDTO> listFeedback = page.getContent().stream()
                .map(FeedbackMapper::toResFeedbackDTO)
                .collect(Collectors.toList());
        res.setResult(listFeedback);
        res.setMeta(meta);

        return res;
    }

    public Feedback fetchFeedbackById(long id) {
        return this.feedbackRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("feedback với id " + id + " không tồn tại"));
    }

    @Transactional
    public Feedback handleCreateFeedback(ReqFeedbackDTO req) {
        // Validation: check doctor exists (will throw if not found)
        Doctor doctor = this.doctorService.fetchDoctorById(req.getDoctorId());

        // Validation: check patient exists (will throw if not found)
        Patient patient = this.patientService.fetchPatientById(req.getPatientId());

        // Validation: check booking exists (will throw if not found)
        Booking bk = this.bookingService.getBookingById(req.getBookingId());

        try {
            Feedback fb = new Feedback();
            fb.setRate(req.getRate());
            fb.setDescription(req.getDescription());
            fb.setDoctor(doctor);
            fb.setPatient(patient);
            fb.setBooking(bk);

            Feedback fed = this.feedbackRepository.save(fb);

            bk.setCheckFeedback(true);
            this.bookingRepository.save(bk);

            return fed;
        } catch (Exception e) {
            throw new IdInvalidException("Không thể tạo feedback: " + e.getMessage());
        }
    }

    public Feedback handleUpdateFeedback(Feedback feedback) {
        // Validation: check feedback exists (will throw if not found)
        Feedback currentFeedback = this.fetchFeedbackById(feedback.getId());

        try {
            if (feedback.getDoctor() != null) {
                Doctor doctor = this.fetchDoctorById(feedback.getDoctor().getId());
                currentFeedback.setDoctor(doctor);
            }
            currentFeedback.setRate(feedback.getRate());
            currentFeedback.setDescription(feedback.getDescription());
            return this.feedbackRepository.save(currentFeedback);
        } catch (Exception e) {
            throw new IdInvalidException("Không thể cập nhật feedback: " + e.getMessage());
        }
    }

    public void handleDeleteFeedback(long id) {
        // Validation: check feedback exists (will throw if not found)
        this.fetchFeedbackById(id);

        try {
            this.feedbackRepository.deleteById(id);
        } catch (Exception e) {
            throw new IdInvalidException("Không thể xóa feedback: " + e.getMessage());
        }
    }

    public ResultPaginationDTO fetchFeedbackByDoctorId(Pageable pageable, Long doctorId) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Feedback> page = this.feedbackRepository.findByDoctorId(pageable, doctorId);

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        // convert
        List<ResFeedbackDTO> listFeedback = page.getContent().stream()
                .map(FeedbackMapper::toResFeedbackDTO)
                .collect(Collectors.toList());
        res.setResult(listFeedback);
        res.setMeta(meta);

        return res;
    }
}
