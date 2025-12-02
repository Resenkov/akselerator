package ru.dstu.work.akselerator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.dstu.work.akselerator.dto.*;
import ru.dstu.work.akselerator.entity.AllocationQuota;
import ru.dstu.work.akselerator.mapper.AllocationQuotaMapper;
import ru.dstu.work.akselerator.repository.FishSpeciesRepository;
import ru.dstu.work.akselerator.repository.FishingRegionRepository;
import ru.dstu.work.akselerator.repository.OrganizationRepository;
import ru.dstu.work.akselerator.service.AllocationQuotaService;

/**
 * REST-контроллер для управления мини-квотами (AllocationQuota).
 *
 * Мини-квоты задают лимит вылова для конкретной организации
 * по виду рыбы и региону на заданный период.
 *
 * Доступ к этому контроллеру должен быть только у пользователей
 * с ролью ADMIN, так как изменение квот влияет на расчёт
 * соблюдения ограничений по вылову.
 */
@RestController
@RequestMapping("/api/allocation-quotas")
@PreAuthorize("hasRole('ADMIN')")
public class AllocationQuotaController {

    private final AllocationQuotaService service;
    private final FishSpeciesRepository fishSpeciesRepository;
    private final FishingRegionRepository fishingRegionRepository;
    private final OrganizationRepository organizationRepository;
    @Autowired
    public AllocationQuotaController(AllocationQuotaService service, FishSpeciesRepository fishSpeciesRepository, FishingRegionRepository fishingRegionRepository, OrganizationRepository organizationRepository) {
        this.service = service;
        this.fishSpeciesRepository = fishSpeciesRepository;
        this.fishingRegionRepository = fishingRegionRepository;
        this.organizationRepository = organizationRepository;
    }

    /**
     * Получить постраничный список всех мини-квот.
     *
     * Пример вызова:
     * GET /api/allocation-quotas?page=0&size=20
     *
     * @param pageable параметры постраничной выборки (страница, размер, сортировка)
     * @return страница с DTO мини-квот
     */
    @GetMapping
    public ResponseEntity<Page<AllocationQuotaDto>> list(Pageable pageable) {
        Page<AllocationQuota> page = service.list(pageable);
        Page<AllocationQuotaDto> dtoPage = page.map(AllocationQuotaMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/table")
    public ResponseEntity<AllocationQuotasTableDto> listTable(Pageable pageable) {
        AllocationQuotasTableDto table = service.listAsTable(pageable);
        return ResponseEntity.ok(table);
    }


    /**
     * Получить одну мини-квоту по её идентификатору.
     *
     * @param id идентификатор мини-квоты
     * @return 200 OK с DTO квоты, либо 404 Not Found, если квота не найдена
     */
    @GetMapping("/{id}")
    public ResponseEntity<AllocationQuotaDto> get(@PathVariable Long id) {
        return service.getById(id)
                .map(AllocationQuotaMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Получить постраничный список мини-квот для конкретной организации.
     *
     * Используется, например, для отображения того, какие лимиты
     * установлены компании по видам и регионам.
     *
     * @param id       идентификатор организации
     * @param pageable параметры постраничной выборки
     * @return страница с мини-квотами организации
     */
    @GetMapping("/organizations/{id}")
    public ResponseEntity<Page<AllocationQuotaDto>> findByOrganizationId(@PathVariable Long id,
                                                                         Pageable pageable) {
        Page<AllocationQuota> page = service.findByOrganizationId(id, pageable);
        Page<AllocationQuotaDto> dtoPage = page.map(AllocationQuotaMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }

    /**
     * Обновить существующую мини-квоту.
     *
     * Перед сохранением также выполняется проверка на превышение
     * общего лимита региона (RegionTotalQuota), поэтому изменение
     * лимита может привести к ошибке QuotaExceededException.
     *
     * @param id  идентификатор обновляемой квоты
     * @param dto новые данные квоты
     * @return 200 OK с обновлённой квотой
     */
    @PutMapping("/{id}")
    public ResponseEntity<AllocationQuotaDto> update(@PathVariable Long id,
                                                     @Validated @RequestBody AllocationQuotaDto dto) {
        AllocationQuota entity = AllocationQuotaMapper.toEntity(dto);
        entity.setId(id);
        AllocationQuota updated = service.update(entity);
        return ResponseEntity.ok(AllocationQuotaMapper.toDto(updated));
    }

    /**
     * Метаданные для формы мини-квот:
     * все виды рыб, все регионы и все организации (компании).
     */
    @PreAuthorize("hasRole('FISHERMAN')")
    @GetMapping("/meta")
    public ResponseEntity<AllocationQuotaMetaDto> getAllocationQuotaMeta() {
        var species = fishSpeciesRepository.findAll().stream()
                .map(s -> new SimpleFishSpeciesDto(
                        s.getId(),
                        s.getScientificName(),
                        s.getCommonName(),
                        s.isEndangered()
                ))
                .toList();

        var regions = fishingRegionRepository.findAll().stream()
                .map(r -> new SimpleFishingRegionDto(
                        r.getId(),
                        r.getCode(),
                        r.getName()
                ))
                .toList();

        var orgs = organizationRepository.findAll().stream()
                .map(o -> new SimpleOrganizationDto(
                        o.getId(),
                        o.getName(),
                        o.getOrgType(),
                        o.getInn()
                ))
                .toList();

        AllocationQuotaMetaDto dto = new AllocationQuotaMetaDto();
        dto.setSpecies(species);
        dto.setRegions(regions);
        dto.setOrganizations(orgs);

        return ResponseEntity.ok(dto);
    }

    /**
     * Удалить мини-квоту по идентификатору.
     *
     * Операция безвозвратна. После удаления квоты,
     * при последующих расчётах лимиты для соответствующей
     * организации/вида/региона будут считаться без неё.
     *
     * @param id идентификатор квоты
     * @return 204 No Content в случае успешного удаления
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
