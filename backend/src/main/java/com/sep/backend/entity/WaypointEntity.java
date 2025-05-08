package com.sep.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.*;

import com.sep.backend.route.WaypointType;
//import com.sep.backend.route.WaypointTypeEnum;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "waypoint")
@Entity
public class WaypointEntity extends AbstractEntity {
    @NotBlank
    @Column(name = "index", unique = false, nullable = false)
    private long index;

    @NotBlank
    @Column(name = "longitude", nullable = false)
    private String longitude;

    @NotBlank
    @Column(name = "latitude", nullable = false)
    private String latitude;

//    @Enumerated(EnumType.ORDINAL)
//    @NotBlank
//    @Column(name = "type", nullable = false)
//    private WaypointTypeEnum type;

    @NotBlank
    @Column(name = "type", nullable = false)
    private String type;

//    @NotBlank
//    //@Column(name = "route", nullable = false)
//    @ManyToOne(optional=false)
//    private RouteEntity routeEntity;

    @NotBlank
    @Column(name = "route_id", nullable = false, unique = true)
    private long routeId;
}
