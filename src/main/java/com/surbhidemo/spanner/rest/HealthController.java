package com.surbhidemo.spanner.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/health/ready")
    public String ready() {
      return "{\"status\": \"ok\"}";
    }

    @GetMapping("/health/live")
    public String live() {
      return "{\"status\": \"ok\"}";
    }
}
