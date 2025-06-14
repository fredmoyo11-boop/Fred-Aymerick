package com.sep.backend.account.balance;

public class TransactionTypes {

    // can only happen by CUSTOMER
    public static final String DEPOSIT = "DEPOSIT";

    public static final String WITHDRAW = "WITHDRAW";

    // can only happen from CUSTOMER to DRIVER
    public static final String TRANSFER = "TRANSFER";

}

