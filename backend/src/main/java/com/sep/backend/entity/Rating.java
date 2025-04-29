package com.sep.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
@Entity
@Table(name = "Rating")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Rating {
    private int rating;
    private String comment;

}
