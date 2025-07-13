package com.sep.backend.trip.history;

import com.sep.backend.ErrorMessages;
import com.sep.backend.NotFoundException;
import com.sep.backend.Roles;
import com.sep.backend.account.AccountService;
import com.sep.backend.account.balance.TransactionRepository;
import com.sep.backend.account.balance.TransactionTypes;
import com.sep.backend.entity.*;
import com.sep.backend.trip.offer.TripOfferService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class TripHistoryService {
    private final TripHistoryRepository tripHistoryRepository;
    private final AccountService accountService;
    private final TripOfferService tripOfferService;
    private final TransactionRepository transactionRepository;
    public TripHistoryService(TripHistoryRepository tripHistoryRepository, AccountService accountService, TripOfferService tripOfferService, TransactionRepository transactionRepository) {
        this.tripHistoryRepository = tripHistoryRepository;
        this.accountService = accountService;
        this.tripOfferService = tripOfferService;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Returns an optional containing the trip history entity for the specified trip offer id.
     * Optional is empty if not trip history exists for provided trip offer id.
     *
     * @param tripOfferId The id of the trip offer.
     * @return The optional containing the trip history entity.
     */
    public Optional<TripHistoryEntity> findByTripOfferId(Long tripOfferId) {
        return tripHistoryRepository.findByTripOfferId(tripOfferId);
    }

    /**
     * Saves the provided trip history entity in repository.
     *
     * @param tripHistoryEntity The trip history entity.
     * @return The saved trip history entity.
     */
    public TripHistoryEntity save(TripHistoryEntity tripHistoryEntity) {
        return tripHistoryRepository.save(tripHistoryEntity);
    }

    /**
     * Creates a trip history for a completed trip offer.
     *
     * @param tripOfferId    The id of the trip offer.
     * @param endTime        The end time of the simulation.
     * @param driverRating   The rating by the driver.
     * @param customerRating The rating by the customer.
     * @return The created trip history entity.
     * @throws NotFoundException If no trip offer with specified id exists.
     */
    public TripHistoryEntity createTripHistory(Long tripOfferId, LocalDateTime endTime, Integer driverRating, Integer customerRating) throws NotFoundException {
        var tripOfferEntity = tripOfferService.getTripOffer(tripOfferId);

        var tripHistoryEntity = new TripHistoryEntity();
        tripHistoryEntity.setTripOfferId(tripOfferId);
        tripHistoryEntity.setEndTime(endTime);
        tripHistoryEntity.setDistance(tripOfferEntity.getTripRequest().getRoute().getGeoJSON().getFeatures().getFirst().getProperties().getSummary().getDistance());
        tripHistoryEntity.setDuration((int) tripOfferEntity.getTripRequest().getRoute().getGeoJSON().getFeatures().getFirst().getProperties().getSummary().getDuration());
        tripHistoryEntity.setPrice(tripOfferEntity.getTripRequest().getPrice());
        tripHistoryEntity.setDriverRating(driverRating);
        tripHistoryEntity.setCustomerRating(customerRating);
        tripHistoryEntity.setCustomer(tripOfferEntity.getTripRequest().getCustomer());
        tripHistoryEntity.setDriver(tripOfferEntity.getDriver());
        return tripHistoryRepository.save(tripHistoryEntity);
    }

    public TripHistoryEntity saveTripHistory(TripOfferEntity offer, double distance, int duration, int driverRating, int customerRating) {
        TripRequestEntity request = offer.getTripRequest();
        CustomerEntity customer = request.getCustomer();
        DriverEntity driver = offer.getDriver();

        TripHistoryEntity history = new TripHistoryEntity();
        history.setTripOfferId(offer.getId());
        history.setCustomer(customer);
        history.setDriver(driver);
        history.setDistance(distance);
        history.setDuration(duration);
        history.setEndTime(LocalDateTime.now(ZoneId.of("Europe/Berlin")));
        history.setPrice(request.getPrice());
        history.setCustomerRating(customerRating);
        history.setDriverRating(driverRating);

        return tripHistoryRepository.save(history);
    }

    public List<TripHistoryDTO> getCurrentTripHistory(Principal principal) {
        String email = principal.getName();
        if (accountService.existsEmail(email)) {
            if (Roles.CUSTOMER.equals(accountService.getRoleByEmail(email))) {
                var customerEntity = accountService.getCustomerByEmail(email);
                return MapToTripHistoryDTO(tripHistoryRepository.findByCustomer_Id(customerEntity.getId()));
            } else {
                var driverEntity = accountService.getDriverByEmail(email);
                return MapToTripHistoryDTO(tripHistoryRepository.findByDriver_Id(driverEntity.getId()));
            }
        } else {
            throw new NotFoundException(ErrorMessages.NOT_FOUND_HISTORY);
        }
    }


    public Double totalDrivenDistance(String email) {
        return findByDriver_Email(email).stream()
                .mapToDouble(TripHistoryEntity::getDistance)
                .sum();
    }

    public Integer totalDrivenDuration(String email) {
        return findByDriver_Email(email).stream()
                .mapToInt(TripHistoryEntity::getDuration)
                .sum();
    }

    public Double averageRating(String email) {
        return findByDriver_Email(email).stream()
                .mapToInt(TripHistoryEntity::getCustomerRating)
                .average()
                .orElse(0.0);
    }
    public Double averageCustomerRating(String email) {
        return transactionRepository.findByDriver_EmailIgnoreCaseAndTransactionType(email, TransactionTypes.TRANSFER).stream()
                .mapToDouble(TransactionEntity::getAmount)
                .sum();
    }

    public Integer totalNumberOfDrivenTrip(String email) {
        return findByDriver_Email(email).size();
    }

    public List<TripHistoryEntity> findByDriver_Email(String email) {
        return tripHistoryRepository.findByDriver_Email(email);
    }

    public List<TripHistoryDTO> MapToTripHistoryDTO(List<TripHistoryEntity> tripHistoryEntities) {
        return tripHistoryEntities.stream().map(tripHistory -> {
            TripHistoryDTO dto = new TripHistoryDTO();
            dto.setTripId(tripHistory.getId());
            dto.setEndTime(tripHistory.getEndTime());
            dto.setDistance(tripHistory.getDistance());
            dto.setDuration(tripHistory.getDuration());
            dto.setPrice(tripHistory.getPrice());
            dto.setCustomerRating(tripHistory.getCustomerRating());
            dto.setCustomerName(tripHistory.getCustomer().getFirstName() + " " + tripHistory.getCustomer().getLastName());
            dto.setCustomerUsername(tripHistory.getCustomer().getUsername());
            dto.setDriverRating(tripHistory.getDriverRating());
            dto.setDriverName(tripHistory.getDriver().getFirstName() + " " + tripHistory.getDriver().getLastName());
            dto.setDriverUsername(tripHistory.getDriver().getUsername());
            return dto;
        }).toList();
    }

}