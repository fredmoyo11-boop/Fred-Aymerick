package com.sep.backend.entity;


import com.sep.backend.ors.data.ORSFeatureCollection;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RouteEntity extends AbstractEntity {

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "stop_order")
    private List<LocationEntity> stops;


    @OneToOne(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    private TripRequestEntity tripRequest;

    @Column(name = "geo_json", nullable = false, columnDefinition = "TEXT")
    @JdbcTypeCode(SqlTypes.JSON)
    private ORSFeatureCollection geoJSON;



}
