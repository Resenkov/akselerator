package ru.dstu.work.akselerator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.dstu.work.akselerator.dto.AllocationQuotaDto;
import ru.dstu.work.akselerator.dto.QuotaUsageSummaryDto;
import ru.dstu.work.akselerator.dto.RegionTotalQuotaDto;
import ru.dstu.work.akselerator.entity.AllocationQuota;
import ru.dstu.work.akselerator.entity.User;
import ru.dstu.work.akselerator.mapper.AllocationQuotaMapper;
import ru.dstu.work.akselerator.repository.UserRepository;
import ru.dstu.work.akselerator.service.AllocationQuotaService;
import ru.dstu.work.akselerator.service.RegionTotalQuotaService;

import java.util.Collections;
import java.util.List;

/**
 * Контроллер для просмотра квот текущей компанией (роль FISHERMAN).
 *
 * Здесь рыбак может:
 *  - увидеть все мини-квоты своей организации;
 *  - посмотреть общие квоты по своему региону.
 *
 * ВАЖНО: этот контроллер только для чтения (read-only).
 * Создание/изменение/удаление квот остаётся в админских контроллерах.
 */
@RestController
@RequestMapping("/api/my/quotas")
public class MyQuotaController {

    private final AllocationQuotaService allocationQuotaService;
    private final RegionTotalQuotaService regionTotalQuotaService;
    private final UserRepository userRepository;

    @Autowired
    public MyQuotaController(AllocationQuotaService allocationQuotaService,
                             RegionTotalQuotaService regionTotalQuotaService,
                             UserRepository userRepository) {
        this.allocationQuotaService = allocationQuotaService;
        this.regionTotalQuotaService = regionTotalQuotaService;
        this.userRepository = userRepository;
    }

    /**
     * Вспомогательный метод: получить текущего аутентифицированного пользователя по токену.
     *
     * Используется для того, чтобы определить:
     *  - организацию пользователя (organization),
     *  - регион организации.
     *
     * @return сущность User из БД
     */
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));
    }

    /**
     * Получить все мини-квоты (AllocationQuota) для организации текущего пользователя.
     *
     * Доступ: только роль FISHERMAN.
     *
     * Пример запроса:
     * GET /api/my/quotas/allocation?page=0&size=20
     *
     * @param pageable параметры постраничной выборки
     * @return 200 OK со страницей мини-квот текущей компании
     */
    @PreAuthorize("hasRole('FISHERMAN')")
    @GetMapping("/allocation")
    public ResponseEntity<Page<AllocationQuotaDto>> myAllocationQuotas(Pageable pageable) {
        User current = getCurrentUser();

        if (current.getOrganization() == null) {
            Page<AllocationQuotaDto> emptyPage = Page.empty(pageable);
            return ResponseEntity.ok(emptyPage);
        }

        Long orgId = current.getOrganization().getId();
        Page<AllocationQuota> page = allocationQuotaService.findByOrganizationId(orgId, pageable);
        Page<AllocationQuotaDto> dtoPage = page.map(AllocationQuotaMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }

    /**
     * Сводка по всем мини-квотам текущей организации:
     * вид, регион, сколько уже выловлено и общий лимит.
     *
     * Доступ: FISHERMAN.
     */
    @PreAuthorize("hasRole('FISHERMAN')")
    @GetMapping("/quota-usage")
    public ResponseEntity<List<QuotaUsageSummaryDto>> getMyQuotaUsage() {
        List<QuotaUsageSummaryDto> list = allocationQuotaService.getMyQuotaUsageSummary();
        return ResponseEntity.ok(list);
    }

    /**
     * Получить общие квоты (RegionTotalQuota) для региона организации текущего пользователя.
     *
     * Доступ: только роль FISHERMAN.
     *
     * Возвращается список всех региональных квот для региона,
     * к которому привязана организация пользователя.
     *
     * Пример запроса:
     * GET /api/my/quotas/region-total
     *
     * @return 200 OK со списком региональных квот, либо пустой список,
     *         если у пользователя нет организации или региона
     */
    @PreAuthorize("hasRole('FISHERMAN')")
    @GetMapping("/region-total")
    public ResponseEntity<List<RegionTotalQuotaDto>> myRegionTotalQuotas() {
        User current = getCurrentUser();

        if (current.getOrganization() == null || current.getOrganization().getRegion() == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        Long regionId = current.getOrganization().getRegion().getId();
        List<RegionTotalQuotaDto> quotas = regionTotalQuotaService.listByRegion(regionId);
        return ResponseEntity.ok(quotas);
    }
}
