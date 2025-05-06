package com.sep.backend.entity;

import jakarta.persistence.*;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int rating;
    private String comment;

}
