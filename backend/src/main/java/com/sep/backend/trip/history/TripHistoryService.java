package com.sep.backend.trip.history;

import com.sep.backend.NotFoundException;
import com.sep.backend.entity.TripHistoryEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TripHistoryService {

    private final TripHistoryRepository tripHistoryRepository;

    public TripHistoryService(TripHistoryRepository tripHistoryRepository) {
        this.tripHistoryRepository = tripHistoryRepository;
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
        var tripHistoryEntity = new TripHistoryEntity();
        // TODO: Implement
        return save(tripHistoryEntity);
    }
}
