package com.whoami.launch.service;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whoami.launch.dto.NotificationActionDTO;
import com.whoami.launch.enums.NotificationType;

@Service
public class NotificationTemplateService {

    private final ObjectMapper objectMapper;

    public NotificationTemplateService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String buildActions(NotificationType type) {

        try {

            List<NotificationActionDTO> actions = switch (type) {

                case CHAT -> List.of(
                		NotificationActionDTO.builder()
                	    .key("REPLY_MESSAGE")
                	    .label("Reply")
                	    .type("INLINE_REPLY")
                	    .endpoint("/api/chat/messages")
                	    .method("POST")
                	    .authenticationRequired(true)
                	    .build(),

                                NotificationActionDTO.builder()
                                .key("MARK_READ")
                                .label("Mark Read")
                                .type("SILENT_API")
                                .endpoint("/api/notifications/read/{notificationId}")
                                .method("PUT")
                                .authenticationRequired(true)
                                .build()
                );

                case ORDER -> List.of(
                        NotificationActionDTO.builder()
                                .key("TRACK_ORDER")
                                .label("Track Order")
                                .type("DEEP_LINK")
                                .build()
                );

                case PRODUCT -> List.of(
                        NotificationActionDTO.builder()
                                .key("VIEW_PRODUCT")
                                .label("View Product")
                                .type("DEEP_LINK")
                                .build(),

                        NotificationActionDTO.builder()
                                .key("VIEW_SHOP")
                                .label("View Shop")
                                .type("DEEP_LINK")
                                .build()
                );

                case SERVICE -> List.of(
                        NotificationActionDTO.builder()
                                .key("VIEW_SERVICE")
                                .label("View Service")
                                .type("DEEP_LINK")
                                .build()
                );

                case REEL -> List.of(
                        NotificationActionDTO.builder()
                                .key("VIEW_REEL")
                                .label("View Reel")
                                .type("DEEP_LINK")
                                .build(),

                                NotificationActionDTO.builder()
                                .key("LIKE_REEL")
                                .label("Like")
                                .type("SILENT_API")
                                .endpoint("/api/users/{userId}/reels/{targetId}/like")
                                .method("POST")
                                .authenticationRequired(true)
                                .build()
                );

                case FOLLOW -> List.of(

                        NotificationActionDTO.builder()
                                .key("VIEW_SHOP")
                                .label("View Shop")
                                .type("DEEP_LINK")
                                .build(),

                        NotificationActionDTO.builder()
                                .key("FOLLOW_SHOP")
                                .label("Follow")
                                .type("SILENT_API")
                                .endpoint("/api/shop-follow/follow")
                                .method("POST")
                                .authenticationRequired(true)
                                .build()
                );

                default -> List.of();
            };

            return objectMapper.writeValueAsString(actions);

        } catch (Exception e) {
            return "[]";
        }
    }

    public String buildDeepLink(
            NotificationType type,
            String targetId
    ) {

        if (targetId == null) {
            return "";
        }

        return switch (type) {

            case CHAT -> "/chat/detail/" + targetId;

            case PRODUCT -> "/product/" + targetId;

            case SERVICE -> "/service/" + targetId;

            case REEL -> "/reels?reelId=" + targetId;

            case SHOP, FOLLOW -> "/shop/" + targetId;

            case ORDER -> "/orders/" + targetId;

            default -> "";
        };
    }
}