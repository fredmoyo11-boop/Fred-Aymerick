package com.sep.backend;


import com.sep.backend.account.CustomerRepository;
import com.sep.backend.account.DriverRepository;
import com.sep.backend.entity.*;
import com.sep.backend.statistics.*;
import com.sep.backend.trip.history.TripHistoryRepository;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.security.Principal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

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

    @Autowired
    private CustomerRepository customerRepository;


    @BeforeEach
    public void setup() {
        tripHistoryRepository.deleteAll();
        tripHistoryRepository.flush();

        driverRepository.deleteAll();
        driverRepository.flush();

        customerRepository.deleteAll();
        customerRepository.flush();

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setFirstName("DUMMY");
        customerEntity.setLastName("DUMMY");
        customerEntity.setEmail("dummy@gmail.com");
        customerEntity.setPassword("DUMMY");
        customerEntity.setUsername("DUMMY");
        customerEntity.setBirthday("2000-01-01");
        customerEntity.setVerified(true);
        customerEntity.setBalance(100.0);
        customerRepository.save(customerEntity);

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

        TripHistoryEntity tripHistory1 = new TripHistoryEntity();
        tripHistory1.setTripOfferId(1L);
        tripHistory1.setDriverRating(5);
        tripHistory1.setDriver(driver1);
        tripHistory1.setCustomer(customerEntity);
        //fields to change
        tripHistory1.setDistance(20.0D);
        tripHistory1.setDuration(100);
        tripHistory1.setPrice(10.0D);
        tripHistory1.setCustomerRating(5);
        tripHistory1.setEndTime(LocalDateTime.of(2000, 10, 20, 4, 20, 59));
        //end
        tripHistoryRepository.save(tripHistory1);

        TripHistoryEntity tripHistory2 = new TripHistoryEntity();
        tripHistory2.setTripOfferId(2L);
        tripHistory2.setDriverRating(5);
        tripHistory2.setDriver(driver1);
        tripHistory2.setCustomer(customerEntity);
        //fields to change
        tripHistory2.setDistance(40.0D);
        tripHistory2.setDuration(300);
        tripHistory2.setPrice(20.0D);
        tripHistory2.setCustomerRating(4);
        tripHistory2.setEndTime(LocalDateTime.of(2000, 10, 20, 4, 20, 59));
        //end
        tripHistoryRepository.save(tripHistory2);

        TripHistoryEntity tripHistory3 = new TripHistoryEntity();
        tripHistory3.setTripOfferId(3L);
        tripHistory3.setDriverRating(5);
        tripHistory3.setDriver(driver1);
        tripHistory3.setCustomer(customerEntity);
        //fields to change
        tripHistory3.setDistance(33.89D);
        tripHistory3.setDuration(324);
        tripHistory3.setPrice(21.9D);
        tripHistory3.setCustomerRating(3);
        tripHistory3.setEndTime(LocalDateTime.of(2000, 9, 20, 4, 20, 59));
        //end
        tripHistoryRepository.save(tripHistory3);

        TripHistoryEntity tripHistory4 = new TripHistoryEntity();
        tripHistory4.setTripOfferId(4L);
        tripHistory4.setDriverRating(5);
        tripHistory4.setDriver(driver1);
        tripHistory4.setCustomer(customerEntity);
        //fields to change
        tripHistory4.setDistance(21.45D);
        tripHistory4.setDuration(77553);
        tripHistory4.setPrice(444.44D);
        tripHistory4.setCustomerRating(2);
        tripHistory4.setEndTime(LocalDateTime.of(2001, 10, 20, 5, 33, 44));
        //end
        tripHistoryRepository.save(tripHistory4);

        TripHistoryEntity tripHistory5 = new TripHistoryEntity();
        tripHistory5.setTripOfferId(5L);
        tripHistory5.setDriverRating(5);
        tripHistory5.setDriver(driver1);
        tripHistory5.setCustomer(customerEntity);
        //fields to change
        tripHistory5.setDistance(62146.131D);
        tripHistory5.setDuration(9182);
        tripHistory5.setPrice(9874.03D);
        tripHistory5.setCustomerRating(1);
        tripHistory5.setEndTime(LocalDateTime.of(2001, 10, 31, 23, 59, 59));
        //end
        tripHistoryRepository.save(tripHistory5);

        principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("asdf@mail.com");
    }


    @Test
    public void functionRunTest() {
        statisticsService.getStatisticsForYear(StatisticsType.DISTANCE, 2000, principal);
        statisticsService.getStatisticsForYear(StatisticsType.RATING, 2001, principal);
        statisticsService.getStatisticsForMonth(StatisticsType.TIME, 2002, 1, principal);
        statisticsService.getStatisticsForMonth(StatisticsType.RATING, 2003, 9, principal);
    }

    @Test
    public void yearTest() {
        //throw new RuntimeException(statisticsService.getStatisticsForYear(StatisticsType.DISTANCE, 2000, principal).toString());

        assertEquals(20.0D + 40.0D, statisticsService.getStatisticsForYear(StatisticsType.DISTANCE, 2000, principal).get(10-1).doubleValue());
        assertEquals(33.89D, statisticsService.getStatisticsForYear(StatisticsType.DISTANCE, 2000, principal).get(9-1).doubleValue());
        assertEquals(21.45D + 62146.131D, statisticsService.getStatisticsForYear(StatisticsType.DISTANCE, 2001, principal).get(10-1).doubleValue());

        assertEquals(10.0D + 20.0D, statisticsService.getStatisticsForYear(StatisticsType.REVENUE, 2000, principal).get(10-1).doubleValue());
        assertEquals(21.9D, statisticsService.getStatisticsForYear(StatisticsType.REVENUE, 2000, principal).get(9-1).doubleValue());

        // floats
        //assertEquals(444.44D + 9874.03D, statisticsService.getStatisticsForYear(StatisticsType.REVENUE, 2001, principal).get(10-1).doubleValue());

        assertEquals(100 + 300, statisticsService.getStatisticsForYear(StatisticsType.TIME, 2000, principal).get(10-1).doubleValue());
        assertEquals(324, statisticsService.getStatisticsForYear(StatisticsType.TIME, 2000, principal).get(9-1).doubleValue());
        assertEquals(77553 + 9182, statisticsService.getStatisticsForYear(StatisticsType.TIME, 2001, principal).get(10-1).doubleValue());

        assertEquals((5D + 4D)/2D , statisticsService.getStatisticsForYear(StatisticsType.RATING, 2000, principal).get(10-1).doubleValue());
        assertEquals(3D , statisticsService.getStatisticsForYear(StatisticsType.RATING, 2000, principal).get(9-1).doubleValue());
        assertEquals((2D + 1D)/2D , statisticsService.getStatisticsForYear(StatisticsType.RATING, 2001, principal).get(10-1).doubleValue());
    }

    @Test
    public void monthTest() {
        //assertEquals();
    }
}
