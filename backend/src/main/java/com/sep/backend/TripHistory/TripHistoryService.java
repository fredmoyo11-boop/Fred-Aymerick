package com.sep.backend.TripHistory;
import com.sep.backend.ErrorMessages;
import com.sep.backend.Roles;
import com.sep.backend.account.AccountService;
import com.sep.backend.entity.*;
import com.sep.backend.trip.request.TripRequestException;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TripHistoryService {
    private final TripHistorieRepository tripHistorieRepository;
    private final AccountService accountService;

    public TripHistoryService(TripHistorieRepository tripHistorieRepository, AccountService accountService) {
        this.tripHistorieRepository = tripHistorieRepository;
        this.accountService = accountService;
    }
    @Schema(description = "Methode zum Erstellen und Speichern  einer abgeschlossenen Fahrt ")
    public TripHistoryEntity saveTripHistory(
            TripOfferEntity offer,
            Double distance,
            Integer duration,
            Integer driverRating,
            Integer customerRating
    ) {
        TripRequestEntity request = offer.getTripRequest();
        CustomerEntity customer = request.getCustomer();
        DriverEntity driver = offer.getDriver();

        TripHistoryEntity history = new TripHistoryEntity();
        history.setTripOfferId(offer.getId());
        history.setCustomer(customer);
        history.setDriver(driver);
        history.setDistance(distance);
        history.setDuration(duration);
        history.setEndTime(LocalDateTime.now());
        history.setPrice(request.getPrice());

        history.setCustomerRating(customerRating);
        history.setDriverRating(driverRating);

        return tripHistorieRepository.save(history);
    }
    public TripHistoryEntity saveTripHistory(@Valid TripHistoryDTO tripHistoryDTO) {
        TripHistoryEntity tripHistoryEntity = new TripHistoryEntity();
        tripHistoryEntity.setTripOfferId(tripHistoryEntity.getTripOfferId());
        tripHistoryEntity.setEndTime(tripHistoryDTO.getEndTime());
        tripHistoryEntity.setDuration(tripHistoryDTO.getDuration());
        tripHistoryEntity.setDistance(tripHistoryDTO.getDistance());
        tripHistoryEntity.setCustomer(accountService.findCustomerByUsername(tripHistoryDTO.getCustomerUsername()));
        tripHistoryEntity.setDriver(accountService.findDriverByUsername(tripHistoryDTO.getDriverUsername()));
        tripHistoryEntity.setCustomerRating(tripHistoryDTO.getCustomerRating());
        tripHistoryEntity.setDriverRating(tripHistoryDTO.getDriverRating());
        tripHistoryEntity.setPrice(tripHistoryDTO.getPrice());
        return tripHistorieRepository.save(tripHistoryEntity);
    }

    public List<TripHistoryDTO> getCurrentTripHistory(Principal principal) {
        String email = principal.getName();
        if (accountService.existsEmail(email)) {
            if (Roles.CUSTOMER.equals(accountService.getRoleByEmail(email))) {
                var customerEntity = accountService.getCustomerByEmail(email);
                return MapToTripHistoryDTO(tripHistorieRepository.findByCustomer(customerEntity));
            } else {
                var driverEntity = accountService.getDriverByEmail(email);
                return MapToTripHistoryDTO(tripHistorieRepository.findByDriver(driverEntity));
            }
        } else {
            throw new TripRequestException(ErrorMessages.HISTORY_NOT_FOUND);
        }
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