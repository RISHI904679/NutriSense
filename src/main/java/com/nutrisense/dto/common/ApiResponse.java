package com.nutrisense.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;

    private int status;

    private String error;

    private String message;

    private String path;

    private LocalDateTime timestamp;

    private T data;

}
