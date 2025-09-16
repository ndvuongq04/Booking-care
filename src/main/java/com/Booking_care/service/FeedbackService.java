package com.Booking_care.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.Booking_care.domain.Doctor;
import com.Booking_care.domain.Feedback;
import com.Booking_care.domain.response.ResFeedbackDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.FeedbackRepository;

@Service
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final DoctorService doctorService;

    public FeedbackService(FeedbackRepository feedbackRepository, DoctorService doctorService) {
        this.feedbackRepository = feedbackRepository;
        this.doctorService = doctorService;
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

    public Feedback handleCreateFeedback(Feedback feedback) {
        return this.feedbackRepository.save(feedback);
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

    public ResFeedbackDTO convertToResFeedbackDTO(Feedback feedback) {
        ResFeedbackDTO res = new ResFeedbackDTO();
        res.setId(feedback.getId());
        res.setDescription(feedback.getDescription());

        Doctor doctor = this.doctorService.fetchDoctorById(feedback.getDoctor().getId());
        res.setDoctor(this.doctorService.convertToDoctorDTO(doctor));

        return res;
    }
}
