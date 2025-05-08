package com.sep.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rating")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "rating")
    @Min(1)
    @Max(5)
    @NotNull
    private int rating;

    @Column(name = "comment", nullable = false)
    @NotNull
    @Lob
    private String comment;

    public  String  toString(){
        return "rating: " + rating + " comment: " + comment;
    }
}
