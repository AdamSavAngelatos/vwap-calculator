package org.vwap.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Trade {

    private Instant timestamp;

    private String currencyPair;

    private double price;

    private int volume;
}
