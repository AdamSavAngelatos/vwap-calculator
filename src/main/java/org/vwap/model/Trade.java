package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class Trade {

    private Instant timestamp;

    private String currencyPair;

    private float price;

    private int volume;
}
