package com.sep.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.sep.backend.route.WaypointTypeEnum;

@Getter
@Setter
@MappedSuperclass
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

    @Enumerated(EnumType.ORDINAL)
    @NotBlank
    @Column(name = "type", nullable = false)
    private WaypointTypeEnum type;
//    @NotNull
//    @Column(name = "verified", nullable = false)
//    private Boolean verified;
//
//    @Column(name = "profile_picture_url")
//    private String profilePictureUrl;

//    public void setBirthday(String birthday) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        this.birthday = LocalDate.parse(birthday, formatter);
//    }
}
