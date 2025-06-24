package com.sep.backend.account.balance;

import com.sep.backend.Roles;
import com.sep.backend.account.AccountService;
import com.sep.backend.account.CustomerRepository;
import com.sep.backend.account.DriverRepository;
import com.sep.backend.entity.CustomerEntity;
import com.sep.backend.entity.DriverEntity;
import com.sep.backend.entity.TransactionEntity;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Slf4j
@Service
public class BalanceService {


    private final CustomerRepository customerRepository;
    private final DriverRepository driverRepository;
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;


    public BalanceService(CustomerRepository customerRepository, DriverRepository driverRepository, TransactionRepository transactionRepository, AccountService accountService) {
        this.customerRepository = customerRepository;
        this.driverRepository = driverRepository;
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
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
        transaction.setTransactionType(TransactionTypes.DEPOSIT);
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
        transaction.setTransactionType(TransactionTypes.WITHDRAW);
        transactionRepository.save(transaction);

    }

    public List<Transaction> getCurrentTransactions(Principal principal) {
        String email = principal.getName();
        String role = accountService.getRoleByEmail(email);
        var transactions = (switch (role) {
            case Roles.CUSTOMER -> transactionRepository.findByCustomer_EmailIgnoreCase(email);
            case Roles.DRIVER -> transactionRepository.findByDriver_EmailIgnoreCase(email);
            default -> throw new RuntimeException("Invalid role");
        });
        return transactions.stream().map(Transaction::from).toList();
    }


    /**
     * Transfers the specified amount from customer to driver.
     *
     * @param amount         The amount to transfer.
     * @param customerEmail  The email of the customer.
     * @param driverUsername The username of the driver.
     */
    @Transactional
    public void transfer(Double amount, String customerEmail, String driverUsername) {
        String role = accountService.getRoleByEmail(customerEmail);
        if (!Roles.CUSTOMER.equals(role)) {
            throw new RuntimeException("Only customers can transfer money");
        }

        // customer can only transfer to a driver
        if (!Roles.DRIVER.equals(accountService.getRoleByUsername(driverUsername))) {
            throw new RuntimeException("Invalid driver username");
        }

        var customerEntity = accountService.getCustomerByEmail(customerEmail);
        customerEntity.setBalance(customerEntity.getBalance() - amount);
        customerEntity = customerRepository.save(customerEntity);

        var driverEntity = accountService.getDriverByUsername(driverUsername);
        driverEntity.setBalance(driverEntity.getBalance() + amount);
        driverEntity = driverRepository.save(driverEntity);

        var transactionEntity = new TransactionEntity();
        transactionEntity.setCustomer(customerEntity);
        transactionEntity.setDriver(driverEntity);
        transactionEntity.setTransactionType(TransactionTypes.TRANSFER);
        transactionEntity.setAmount(amount);

        transactionRepository.save(transactionEntity);
    }

}
