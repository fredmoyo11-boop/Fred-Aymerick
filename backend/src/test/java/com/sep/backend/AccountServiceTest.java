package com.sep.backend;

import com.sep.backend.account.*;
import com.sep.backend.entity.CustomerEntity;
import com.sep.backend.entity.DriverEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {

    @Autowired
    private AccountService accountService;
    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        CustomerEntity customer = new CustomerEntity();
        customer.setUsername("test");
        customer.setEmail("test@example.com");
        customer.setPassword("PASSWORD");
        customer.setFirstName("Fred");
        customer.setLastName("hello");
        customer.setBirthday("1999-01-01");
        customer.setVerified(true);


        customerRepository.save(customer);


        DriverEntity driver = new DriverEntity();
        driver.setUsername("hii");
        driver.setEmail("ok@example.com");
        driver.setBirthday("1999-01-01");
        driver.setPassword("password");
        driver.setFirstName("Fred");
        driver.setLastName("hello");
        driver.setVerified(true);
        driver.setCarType("MEDIUM");
        driver.setTotalNumberOfRides(5);

        driverRepository.save(driver);


    }

    @AfterEach
    void tearDown() {
        customerRepository.deleteAll();
        driverRepository.deleteAll();
    }

    @Test
    void shouldSaveAccount() {

        List<AccountDTO> driverList = accountService.SearchUser("hi");
        AccountDTO driver = driverList.getFirst();

        assertEquals("hii", driver.getUsername());
        assertEquals("ok@example.com", driver.getEmail());
        assertEquals("DRIVER", driver.getRole());
        assertEquals("Fred", driver.getFirstName());
        assertEquals("hello", driver.getLastName());
        assertNull(driver.getProfilePictureUrl());
        assertEquals("MEDIUM", driver.getCarType());
        assertEquals("1999-01-01", driver.getBirthday());
        assertEquals(5, driver.getTotalNumberOfRides());


        List<AccountDTO> resultList = accountService.SearchUser("te");
        AccountDTO result = resultList.getFirst();

        assertEquals("test", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("CUSTOMER", result.getRole());
        assertEquals("Fred", result.getFirstName());
        assertEquals("hello", result.getLastName());
        assertNull(result.getProfilePictureUrl());
        assertNull(result.getCarType()); // Für Customer ist CarType null
        assertEquals("1999-01-01", result.getBirthday());
        assertEquals(5, result.getTotalNumberOfRides());
        assertEquals(new ArrayList<>(), result.getRatings()); // ist leer

    }

    @Test
    void searchUser() {

        List<AccountDTO> driverList = accountService.SearchUser("h");
        AccountDTO driver = driverList.getFirst();
        assertEquals("hii", driver.getUsername());
        assertEquals("ok@example.com", driver.getEmail());
        assertEquals("DRIVER", driver.getRole());
        assertEquals("Fred", driver.getFirstName());
        assertEquals("hello", driver.getLastName());
        assertNull(driver.getProfilePictureUrl());
        assertEquals("MEDIUM", driver.getCarType());
        assertEquals("1999-01-01", driver.getBirthday());
        assertEquals(5, driver.getTotalNumberOfRides());


        List<AccountDTO> resultList = accountService.SearchUser("t");
        AccountDTO result = resultList.getFirst();
        assertEquals("test", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("CUSTOMER", result.getRole());
        assertEquals("Fred", result.getFirstName());
        assertEquals("hello", result.getLastName());
        assertNull(result.getProfilePictureUrl());
        assertNull(result.getCarType()); // Für Customer ist CarType null
        assertEquals("1999-01-01", result.getBirthday());
        assertEquals(5, result.getTotalNumberOfRides());
        assertEquals(new ArrayList<>(), result.getRatings()); // ist leer
    }

    @Test
    void getAccountprofile() {

        AccountDTO driver = accountService.getAccountprofile("hii");
        assertEquals("hii", driver.getUsername());
        assertEquals("ok@example.com", driver.getEmail());
        assertEquals("DRIVER", driver.getRole());
        assertEquals("Fred", driver.getFirstName());
        assertEquals("hello", driver.getLastName());
        assertNull(driver.getProfilePictureUrl());
        assertEquals("MEDIUM", driver.getCarType());
        assertEquals("1999-01-01", driver.getBirthday());
        assertEquals(5, driver.getTotalNumberOfRides());


        AccountDTO CustomerResult = accountService.getAccountprofile("test");
        assertEquals("test", CustomerResult.getUsername());
        assertEquals("test@example.com", CustomerResult.getEmail());
        assertEquals("CUSTOMER", CustomerResult.getRole());
        assertEquals("Fred", CustomerResult.getFirstName());
        assertEquals("hello", CustomerResult.getLastName());
        assertNull(CustomerResult.getProfilePictureUrl());
        assertNull(CustomerResult.getCarType()); // Für Customer ist CarType null
        assertEquals("1999-01-01", CustomerResult.getBirthday());
        assertEquals(5, CustomerResult.getTotalNumberOfRides());
        assertEquals(new ArrayList<>(), CustomerResult.getRatings()); // ist leer

    }

    // Ausgabe bei leerem String testen
    @Test
    void UserSearchTest() {
        List<AccountDTO> result = accountService.SearchUser("");
        assertEquals(2, result.size());
        assertEquals("test", result.get(0).getUsername());
        assertEquals("hii", result.get(1).getUsername());
        assertEquals("CUSTOMER", result.get(0).getRole());
        assertEquals("DRIVER", result.get(1).getRole());
    }

    @Test
    void saveAccountChanges() {
    }
}