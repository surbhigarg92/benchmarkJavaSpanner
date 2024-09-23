package com.surbhidemo.spanner.rest;
import static com.google.cloud.opentelemetry.detection.GCPPlatformDetector.SupportedPlatform.GOOGLE_KUBERNETES_ENGINE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.opentelemetry.detection.AttributeKeys;
import com.google.cloud.opentelemetry.detection.DetectedPlatform;
import com.google.cloud.opentelemetry.detection.GCPPlatformDetector;
import com.surbhidemo.spanner.restInterfaces.AppServiceRequestBoundary;

@RestController
public class TestController {
    
    @Autowired
    private final AppServiceRequestBoundary appService;

    public TestController(AppServiceRequestBoundary appService) {
        this.appService = appService;
    }

    
    @GetMapping("/test/parallelCycles/{parallelCycles}/iterations/{iterations}")
    public String test(@PathVariable(value = "parallelCycles") int parallelCycles,
    @PathVariable(value = "iterations") int iterations) {
      appService.run(parallelCycles, iterations);
      return String.format("{\"status\": \"ok\", \"parallelCycles\": \"%s\", \"iterations\": \"%s\"}", parallelCycles, iterations);
    }

    @GetMapping("/test/clientLocation")
    public String clientLocation() {
      GCPPlatformDetector detector = GCPPlatformDetector.DEFAULT_INSTANCE;
    DetectedPlatform detectedPlatform = detector.detectPlatform();
    // All platform except GKE uses "cloud_region" for region attribute.
    String region = detectedPlatform.getAttributes().get("cloud_region");
    if (detectedPlatform.getSupportedPlatform() == GOOGLE_KUBERNETES_ENGINE) {
      System.out.println("GKE_CLUSTER_LOCATION " + detectedPlatform.getAttributes().get(AttributeKeys.GKE_CLUSTER_LOCATION));
      region = detectedPlatform.getAttributes().get(AttributeKeys.GKE_LOCATION_TYPE_REGION);
    }
    return region == null ? "global" : region;
    }
}
