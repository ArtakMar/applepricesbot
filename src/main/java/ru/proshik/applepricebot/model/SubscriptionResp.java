package ru.proshik.applepricebot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ru.proshik.applepricebot.repository.model.ProductType;

@Getter
@Builder
@AllArgsConstructor
public class SubscriptionResp {

    private Long id;

    private Long providerId;

    private ProductType productType;

}
