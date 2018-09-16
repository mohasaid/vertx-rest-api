package com.moha.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moha.utils.serializer.ZonedDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.DecimalMin;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Transaction implements Serializable {

    private static final long serialVersionUID = 6175052636025900629L;

    @DecimalMin("0.00")
    @JsonFormat(shape = STRING)
    private final BigDecimal amount;

    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private final ZonedDateTime timestamp;
}
