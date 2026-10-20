package com.enso.service;

public interface SmsService {
    void sendOtp(String phoneNumber, String code);
}
