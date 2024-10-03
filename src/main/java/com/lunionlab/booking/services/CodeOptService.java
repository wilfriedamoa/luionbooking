package com.lunionlab.booking.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Random;

import com.lunionlab.booking.repository.CodeOtpRespository;

@Service
public class CodeOptService {

    @Autowired
    CodeOtpRespository codeOtpRespository;

    /**
     * The function generates a random code of a specified length that does not
     * already exist in a
     * repository.
     * 
     * @param length The `length` parameter in the `generateCodeOtp` method
     *               specifies the length of the
     *               OTP (One Time Password) code that will be generated. This code
     *               will be a string of numbers with
     *               the specified length.
     * @return The method `generateCodeOtp` returns a randomly generated code of the
     *         specified length
     *         that does not already exist in the `codeOtpRespository`.
     */
    public String generateCodeOtp(int length) {
        Random random = new Random();
        String code;
        while (true) {
            StringBuilder codeBuilder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                codeBuilder.append(random.nextInt(10));
            }
            code = codeBuilder.toString();
            Boolean codeExit = codeOtpRespository.existsByCode(code);
            if (!codeExit) {
                break;
            }
        }
        return code;

    }
}
