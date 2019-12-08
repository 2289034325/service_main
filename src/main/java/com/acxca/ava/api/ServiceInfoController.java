package com.acxca.ava.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "info",produces= MediaType.APPLICATION_JSON_VALUE)
public class ServiceInfoController {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @RequestMapping(path = "env",method = RequestMethod.GET)
    public ResponseEntity<Object> getEnv() {
        return new ResponseEntity(activeProfile, HttpStatus.OK);
    }
}
