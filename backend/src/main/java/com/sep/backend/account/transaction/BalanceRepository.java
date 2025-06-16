package com.sep.backend.account.transaction;

import com.sep.backend.entity.BalanceEntity;
import com.sep.backend.entity.DriverEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BalanceRepository extends JpaRepository<BalanceEntity,Long> {

    List<BalanceRepository> findByUsernameContainingIgnoreCase(@NotNull String part);
}

