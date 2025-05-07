package com.sep.backend.account;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum CarType {
    SMALL,
    MEDIUM,
    DELUXE
}
