package com.Booking_care.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.Booking_care.domain.Doctor;
import com.Booking_care.domain.Feedback;
import com.Booking_care.domain.Patient;
import com.Booking_care.domain.dto.ResFeedbackDTO;
import com.Booking_care.domain.dto.FeedbackDTO.ReqFeedbackDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.FeedbackRepository;

@Service
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public FeedbackService(FeedbackRepository feedbackRepository,
            DoctorService doctorService,
            PatientService patientService) {
        this.feedbackRepository = feedbackRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
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
                .map(item -> this.convertToResFeedbackDTO(item))
                .collect(Collectors.toList());
        res.setResult(listFeedback);
        res.setMeta(meta);

        return res;
    }

    public Feedback fetchFeedbackById(long id) {
        Optional<Feedback> feedback = this.feedbackRepository.findById(id);
        if (feedback.isPresent()) {
            return feedback.get();
        }
        return null;
    }

    @Transactional
    public Feedback handleCreateFeedback(ReqFeedbackDTO req) {
        Feedback fb = new Feedback();
        fb.setRate(req.getRate());
        fb.setDescription(req.getDescription());

        fb.setDoctor(this.doctorService.fetchDoctorById(req.getDoctorId()));
        fb.setPatient(this.patientService.fetchPatientById(req.getDoctorId()));

        return this.feedbackRepository.save(fb);
    }

    public Feedback handleUpdateFeedback(Feedback feedback) {
        Feedback currentFeedback = this.fetchFeedbackById(feedback.getId());
        if (currentFeedback != null) {
            if (feedback.getDoctor() != null) {
                Doctor doctor = this.fetchDoctorById(feedback.getDoctor().getId());
                currentFeedback.setDoctor(doctor != null ? doctor : null);
            }
            currentFeedback.setRate(feedback.getRate());
            currentFeedback.setDescription(feedback.getDescription());
            currentFeedback = this.feedbackRepository.save(currentFeedback);
        }
        return currentFeedback;
    }

    public void handleDeleteFeedback(long id) {
        this.feedbackRepository.deleteById(id);
    }

    public ResFeedbackDTO convertToResFeedbackDTO(Feedback fb) {
        if (fb == null)
            return null;
        ResFeedbackDTO res = new ResFeedbackDTO();
        res.setId(fb.getId());
        res.setDescription(fb.getDescription());
        res.setRate(fb.getRate());

        // doctor
        Doctor doctor = fb.getDoctor();
        if (doctor != null) {
            res.setDoctor(doctorService.convertToDoctorDTO(doctor));
        }

        // patient
        Patient patient = fb.getPatient();
        if (patient != null) {
            res.setPatient(patientService.convertToResPatientDTO(patient));
        }

        return res;
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
                .map(item -> this.convertToResFeedbackDTO(item))
                .collect(Collectors.toList());
        res.setResult(listFeedback);
        res.setMeta(meta);

        return res;
    }
}
