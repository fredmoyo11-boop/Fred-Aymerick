package com.sep.backend.account.balance;

import com.sep.backend.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findByCustomer_EmailIgnoreCase(String email);

    List<TransactionEntity> findByDriver_EmailIgnoreCase(String email);


    List<TransactionEntity> findByDriver_EmailIgnoreCaseAndTransactionType(String email, String transactionType);
}
