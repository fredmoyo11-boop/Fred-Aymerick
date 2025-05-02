package com.sep.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
@Table(name = "route")
@Entity
public class RouteEntity extends AbstractEntity {

}
