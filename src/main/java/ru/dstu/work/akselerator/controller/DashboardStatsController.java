package ru.dstu.work.akselerator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.dstu.work.akselerator.dto.DashboardCardsDto;
import ru.dstu.work.akselerator.service.DashboardStatsService;

import java.time.Year;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardStatsController {

    private final DashboardStatsService dashboardStatsService;

    public DashboardStatsController(DashboardStatsService dashboardStatsService) {
        this.dashboardStatsService = dashboardStatsService;
    }

    @GetMapping("/cards")
    public ResponseEntity<DashboardCardsDto> getCardsStats(@RequestParam(value = "year", required = false) Year year) {
        DashboardCardsDto stats = dashboardStatsService.getCardsStats(year);
        return ResponseEntity.ok(stats);
    }
}
