package com.Booking_care.mapper.notification;

import com.Booking_care.domain.Notification;
import com.Booking_care.domain.dto.ResNotificationDTO;
import com.Booking_care.mapper.account.AccountMapper;

/**
 * Mapper for Notification entity
 */
public final class NotificationMapper {
    
    private NotificationMapper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Convert Notification entity to ResNotificationDTO
     * @param notification Notification entity
     * @return ResNotificationDTO
     */
    public static ResNotificationDTO toResNotificationDTO(Notification notification) {
        if (notification == null) {
            return null;
        }

        ResNotificationDTO res = new ResNotificationDTO();
        res.setId(notification.getId());
        res.setCreateAt(notification.getCreateAt());
        res.setContent(notification.getContent());
        res.setTitle(notification.getTitle());

        if (notification.getAccount() != null) {
            res.setAccount(AccountMapper.toResAccountDTO(notification.getAccount()));
        }

        return res;
    }
}

