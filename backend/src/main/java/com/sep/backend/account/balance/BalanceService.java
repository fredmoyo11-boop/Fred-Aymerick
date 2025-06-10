package com.sep.backend.account.balance;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class BalanceService {

    /**
     * Transfers the specified amount from customer to driver.
     *
     * @param amount         The amount to transfer.
     * @param customerEmail  The email of the customer.
     * @param driverUsername The username of the driver.
     */
    @Transactional
    public void transfer(Double amount, String customerEmail, String driverUsername) {
        // TODO: Implement
    }

}
