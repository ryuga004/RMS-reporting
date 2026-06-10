package com.rms.reporting.consumer;

import com.rms.reporting.model.NotificationEvent;
import com.rms.reporting.service.UserNotificationService;
import com.rms.reporting.utils.constants.QueueConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final UserNotificationService userNotificationService;

    @RabbitListener(queues = QueueConstants.UserNotification.Queue.MAIN)
    public void handleNotification(NotificationEvent message) {
        log.info("Received notification: notificationId={}, userId={}",
                message != null ? message.getNotificationId() : null,
                message != null ? message.getUserId() : null);
        if (message == null) {
            throw new IllegalArgumentException("Notification message was null.");
        }
        userNotificationService.store(message);
    }

    @RabbitListener(queues = QueueConstants.UserNotification.Queue.DLQ)
    public void handleNotificationDlq(NotificationEvent message) {
        log.error("Notification moved to DLQ: notificationId={}, userId={}",
                message != null ? message.getNotificationId() : null,
                message != null ? message.getUserId() : null);
    }
}
