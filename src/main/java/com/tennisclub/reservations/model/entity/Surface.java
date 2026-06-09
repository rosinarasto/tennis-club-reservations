package com.tennisclub.reservations.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"surfaces\"")
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Surface extends BaseEntity {

    public static final String FIELD_NAME = "name";

    @Column(name = "minute_price")
    private BigDecimal minutePrice;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "surface")
    private List<Court> courts = new ArrayList<>();
}
