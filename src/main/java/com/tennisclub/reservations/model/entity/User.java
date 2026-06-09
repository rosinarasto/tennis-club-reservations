package com.tennisclub.reservations.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "\"users\"")
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    public static final String FIELD_PHONE_NUMBER = "phoneNumber";

    @Column(nullable = false)
    private String name;

    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;

    private String password;

    @OneToMany(mappedBy = "user")
    @OrderBy(Reservation.FIELD_FROM)
    private List<Reservation> reservations = new ArrayList<>();
}
