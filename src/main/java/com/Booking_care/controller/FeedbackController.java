package com.Booking_care.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Booking_care.domain.Doctor;
import com.Booking_care.domain.Feedback;
import com.Booking_care.domain.Patient;
import com.Booking_care.domain.dto.ResFeedbackDTO;
import com.Booking_care.domain.dto.FeedbackDTO.ReqFeedbackDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.service.FeedbackService;
import com.Booking_care.service.PatientService;
import com.Booking_care.util.annotation.ApiMessage;
import com.Booking_care.util.error.IdInvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/feedbacks")
public class FeedbackController {
    private final FeedbackService feedbackService;
    private final PatientService patientService;

    public FeedbackController(FeedbackService feedbackService,
            PatientService patientService) {
        this.feedbackService = feedbackService;
        this.patientService = patientService;
    }

    @GetMapping("/{id}")
    @ApiMessage("Fetch feedback by id")
    public ResponseEntity<ResFeedbackDTO> fetchFeedbackById(@PathVariable("id") long id)
            throws IdInvalidException {
        Feedback feedback = this.feedbackService.fetchFeedbackById(id);

        if (feedback == null) {
            throw new IdInvalidException("feedback với id " + id + " không tồn tại");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(this.feedbackService.convertToResFeedbackDTO(feedback));
    }

    @GetMapping("/doctor/{doctorId}")
    @ApiMessage("Fetch feedback by doctor id")
    public ResponseEntity<ResultPaginationDTO> fetchFeedbackByDoctorId(Pageable pageable,
            @PathVariable("doctorId") long doctorId)
            throws IdInvalidException {
        ResultPaginationDTO res = this.feedbackService.fetchFeedbackByDoctorId(pageable, doctorId);

        if (res == null || res.getMeta() == null || res.getMeta().getTotals() == 0) {
            throw new IdInvalidException("feedback của doctor id " + doctorId + " không tồn tại");
        }

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping
    @ApiMessage("Fetch all feedback")
    public ResponseEntity<ResultPaginationDTO> fetchAllFeedback(
            Pageable pageable) {
        ResultPaginationDTO result = this.feedbackService.fetchAllFeedback(pageable);
        return ResponseEntity.ok().body(result);
    }

    @PostMapping
    public ResponseEntity<ResFeedbackDTO> handleCreateFeedback(@Valid @RequestBody ReqFeedbackDTO feedback)
            throws IdInvalidException {

        Doctor doctor = this.feedbackService.fetchDoctorById(feedback.getDoctorId());
        if (doctor == null) {
            throw new IdInvalidException("Doctor với id " + feedback.getDoctorId() + " không tồn tại");
        }

        Patient patient = this.patientService.fetchPatientById(feedback.getPatientId());
        if (patient == null) {
            throw new IdInvalidException("Patient với id " + feedback.getPatientId() + " không tồn tại");
        }

        Feedback feedbackDB = this.feedbackService.handleCreateFeedback(feedback);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.feedbackService.convertToResFeedbackDTO(feedbackDB));
    }

    @PutMapping
    public ResponseEntity<ResFeedbackDTO> handleUpdateFeedback(@Valid @RequestBody Feedback feedback)
            throws IdInvalidException {
        Feedback feedbackDb = this.feedbackService.handleUpdateFeedback(feedback);

        if (feedbackDb == null) {
            throw new IdInvalidException("Feedback với id " + feedback.getId() + " không tồn tại");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.feedbackService.convertToResFeedbackDTO(feedbackDb));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete feedback by id")
    public ResponseEntity<Void> handleDeleteFeedback(@PathVariable("id") long id)
            throws IdInvalidException {
        Feedback feedback = this.feedbackService.fetchFeedbackById(id);

        if (feedback == null) {
            throw new IdInvalidException("Feedback với id " + id + " không tồn tại");
        }
        this.feedbackService.handleDeleteFeedback(id);
        ;

        return ResponseEntity.status(HttpStatus.OK)
                .body(null);
    }
}
