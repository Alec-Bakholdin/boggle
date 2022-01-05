package com.alecbakholdin.boggle.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class Point {
    @NonNull private int i;
    @NonNull private int j;
}
