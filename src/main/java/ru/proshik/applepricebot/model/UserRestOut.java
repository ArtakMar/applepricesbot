package ru.proshik.applepricebot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class UserRestOut {

    private Long id;

    private ZonedDateTime createdDate;

    private String chatId;

    private List<SubscriptionRestOut> subscriptions;


}
