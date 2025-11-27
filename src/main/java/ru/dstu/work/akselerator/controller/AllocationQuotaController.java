package ru.dstu.work.akselerator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.dstu.work.akselerator.dto.AllocationQuotaDto;
import ru.dstu.work.akselerator.dto.AllocationQuotasTableDto;
import ru.dstu.work.akselerator.dto.AvailableSpeciesAndRegionsDto;
import ru.dstu.work.akselerator.entity.AllocationQuota;
import ru.dstu.work.akselerator.mapper.AllocationQuotaMapper;
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

    @Autowired
    public AllocationQuotaController(AllocationQuotaService service) {
        this.service = service;
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
    public ResponseEntity<AllocationQuotasTableDto> listAsTable(Pageable pageable) {
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
     * Вернуть массив видов рыб и массив регионов,
     * доступных текущей организации по мини-квотам.
     *
     * Доступ: только FISHERMAN.
     *
     * Пример ответа:
     * {
     *   "species": [
     *     { "id": 1, "commonName": "Судак", "scientificName": "Sander lucioperca" },
     *     { "id": 2, "commonName": "Щука", "scientificName": "Esox lucius" }
     *   ],
     *   "regions": [
     *     { "id": 1, "code": "R1", "name": "Северный бассейн" },
     *     { "id": 3, "code": "R3", "name": "Каспийский бассейн" }
     *   ]
     * }
     */
    @PreAuthorize("hasRole('FISHERMAN')")
    @GetMapping("/available-species-regions")
    public ResponseEntity<AvailableSpeciesAndRegionsDto> getAvailableSpeciesAndRegions() {
        AvailableSpeciesAndRegionsDto dto =
                service.getAvailableSpeciesAndRegionsForCurrentOrg();
        return ResponseEntity.ok(dto);
    }

    /**
     * Создать новую мини-квоту.
     *
     * В рамках сервиса перед сохранением выполняется проверка,
     * что сумма мини-квот по региону и пересекающимся периодам
     * не превышает общий лимит региона (RegionTotalQuota).
     * При нарушении бросается QuotaExceededException.
     *
     * @param dto данные новой мини-квоты (организация, вид, регион, период, лимит)
     * @return 201 Created с созданной квотой
     */
    @PostMapping
    public ResponseEntity<AllocationQuotaDto> create(@Validated @RequestBody AllocationQuotaDto dto) {
        AllocationQuota entity = AllocationQuotaMapper.toEntity(dto);
        AllocationQuota saved = service.create(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(AllocationQuotaMapper.toDto(saved));
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
