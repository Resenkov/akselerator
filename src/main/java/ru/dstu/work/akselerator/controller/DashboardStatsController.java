package ru.dstu.work.akselerator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.dstu.work.akselerator.dto.DashboardCardsDto;
import ru.dstu.work.akselerator.service.DashboardStatsService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardStatsController {

    private final DashboardStatsService dashboardStatsService;

    public DashboardStatsController(DashboardStatsService dashboardStatsService) {
        this.dashboardStatsService = dashboardStatsService;
    }

    @GetMapping("/cards")
    public ResponseEntity<DashboardCardsDto> getCardsStats(@RequestParam(value = "date", required = false) LocalDate date) {
        DashboardCardsDto stats = dashboardStatsService.getCardsStats(date);
        return ResponseEntity.ok(stats);
    }
}
