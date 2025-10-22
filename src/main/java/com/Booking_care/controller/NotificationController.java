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
import com.Booking_care.domain.Notification;
import com.Booking_care.domain.dto.ResNotificationDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.service.NotificationService;
import com.Booking_care.util.annotation.ApiMessage;
import com.Booking_care.util.error.IdInvalidException;
import com.Booking_care.mapper.notification.NotificationMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/{id}")
    @ApiMessage("Fetch notification by id")
    public ResponseEntity<ResNotificationDTO> fetchNotificationById(@PathVariable("id") long id)
            throws IdInvalidException {
        Notification notification = this.notificationService.fetchNotificationById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(NotificationMapper.toResNotificationDTO(notification));
    }

    @GetMapping
    @ApiMessage("Fetch all notification")
    public ResponseEntity<ResultPaginationDTO> fetchAllNotification(
            Pageable pageable) {
        ResultPaginationDTO result = this.notificationService.fetchAllNotification(pageable);
        return ResponseEntity.ok().body(result);
    }

    @PostMapping
    @ApiMessage("Create a notification")
    public ResponseEntity<ResNotificationDTO> handleCreateNotification(@Valid @RequestBody Notification notification)
            throws IdInvalidException {
        Notification notificationDB = this.notificationService.handleCreateNotification(notification);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(NotificationMapper.toResNotificationDTO(notificationDB));
    }

    @PutMapping
    @ApiMessage("Update a notification")
    public ResponseEntity<ResNotificationDTO> handleUpdateNotification(@Valid @RequestBody Notification notification)
            throws IdInvalidException {
        Notification notificationDb = this.notificationService.handleUpdateNotification(notification);
        return ResponseEntity.status(HttpStatus.OK)
                .body(NotificationMapper.toResNotificationDTO(notificationDb));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete notification by id")
    public ResponseEntity<Void> handleDeleteNotification(@PathVariable("id") long id)
            throws IdInvalidException {
        this.notificationService.handleDeleteNotification(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(null);
    }
}
