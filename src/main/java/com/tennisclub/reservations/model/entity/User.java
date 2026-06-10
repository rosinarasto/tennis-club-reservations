package com.tennisclub.reservations.model.entity;

import com.tennisclub.reservations.model.Role;
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
    public static final String FIELD_ROLE = "role";

    @Column(nullable = false)
    private String name;

    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @OneToMany(mappedBy = "user")
    @OrderBy(Reservation.FIELD_FROM)
    private List<Reservation> reservations = new ArrayList<>();
}
