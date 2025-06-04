package com.sep.backend.trip.offer;

import com.sep.backend.account.Account;
import com.sep.backend.account.DriverStatistics;
import com.sep.backend.entity.TripOfferEntity;
import com.sep.backend.trip.request.TripRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripOffer {
    @Schema(description = "The if of the trip offer.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "The trip request belonging to the trip offer.", requiredMode = Schema.RequiredMode.REQUIRED)
    private TripRequest tripRequest;

    @Schema(description = "The driver of the trip offer.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Account driver;

    @Schema(description = "The statistical information of the driver.", requiredMode = Schema.RequiredMode.REQUIRED)
    private DriverStatistics driverStatistics;

    @Schema(description = "The status of the trip offer. Either PENDING, ACCEPTED, REJECTED or COMPLETED.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;


    public static TripOffer from(TripOfferEntity entity) {
        var tripOffer = new TripOffer();
        tripOffer.setId(entity.getId());
        tripOffer.setTripRequest(TripRequest.from(entity.getTripRequest()));
        tripOffer.setDriver(Account.fromDriver(entity.getDriver()));
        tripOffer.setStatus(entity.getStatus());
        return tripOffer;
    }
}
