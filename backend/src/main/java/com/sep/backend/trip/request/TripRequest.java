package com.sep.backend.trip.request;

import com.sep.backend.account.Account;
import com.sep.backend.entity.TripRequestEntity;
import com.sep.backend.route.Route;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripRequest {

    @Schema(description = "The id of the trip request.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "The customer of the trip request.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Account customer;

    @Schema(description = "The route of the trip request.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Route route;

    @Schema(description = "The requested car type for the trip request.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String carType;

    @Schema(description = "The status of the trip request. Either ACTIVE or DELETED.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @Schema(description = "The price for the trip request in euro.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double price;

    public static TripRequest from(TripRequestEntity entity) {
        var account = Account.fromCustomer(entity.getCustomer());
        var route = Route.from(entity.getRoute());


        var tripRequest = new TripRequest();
        tripRequest.setId(entity.getId());
        tripRequest.setCustomer(account);
        tripRequest.setRoute(route);
        tripRequest.setCarType(entity.getCarType());
        tripRequest.setStatus(entity.getStatus());
        tripRequest.setPrice(entity.getPrice());
        return tripRequest;
    }
}
