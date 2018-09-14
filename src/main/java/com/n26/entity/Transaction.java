package com.n26.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Transaction implements Serializable {

    private static final long serialVersionUID = 6175052636025900629L;

    private final BigDecimal amount;
    private final ZonedDateTime timestamp;
}
