package com.sep.backend.statistics;


import org.springframework.stereotype.Service;

import com.sep.backend.statistics.StatisticsType;
import com.sep.backend.statistics.StatisticsTimeInterval;

import java.security.Principal;
import java.util.List;
import java.util.ArrayList;


@Service
public class StatisticsService {

    public List<Number> getStatisticsForYear(String type, int year, Principal principal) {

    }

    public List<Number> getStatisticsForMonth(String type, int year, int month, Principal principal) {

    }

    private static int getDayCountForMonth(int month, int year) throws IllegalArgumentException{
        return switch (month) {
            case 1, 3, 5, 7, 8, 10, 12 -> 31;
            case 4, 6, 9, 11 -> 30;
            case 2 -> ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) ? 29 : 28;
            default -> throw new IllegalArgumentException("Invalid month: " + month);
        };
    }
}
