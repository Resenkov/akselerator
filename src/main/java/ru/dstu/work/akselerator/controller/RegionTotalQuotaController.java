package ru.dstu.work.akselerator.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.dstu.work.akselerator.dto.RegionTotalQuotaDto;
import ru.dstu.work.akselerator.dto.TableColumnDto;
import ru.dstu.work.akselerator.dto.TableResponse;
import ru.dstu.work.akselerator.service.RegionTotalQuotaService;

import java.util.List;

/**
 * REST-контроллер для управления общими региональными квотами (RegionTotalQuota).
 *
 * Общая квота по региону задаёт максимальный суммарный лимит вылова (в кг)
 * для конкретного региона на заданный период. На основании этой квоты затем
 * проверяется сумма всех мини-квот (AllocationQuota) по организациям.
 *
 * Доступ к этому контроллеру имеют только пользователи с ролью ADMIN.
 */
@RestController
@RequestMapping("/api/region-total-quotas")
@PreAuthorize("hasRole('ADMIN')") // все методы внутри доступны только ADMIN
public class RegionTotalQuotaController {

    private final RegionTotalQuotaService service;

    public RegionTotalQuotaController(RegionTotalQuotaService service) {
        this.service = service;
    }

    /**
     * Получить список всех общих региональных квот.
     *
     * Используется для отображения текущей конфигурации лимитов по регионам
     * и периодам. Возвращает простой список без постраничной разбивки.
     *
     * Пример запроса:
     * GET /api/region-total-quotas
     *
     * @return 200 OK со списком RegionTotalQuotaDto
     */
    @GetMapping
    public ResponseEntity<TableResponse<RegionTotalQuotaDto>> list() {
        List<RegionTotalQuotaDto> data = service.list();
        List<TableColumnDto> columns = List.of(
                new TableColumnDto("ID квоты", "id"),
                new TableColumnDto("ID региона", "regionId"),
                new TableColumnDto("Название региона", "regionName"),
                new TableColumnDto("Код региона", "regionCode"),
                new TableColumnDto("Дата начала", "periodStart"),
                new TableColumnDto("Дата окончания", "periodEnd"),
                new TableColumnDto("Лимит, кг", "limitKg")
        );

        return ResponseEntity.ok(new TableResponse<>(data, columns));
    }

    /**
     * Получить одну общую региональную квоту по её идентификатору.
     *
     * Пример запроса:
     * GET /api/region-total-quotas/{id}
     *
     * @param id идентификатор региональной квоты
     * @return 200 OK с DTO, если найдено, либо 404 Not Found, если квота не найдена
     */
    @GetMapping("/{id}")
    public ResponseEntity<RegionTotalQuotaDto> get(@PathVariable Long id) {
        var dto = service.get(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    /**
     * Создать новую общую региональную квоту.
     *
     * Перед сохранением в сервисе выполняется проверка:
     * сумма уже существующих мини-квот (AllocationQuota) по региону и
     * пересекающимся периодам не должна превышать устанавливаемый общий лимит.
     * При нарушении выбрасывается QuotaExceededException с подробной информацией.
     *
     * Пример запроса:
     * POST /api/region-total-quotas
     * {
     *   "regionId": 1,
     *   "periodStart": "2025-01-01",
     *   "periodEnd": "2025-12-31",
     *   "limitKg": 100000.000
     * }
     *
     * @param dto данные новой региональной квоты
     * @return 201 Created с созданной квотой
     */
    @PostMapping
    public ResponseEntity<RegionTotalQuotaDto> create(@Valid @RequestBody RegionTotalQuotaDto dto) {
        RegionTotalQuotaDto created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Обновить существующую общую региональную квоту.
     *
     * Аналогично созданию, при обновлении выполняется проверка,
     * что сумма мини-квот не превышает новый общий лимит региона.
     * В случае нарушения лимита будет выброшено исключение QuotaExceededException.
     *
     * Пример запроса:
     * PUT /api/region-total-quotas/{id}
     * {
     *   "regionId": 1,
     *   "periodStart": "2025-01-01",
     *   "periodEnd": "2025-12-31",
     *   "limitKg": 120000.000
     * }
     *
     * @param id  идентификатор обновляемой квоты
     * @param dto новые значения для региональной квоты
     * @return 200 OK с обновлённой DTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<RegionTotalQuotaDto> update(@PathVariable Long id,
                                                      @Valid @RequestBody RegionTotalQuotaDto dto) {
        RegionTotalQuotaDto updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Удалить общую региональную квоту.
     *
     * После удаления данной записи система перестанет использовать
     * этот общий лимит при проверке мини-квот. Использовать осторожно,
     * чтобы не нарушить консистентность настроек квот.
     *
     * Пример запроса:
     * DELETE /api/region-total-quotas/{id}
     *
     * @param id идентификатор удаляемой квоты
     * @return 204 No Content в случае успешного удаления
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
