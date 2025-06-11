package com.sep.backend.account.transaction;

import com.sep.backend.Roles;
import com.sep.backend.Transactions;
import com.sep.backend.account.AccountService;
import com.sep.backend.entity.CustomerEntity;
import com.sep.backend.entity.DriverEntity;
import com.sep.backend.entity.TransactionEntity;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.security.Principal;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);
    private final AccountService accountService;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountService accountService, TransactionRepository transactionRepository) {
        this.accountService = accountService;
        this.transactionRepository = transactionRepository;
    }


    public void deposit(double amount, Principal principal) {
        String email = principal.getName();
        String role = accountService.getRoleByEmail(email);
        TransactionEntity transaction = new TransactionEntity();

        if (Roles.CUSTOMER.equals(role)) {
            CustomerEntity customer = this.accountService.getCustomerByEmail(email);
            customer.setBalance(customer.getBalance() + amount);
            log.debug("Customer {} has been deposited to {}, the result is {}", customer.getEmail(), amount, customer.getBalance());
            accountService.saveCustomer(customer);

            transaction.setCustomer(customer);
            transaction.setDriver(null);
        }
        if (Roles.DRIVER.equals(role)) {
            DriverEntity driver = this.accountService.getDriverByEmail(email);
            driver.setBalance(driver.getBalance() + amount);
            accountService.saveDriver(driver);

            transaction.setDriver(driver);
            transaction.setCustomer(null);
        }

        transaction.setAmount(amount);
        transaction.setTransactionType(Transactions.DEPOSIT);
        transactionRepository.save(transaction);
    }


    public void withdraw(double amount, Principal principal) {
        String email = principal.getName();
        String role = accountService.getRoleByEmail(email);
        TransactionEntity transaction = new TransactionEntity();
        if (Roles.CUSTOMER.equals(role)) {
            CustomerEntity customer = this.accountService.getCustomerByEmail(email);
            customer.setBalance(customer.getBalance() - amount);

            accountService.saveCustomer(customer);
            transaction.setCustomer(customer);
            transaction.setDriver(null);

        }
        if (Roles.DRIVER.equals(role)) {
            DriverEntity driver = this.accountService.getDriverByEmail(email);
            driver.setBalance(driver.getBalance() - amount);

            accountService.saveDriver(driver);
            transaction.setDriver(driver);
            transaction.setCustomer(null);

        }

        transaction.setAmount(amount);
        transaction.setTransactionType(Transactions.WITHDRAWAL);
        transactionRepository.save(transaction);

    }

    public void transaction(double amount, Principal principal, DriverEntity driver) {

        String customerEmail = principal.getName();
        String driverEmail = driver.getEmail();
        String role = accountService.getRoleByEmail(customerEmail);

        TransactionEntity transaction = new TransactionEntity();

        CustomerEntity customer = this.accountService.getCustomerByEmail(customerEmail);
        DriverEntity driver1 = this.accountService.getDriverByEmail(driverEmail);
        if (Roles.CUSTOMER.equals(role)) {// toDO


            customer.setBalance(customer.getBalance() - amount);
            driver1.setBalance(driver1.getBalance() + amount);

            accountService.saveCustomer(customer);
            accountService.saveDriver(driver1);


            transaction.setCustomer(customer);
            transaction.setDriver(driver1);


        }
        if (Roles.DRIVER.equals(role)) {




            customer.setBalance(customer.getBalance() - amount);
            driver1.setBalance(driver1.getBalance() + amount);

            accountService.saveCustomer(customer);
            accountService.saveDriver(driver1);

            transaction.setCustomer(customer);
            transaction.setDriver(driver1);


        }

        transaction.setAmount(amount);
        transaction.setTransactionType(Transactions.TRANSACTION);
        transactionRepository.save(transaction);

    }
}
