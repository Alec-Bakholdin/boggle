package com.alecbakholdin.boggle.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class ApiError {
    @NonNull private final int status;
    @NonNull private final String exceptionType;
    @NonNull private final String message;
}
