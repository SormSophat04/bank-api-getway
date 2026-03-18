package com.lolc.api.getway.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class BCryptTool {
    private BCryptTool() {
    }

    public static void main(String[] args) {
        String raw = args.length > 0 ? args[0] : "123456";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode(raw));
    }
}
