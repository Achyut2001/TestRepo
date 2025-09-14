package com.tdit.dataprovideservice.controller;

import com.tdit.dataprovideservice.constants.Constants;
import com.tdit.dataprovideservice.entity.Property;
import com.tdit.dataprovideservice.service.PropertyServiceAdmin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.tdit.dataprovideservice.constants.Constants.Property_status_updated_to;

@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@RestController
public class AdminController {

    private final PropertyServiceAdmin propertyServiceAdmin;

    @PutMapping("/admin/{id}/status")
    public ResponseEntity<String> updatePropertyStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        if (!status.equalsIgnoreCase("APPROVED") && !status.equalsIgnoreCase("REJECTED")) {
            return ResponseEntity.badRequest().body(Constants.ERROR_INVALID_STATUS);
        }
        propertyServiceAdmin.updatePropertyStatus(id, status.toUpperCase());
        return ResponseEntity.ok(Property_status_updated_to + status.toUpperCase());
    }


    @GetMapping("/properties/rejected")
    public ResponseEntity<List<Property>> getRejectedProperties() {
        return ResponseEntity.ok(propertyServiceAdmin.getRejectedProperties());
    }

    //need to implment jav 17 featues 
    //need to add logger properly
}
