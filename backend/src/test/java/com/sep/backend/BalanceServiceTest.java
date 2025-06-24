package com.sep.backend;


import com.sep.backend.account.AccountService;
import com.sep.backend.account.balance.BalanceService;
import com.sep.backend.account.balance.TransactionRepository;
import com.sep.backend.entity.CustomerEntity;
import com.sep.backend.entity.DriverEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BalanceServiceTest {

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionRepository balanceRepository;

    @Mock
    private Principal principal;

    @InjectMocks
    private BalanceService balanceService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // Tests für withdraw

    @Test
    void customerCanWithdraw() {
        String email = "kunde@mail.de";
        double betrag = 20.0;

        CustomerEntity kunde = new CustomerEntity();
        kunde.setEmail(email);
        kunde.setBalance(100.0);

        when(principal.getName()).thenReturn(email);
        when(accountService.getRoleByEmail(email)).thenReturn(Roles.CUSTOMER);
        when(accountService.getCustomerByEmail(email)).thenReturn(kunde);

        balanceService.withdraw(betrag, principal);

        assertEquals(80.0, kunde.getBalance());
        verify(accountService).saveCustomer(kunde);
        verify(balanceRepository).save(any());
    }

    @Test
    void driverCanWithdraw() {
        String email = "fahrer@mail.de";
        double betrag = 40.0;

        DriverEntity fahrer = new DriverEntity();
        fahrer.setEmail(email);
        fahrer.setBalance(120.0);

        when(principal.getName()).thenReturn(email);
        when(accountService.getRoleByEmail(email)).thenReturn(Roles.DRIVER);
        when(accountService.getDriverByEmail(email)).thenReturn(fahrer);

        balanceService.withdraw(betrag, principal);

        assertEquals(80.0, fahrer.getBalance());
        verify(accountService).saveDriver(fahrer);
        verify(balanceRepository).save(any());
    }
}
