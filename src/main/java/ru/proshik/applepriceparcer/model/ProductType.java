package ru.proshik.applepriceparcer.model;

import java.io.Serializable;
import java.util.Arrays;

public enum ProductType implements Serializable {

    IPHONE("iPhone"),
    IPAD("iPad"),
    IMAC("iMac"),
    MAC_MINI("MacMini"),
    MACBOOK_PRO("MacbookPro"),
    MACBOOK_AIR("MacbookAir"),
    ACCESSORIES("Accessories"),
    OTHER("Other");

    private String value;

    ProductType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ProductType fromValue(String value) {
        return Arrays.stream(values())
                .filter(pro -> pro.getValue().toUpperCase().equals(value.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unexpected ProductType value=" + value));
    }
}
