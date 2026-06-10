package com.rms.reporting.consumer;

import com.rms.reporting.model.ChatMessageEvent;
import com.rms.reporting.service.ChatMessageService;
import com.rms.reporting.utils.constants.QueueConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatMessageListener {

    private final ChatMessageService chatMessageService;

    @RabbitListener(queues = QueueConstants.Chat.Queue.MAIN)
    public void handleChatMessage(ChatMessageEvent message) {
        log.info("Received chat message: messageId={}, roomId={}",
                message != null ? message.getMessageId() : null,
                message != null ? message.getRoomId() : null);
        if (message == null) {
            throw new IllegalArgumentException("Chat message was null.");
        }
        chatMessageService.store(message);
    }

    @RabbitListener(queues = QueueConstants.Chat.Queue.DLQ)
    public void handleChatMessageDlq(ChatMessageEvent message) {
        log.error("Chat message moved to DLQ: messageId={}, roomId={}",
                message != null ? message.getMessageId() : null,
                message != null ? message.getRoomId() : null);
    }
}
