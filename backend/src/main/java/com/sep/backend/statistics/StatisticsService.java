package com.sep.backend.statistics;


import com.sep.backend.trip.history.TripHistoryRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;


@Service
public class StatisticsService {
    private final TripHistoryRepository tripHistoryRepository;

    public StatisticsService(TripHistoryRepository tripHistoryRepository) {
        this.tripHistoryRepository = tripHistoryRepository;
    }

    public List<Number> getStatisticsForYear(String type, int year, Principal principal) {
        ArrayList<Number> result = new ArrayList<>();
        for(int month = 1; month <= 12; month++) {
            LocalDateTime lowerTime = LocalDateTime.of(year, month, 1, 0, 0, 0);
            LocalDateTime upperTime = LocalDateTime.of(year, month, getDayCountForMonth(year, month), 23, 59, 59);
            result.add(getValueForTypeAndTime(type, lowerTime, upperTime, principal));
        }
        return result;
    }

    public List<Number> getStatisticsForMonth(String type, int year, int month, Principal principal) {
        ArrayList<Number> result = new ArrayList<>();
        int dayCount = getDayCountForMonth(year, month);
        for(int day = 1; day <= dayCount; day++) {
            LocalDateTime lowerTime = LocalDateTime.of(year, month, day, 0, 0, 0);
            LocalDateTime upperTime = LocalDateTime.of(year, month, day, 23, 59, 59);
            result.add(getValueForTypeAndTime(type, lowerTime, upperTime, principal));
        }
        return result;
    }

    private Number getValueForTypeAndTime(String type, LocalDateTime lowerTime, LocalDateTime upperTime, Principal principal) throws IllegalArgumentException{
        return switch (type) {
            case StatisticsType.DISTANCE, StatisticsType.TIME, StatisticsType.REVENUE -> tripHistoryRepository.getSumStatisticsByDriver(principal.getName(), type, lowerTime, upperTime);
            case StatisticsType.RATING -> tripHistoryRepository.getAvgStatisticsByDriver(principal.getName(), type, lowerTime, upperTime);
            default -> throw new IllegalStateException("Unexpected type: " + type);
        };
    }

    private static int getDayCountForMonth(int year, int month) throws IllegalArgumentException{
        return switch (month) {
            case 1, 3, 5, 7, 8, 10, 12 -> 31;
            case 4, 6, 9, 11 -> 30;
            case 2 -> ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) ? 29 : 28;
            default -> throw new IllegalArgumentException("Invalid month: " + month);
        };
    }
}
