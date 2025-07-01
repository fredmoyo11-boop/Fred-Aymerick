package com.sep.backend;


import com.sep.backend.account.DriverRepository;
import com.sep.backend.entity.DriverEntity;
import com.sep.backend.statistics.*;
import com.sep.backend.trip.history.TripHistoryRepository;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.security.Principal;

@SpringBootTest
@ActiveProfiles("testaaaaaaaa")
public class StatisticsTest {

    @Mock
    private Principal principal;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private TripHistoryRepository tripHistoryRepository;


    @BeforeEach
    public void setup() {
        tripHistoryRepository.deleteAll();
        tripHistoryRepository.flush();

        driverRepository.deleteAll();
        driverRepository.flush();


        DriverEntity driver1 = new DriverEntity();
        driver1.setFirstName("John");
        driver1.setLastName("Doe");
        driver1.setEmail("asdf@mail.com");
        driver1.setPassword("password");
        driver1.setUsername("johndoe");
        driver1.setBirthday("2000-01-01");
        driver1.setVerified(true);
        driver1.setBalance(100.0);
        driverRepository.save(driver1);
    }


    @Test
    public void functionRunTest() {
        principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("asdf@mail.com");

        statisticsService.getStatisticsForYear(StatisticsType.DISTANCE, 2000, principal);
        statisticsService.getStatisticsForYear(StatisticsType.RATING, 2001, principal);
        statisticsService.getStatisticsForMonth(StatisticsType.TIME, 2002, 1, principal);
        statisticsService.getStatisticsForMonth(StatisticsType.RATING, 2003, 9, principal);
    }
}
