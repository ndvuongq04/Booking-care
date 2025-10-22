package com.Booking_care.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.Booking_care.domain.Account;
import com.Booking_care.domain.Notification;
import com.Booking_care.domain.dto.ResNotificationDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.NotificationRepository;
import com.Booking_care.util.error.IdInvalidException;
import com.Booking_care.mapper.notification.NotificationMapper;

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
                .map(NotificationMapper::toResNotificationDTO)
                .collect(Collectors.toList());
        res.setResult(listNoti);
        res.setMeta(meta);

        return res;
    }

    public Notification fetchNotificationById(long id) {
        return this.notificationRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Notification với id " + id + " không tồn tại"));
    }

    public Notification handleCreateNotification(Notification notification) {
        // Validation: check account exists (will throw if not found)
        Account account = this.fetchAccountById(notification.getAccount().getId());

        try {
            notification.setAccount(account);
            return this.notificationRepository.save(notification);
        } catch (Exception e) {
            throw new IdInvalidException("Không thể tạo notification: " + e.getMessage());
        }
    }

    public Notification handleUpdateNotification(Notification notification) {
        // Validation: check notification exists (will throw if not found)
        Notification currentNotification = this.fetchNotificationById(notification.getId());

        try {
            if (notification.getAccount() != null) {
                Account account = this.fetchAccountById(notification.getAccount().getId());
                currentNotification.setAccount(account);
            }
            currentNotification.setTitle(notification.getTitle());
            currentNotification.setContent(notification.getContent());
            return this.notificationRepository.save(currentNotification);
        } catch (Exception e) {
            throw new IdInvalidException("Không thể cập nhật notification: " + e.getMessage());
        }
    }

    public void handleDeleteNotification(long id) {
        // Validation: check notification exists (will throw if not found)
        this.fetchNotificationById(id);

        try {
            this.notificationRepository.deleteById(id);
        } catch (Exception e) {
            throw new IdInvalidException("Không thể xóa notification: " + e.getMessage());
        }
    }

}
