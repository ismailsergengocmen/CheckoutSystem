package com.example.checkoutCase.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class RequestResponse {
    private boolean result;
    private String message;
}
