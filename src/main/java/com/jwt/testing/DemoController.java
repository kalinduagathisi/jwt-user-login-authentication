package com.jwt.testing;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/testing")
public class DemoController {

    @GetMapping("/hello")
    public ResponseEntity<String> accessSucceed(){
        return ResponseEntity.ok("Helloo from secured endpoint");
    }
}
