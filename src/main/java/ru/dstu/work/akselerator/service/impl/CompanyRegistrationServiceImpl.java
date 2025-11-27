package ru.dstu.work.akselerator.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dstu.work.akselerator.dto.CompanyRegistrationRequest;
import ru.dstu.work.akselerator.dto.CompanyRegistrationResponse;
import ru.dstu.work.akselerator.entity.FishingRegion;
import ru.dstu.work.akselerator.entity.Organization;
import ru.dstu.work.akselerator.entity.User;
import ru.dstu.work.akselerator.repository.FishingRegionRepository;
import ru.dstu.work.akselerator.repository.OrganizationRepository;
import ru.dstu.work.akselerator.repository.UserRepository;
import ru.dstu.work.akselerator.service.CompanyRegistrationService;

@Service
@RequiredArgsConstructor
public class CompanyRegistrationServiceImpl implements CompanyRegistrationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final FishingRegionRepository fishingRegionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public CompanyRegistrationResponse registerCompany(CompanyRegistrationRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Пользователь с таким логином уже существует");
        }

        FishingRegion region = fishingRegionRepository.findById(request.getRegionId())
                .orElseThrow(() -> new IllegalArgumentException("Регион не найден"));

        Organization org = new Organization();
        org.setName(request.getOrgName());
        org.setOrgType(request.getOrgType());
        org.setInn(request.getInn());
        org.setRegion(region);

        Organization savedOrg = organizationRepository.save(org);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setOrganization(savedOrg);
        user.setActive(true);


        User savedUser = userRepository.save(user);

        CompanyRegistrationResponse response = new CompanyRegistrationResponse();
        response.setOrganizationId(savedOrg.getId());
        response.setOrganizationName(savedOrg.getName());
        response.setUserId(savedUser.getId());
        response.setUsername(savedUser.getUsername());
        response.setEmail(savedUser.getEmail());

        return response;
    }
}
