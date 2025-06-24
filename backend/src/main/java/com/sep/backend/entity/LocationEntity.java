package com.sep.backend.entity;

import com.sep.backend.nominatim.data.NominatimFeature;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "location")
public class LocationEntity extends AbstractEntity {

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private RouteEntity route;

    @ManyToOne
    @JoinColumn(name = "trip_request_id")
    private TripRequestEntity tripRequest;

    @Column(name = "geo_json", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private NominatimFeature geoJSON;
}
