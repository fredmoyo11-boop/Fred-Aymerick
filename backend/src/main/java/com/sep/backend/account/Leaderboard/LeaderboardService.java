package com.sep.backend.account.Leaderboard;

import com.sep.backend.account.DriverRepository;
import com.sep.backend.account.balance.TransactionRepository;
import com.sep.backend.account.balance.TransactionTypes;
import com.sep.backend.entity.TransactionEntity;
import com.sep.backend.trip.history.TripHistoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class LeaderboardService {
    private final TripHistoryService tripHistoryService;
    private final TransactionRepository transactionRepository;
    private final DriverRepository driverRepository;


    public List<Leaderboard> getDriverLeaderboards() {
        return driverRepository.findAll().stream().map(driver -> {

                    String email = driver.getEmail();

                    String driverUsername = driver.getUsername();

                    String driverName = driver.getFirstName() + " " + driver.getLastName();

                    double totalDrivenDistance = tripHistoryService.totalDrivenDistance(email);

                    double averageRating = tripHistoryService.averageRating(email);

                    Integer totalDriveTime = tripHistoryService.totalDrivenDuration(email);

                    int totalNumberOfDrivenTrip = tripHistoryService.totalNumberOfDrivenTrip(email);

                    Double totalEarnings =tripHistoryService.averageCustomerRating(email);

                    return new Leaderboard(
                            driverUsername,
                            driverName,
                            totalDrivenDistance,
                            averageRating,
                            totalDriveTime,
                            totalNumberOfDrivenTrip,
                            totalEarnings
                    );
                }

        ).toList();

    }
}