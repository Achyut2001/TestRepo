package com.tdit.dataprovideservice.service;

import com.tdit.dataprovideservice.entity.Property;
import com.tdit.dataprovideservice.entity.Status;
import com.tdit.dataprovideservice.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.tdit.dataprovideservice.constants.Constants.ERROR_PROPERTY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PropertyServiceAdmin {

    private final PropertyRepository propertyRepository;

    public void updatePropertyStatus(Long id, String status) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(ERROR_PROPERTY_NOT_FOUND));
        property.setStatus(status);
        propertyRepository.save(property);
    }

    public List<Property> getRejectedProperties() {
        return propertyRepository.findByStatus(Status.REJECTED.name());
    }
}
