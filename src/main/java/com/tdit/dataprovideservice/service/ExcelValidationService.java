package com.tdit.dataprovideservice.service;

import com.tdit.dataprovideservice.dto.ExcelRowData;
import com.tdit.dataprovideservice.constants.Constants;
import com.tdit.dataprovideservice.entity.UploadAudit;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExcelValidationService {

    private final Validator validator;

    public UploadAudit.RowResult validateRow(ExcelRowData rowData, int rowNumber) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();


        Set<ConstraintViolation<ExcelRowData>> violations = validator.validate(rowData);
        for (ConstraintViolation<ExcelRowData> violation : violations) {
            errors.add(violation.getMessage());
        }


        addBusinessValidations(rowData, errors, warnings);

        boolean success = errors.isEmpty();

        return UploadAudit.RowResult.builder()
                .success(success)
                .errorMessage(errors.isEmpty() ? null : String.join("; ", errors))
                .warningMessage(warnings.isEmpty() ? null : String.join("; ", warnings))
                .rowNumber(rowNumber)
                .build();
    }

    private void addBusinessValidations(ExcelRowData rowData, List<String> errors, List<String> warnings) {

        if (rowData.getCurrency() != null) {
            if (!Constants.PREFERRED_CURRENCIES.contains(rowData.getCurrency().toUpperCase())) {
                warnings.add(String.format(Constants.WARN_CURRENCY_NOT_PREFERRED, rowData.getCurrency()));
            }
        }


        if (rowData.getLatitude() != null && !rowData.getLatitude().trim().isEmpty()) {
            try {
                double lat = Double.parseDouble(rowData.getLatitude());
                if (lat < Constants.LAT_MIN || lat > Constants.LAT_MAX) {
                    errors.add(Constants.ERROR_LAT_RANGE);
                }
            } catch (NumberFormatException e) {
                errors.add(Constants.ERROR_LAT_NUMBER);
            }
        }

        if (rowData.getLongitude() != null && !rowData.getLongitude().trim().isEmpty()) {
            try {
                double lng = Double.parseDouble(rowData.getLongitude());
                if (lng < Constants.LNG_MIN || lng > Constants.LNG_MAX) {
                    errors.add(Constants.ERROR_LNG_RANGE);
                }
            } catch (NumberFormatException e) {
                errors.add(Constants.ERROR_LNG_NUMBER);
            }
        }


        if (rowData.getBasePrice() != null && !rowData.getBasePrice().trim().isEmpty()) {
            try {
                double price = Double.parseDouble(rowData.getBasePrice());
                if (price <= Constants.BASE_PRICE_MIN) {
                    errors.add(Constants.ERROR_BASE_PRICE_RANGE);
                }
            } catch (NumberFormatException e) {
                errors.add(Constants.ERROR_BASE_PRICE_NUMBER);
            }
        }


        if (rowData.getPropertyUrl() != null && !rowData.getPropertyUrl().trim().isEmpty()) {
            String url = rowData.getPropertyUrl().toLowerCase();
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                warnings.add(Constants.WARN_PROPERTY_URL);
            }
        }

        if (rowData.getHostId() != null && rowData.getHostName() != null) {
            if (rowData.getHostId().trim().isEmpty() && !rowData.getHostName().trim().isEmpty()) {
                warnings.add(Constants.WARN_HOST_ID_EMPTY);
            }
        }
    }
}
