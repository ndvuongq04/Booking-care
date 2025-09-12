package com.Booking_care.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.Booking_care.domain.Account;
import com.Booking_care.domain.Notification;
import com.Booking_care.domain.response.ResNotificationDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.NotificationRepository;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final AccountService accountService;

    public NotificationService(NotificationRepository notificationRepository, AccountService accountService) {
        this.notificationRepository = notificationRepository;
        this.accountService = accountService;
    }

    public Account fetchAccountById(long id) {
        return this.accountService.fetchAccountById(id);
    }

    public boolean isNotificationExits(long id) {
        return this.notificationRepository.existsByAccountId(id);
    }

    public ResultPaginationDTO fetchAllNotification(Pageable pageable) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Notification> page = this.notificationRepository.findAll(pageable);

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        // convert
        List<ResNotificationDTO> listNoti = page.getContent().stream()
                .map(item -> this.convertToResNotificationDTO(item))
                .collect(Collectors.toList());
        res.setResult(listNoti);
        res.setMeta(meta);

        return res;
    }

    public Notification fetchNotificationById(long id) {
        Optional<Notification> noti = this.notificationRepository.findById(id);
        if (noti.isPresent()) {
            return noti.get();
        }
        return null;
    }

    public Notification handleCreateNotification(Notification notification) {
        return this.notificationRepository.save(notification);
    }

    public Notification handleUpdateNotification(Notification notification) {
        Notification currentNotification = this.fetchNotificationById(notification.getId());
        if (currentNotification != null) {
            if (notification.getAccount() != null) {
                Account account = this.fetchAccountById(notification.getAccount().getId());
                // set value
                currentNotification.setAccount(account != null ? account : null);
            }
            currentNotification.setTitle(notification.getTitle());
            currentNotification.setContent(notification.getContent());
            currentNotification = this.notificationRepository.save(currentNotification);
        }
        return currentNotification;
    }

    public void handleDeleteNotification(long id) {
        this.notificationRepository.deleteById(id);
    }

    public ResNotificationDTO convertToResNotificationDTO(Notification notification) {
        ResNotificationDTO res = new ResNotificationDTO();
        res.setId(notification.getId());
        res.setCreateAt(notification.getCreateAt());
        res.setContent(notification.getContent());
        res.setTitle(notification.getTitle());
        res.setAccount(this.accountService.convertToResAccountDTO(notification.getAccount()));

        return res;
    }
}
