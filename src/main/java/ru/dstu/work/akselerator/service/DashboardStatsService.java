package ru.dstu.work.akselerator.service;

import ru.dstu.work.akselerator.dto.DashboardCardsDto;

import java.time.Year;

public interface DashboardStatsService {
    DashboardCardsDto getCardsStats(Year year);

    DashboardCardsDto getCardsStatsAllTime();
}
