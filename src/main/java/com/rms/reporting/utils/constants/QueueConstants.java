package com.rms.reporting.utils.constants;

public interface QueueConstants {
    interface Audit {
        String EXCHANGE = "admin-audit.exchange";

        interface Queue {
            String MAIN = "admin-audit.queue";
            String DLQ = "admin-audit.dlq";
        }

        interface RoutingKey {
            String MAIN = "admin.audit.routingkey";
            String DLQ = "admin.audit.dlq";
        }
    }

    interface Chat {
        String EXCHANGE = "chat.exchange";
        interface Queue {
            String MAIN = "chat.queue";
            String DLQ = "chat.dlq";
        }
        interface RoutingKey {
            String MAIN = "chat.message";
            String DLQ = "chat.dlq";
        }
    }

    interface UserNotification {
        String EXCHANGE = "notification.exchange";
        interface Queue {
            String MAIN = "notification.queue";
            String DLQ = "notification.dlq";
        }
        interface RoutingKey {
            String MAIN = "notification.message";
            String DLQ = "notification.dlq";
        }
    }
}
