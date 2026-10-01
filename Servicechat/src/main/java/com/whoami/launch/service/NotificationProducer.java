package com.whoami.launch.service;

import com.locato.constants.events.chat.ChatNotificationEvent;

public interface NotificationProducer {

    void sendNotification(
            ChatNotificationEvent event);
}
