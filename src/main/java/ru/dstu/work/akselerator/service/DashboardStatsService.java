package ru.dstu.work.akselerator.service;

import ru.dstu.work.akselerator.dto.DashboardCardsDto;

import java.time.LocalDate;

public interface DashboardStatsService {
    DashboardCardsDto getCardsStats(LocalDate date);
}
