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
import com.Booking_care.domain.Feedback;
import com.Booking_care.domain.dto.ResFeedbackDTO;
import com.Booking_care.domain.dto.FeedbackDTO.ReqFeedbackDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.service.FeedbackService;
import com.Booking_care.util.annotation.ApiMessage;
import com.Booking_care.util.error.IdInvalidException;
import com.Booking_care.mapper.feedback.FeedbackMapper;
import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/feedbacks")
public class FeedbackController {
    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping("/{id}")
    @ApiMessage("Fetch feedback by id")
    public ResponseEntity<ResFeedbackDTO> fetchFeedbackById(@PathVariable("id") long id)
            throws IdInvalidException {
        Feedback feedback = this.feedbackService.fetchFeedbackById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(FeedbackMapper.toResFeedbackDTO(feedback));
    }

    @GetMapping("/doctor/{doctorId}")
    @ApiMessage("Fetch feedback by doctor id")
    public ResponseEntity<ResultPaginationDTO> fetchFeedbackByDoctorId(Pageable pageable,
            @PathVariable("doctorId") long doctorId)
            throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(this.feedbackService.fetchFeedbackByDoctorId(pageable, doctorId));
    }

    @GetMapping
    @ApiMessage("Fetch all feedback")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultPaginationDTO> fetchAllFeedback(
            Pageable pageable) {
        ResultPaginationDTO result = this.feedbackService.fetchAllFeedback(pageable);
        return ResponseEntity.ok().body(result);
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ResFeedbackDTO> handleCreateFeedback(@Valid @RequestBody ReqFeedbackDTO feedback)
            throws IdInvalidException {
        Feedback feedbackDB = this.feedbackService.handleCreateFeedback(feedback);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(FeedbackMapper.toResFeedbackDTO(feedbackDB));
    }

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResFeedbackDTO> handleUpdateFeedback(@Valid @RequestBody Feedback feedback)
            throws IdInvalidException {
        Feedback feedbackDb = this.feedbackService.handleUpdateFeedback(feedback);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(FeedbackMapper.toResFeedbackDTO(feedbackDb));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete feedback by id")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<Void> handleDeleteFeedback(@PathVariable("id") long id)
            throws IdInvalidException {
        this.feedbackService.handleDeleteFeedback(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(null);
    }
}
