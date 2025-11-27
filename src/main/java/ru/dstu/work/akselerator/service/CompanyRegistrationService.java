package ru.dstu.work.akselerator.service;

import ru.dstu.work.akselerator.dto.CompanyRegistrationRequest;
import ru.dstu.work.akselerator.dto.CompanyRegistrationResponse;

public interface CompanyRegistrationService {
    CompanyRegistrationResponse registerCompany(CompanyRegistrationRequest request);
}
