package com.tennisclub.reservations.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"courts\"")
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Court extends BaseEntity {

    private String name;

    @Column(unique = true, nullable = false)
    private int number;

    @OneToMany(mappedBy = "court")
    @OrderBy("creationDate")
    private List<Reservation> reservations = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "surface_id")
    private Surface surface;
}
