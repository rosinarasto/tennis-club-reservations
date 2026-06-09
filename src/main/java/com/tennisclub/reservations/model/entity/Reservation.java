package com.tennisclub.reservations.model.entity;

import com.tennisclub.reservations.model.GameType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"reservations\"")
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation extends BaseEntity {

    public static final String FIELD_FROM = "from";
    public static final String FIELD_TO = "to";
    public static final String FIELD_USER = "user";
    public static final String FIELD_COURT = "court";

    @Column(name = "from_date")
    private LocalDateTime from;

    @Column(name = "to_date")
    private LocalDateTime to;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "game_type")
    private GameType gameType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "court_id")
    private Court court;
}
