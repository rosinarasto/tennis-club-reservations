package com.tennisclub.reservations.repository;

import com.tennisclub.reservations.model.factory.CourtFactory;
import com.tennisclub.reservations.model.factory.ReservationFactory;
import com.tennisclub.reservations.model.factory.UserFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;
import org.springframework.test.annotation.DirtiesContext;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void findByCourtNumberOrderByCreationDate_returnsReservationsOrderedByCreationDate() {
        var court = CourtFactory.createCourt(4);
        var user = UserFactory.createUser("user", "123456789");

        em.persist(court.getSurface());
        em.persist(court);
        em.persist(user);

        var laterCreatedReservation = ReservationFactory.createReservation(getTime(14, 0), getTime(15, 50));
        laterCreatedReservation.setCourt(court);
        laterCreatedReservation.setUser(user);

        var earlierCreatedReservation = ReservationFactory.createReservation(getTime(12, 0), getTime(13, 30));
        earlierCreatedReservation.setCourt(court);
        earlierCreatedReservation.setUser(user);

        var deletedReservation = ReservationFactory.createReservation(getTime(16, 0), getTime(17, 0));
        deletedReservation.setCourt(court);
        deletedReservation.setUser(user);
        deletedReservation.softDelete();

        em.persist(laterCreatedReservation);
        em.persist(earlierCreatedReservation);
        em.persist(deletedReservation);
        em.flush();

        updateCreationDate(laterCreatedReservation.getId(), LocalDateTime.of(2024, 1, 2, 0, 0));
        updateCreationDate(earlierCreatedReservation.getId(), LocalDateTime.of(2024, 1, 1, 0, 0));
        updateCreationDate(deletedReservation.getId(), LocalDateTime.of(2023, 1, 1, 0, 0));
        em.clear();

        var actual = reservationRepository.findByCourtNumberOrderByCreationDate(4);

        assertThat(actual)
                .extracting("from")
                .containsExactly(getTime(12, 0), getTime(14, 0));
    }

    @Test
    public void isDateAvailable_returnsFalseWhenRequestedIntervalOverlapsReservationStart() {
        var courtId = persistReservation(getTime(12, 0), getTime(13, 30), false);

        var actual = reservationRepository.isDateAvailable(courtId, getTime(11, 30), getTime(12, 15));

        assertThat(actual).isFalse();
    }

    @Test
    public void isDateAvailable_returnsFalseWhenRequestedIntervalOverlapsReservationEnd() {
        var courtId = persistReservation(getTime(12, 0), getTime(13, 30), false);

        var actual = reservationRepository.isDateAvailable(courtId, getTime(12, 30), getTime(13, 45));

        assertThat(actual).isFalse();
    }

    @Test
    public void isDateAvailable_returnsFalseWhenRequestedIntervalEqualsExistingReservation() {
        var courtId = persistReservation(getTime(12, 0), getTime(13, 30), false);

        var actual = reservationRepository.isDateAvailable(courtId, getTime(12, 0), getTime(13, 30));

        assertThat(actual).isFalse();
    }

    @Test
    public void isDateAvailable_returnsFalseWhenRequestedIntervalContainsExistingReservation() {
        var courtId = persistReservation(getTime(12, 0), getTime(13, 30), false);

        var actual = reservationRepository.isDateAvailable(courtId, getTime(11, 30), getTime(14, 0));

        assertThat(actual).isFalse();
    }

    @Test
    public void isDateAvailable_returnsTrueWhenRequestedIntervalEndsAtExistingReservationStart() {
        var courtId = persistReservation(getTime(12, 0), getTime(13, 30), false);

        var actual = reservationRepository.isDateAvailable(courtId, getTime(11, 0), getTime(12, 0));

        assertThat(actual).isTrue();
    }

    @Test
    public void isDateAvailable_returnsTrueWhenRequestedIntervalStartsAtExistingReservationEnd() {
        var courtId = persistReservation(getTime(12, 0), getTime(13, 30), false);

        var actual = reservationRepository.isDateAvailable(courtId, getTime(13, 30), getTime(14, 0));

        assertThat(actual).isTrue();
    }

    @Test
    public void isDateAvailable_ignoresDeletedReservations() {
        var courtId = persistReservation(getTime(12, 0), getTime(13, 30), true);

        var actual = reservationRepository.isDateAvailable(courtId, getTime(12, 30), getTime(13, 0));

        assertThat(actual).isTrue();
    }

    @Test
    public void isDateAvailable_returnsFalseWhenCourtDoesNotExist() {
        var actual = reservationRepository.isDateAvailable(4L, getTime(12, 0), getTime(13, 30));

        assertThat(actual).isFalse();
    }

    @Test
    public void isDateAvailable_returnsFalseWhenCourtIsDeleted() {
        var court = CourtFactory.createCourt(4);
        court.softDelete();
        em.persist(court.getSurface());
        em.persist(court);
        em.flush();
        var courtId = court.getId();
        em.clear();

        var actual = reservationRepository.isDateAvailable(courtId, getTime(12, 0), getTime(13, 30));

        assertThat(actual).isFalse();
    }

    @Test
    public void findByUserPhoneNumberOrderByFrom_returnsReservationsOrderedByFrom() {
        var court = CourtFactory.createCourt(4);
        var user = UserFactory.createUser("user", "123456789");

        em.persist(court.getSurface());
        em.persist(court);
        em.persist(user);

        var laterReservation = ReservationFactory.createReservation(getTime(14, 0), getTime(15, 50));
        laterReservation.setCourt(court);
        laterReservation.setUser(user);

        var earlierReservation = ReservationFactory.createReservation(getTime(12, 0), getTime(13, 30));
        earlierReservation.setCourt(court);
        earlierReservation.setUser(user);

        var deletedReservation = ReservationFactory.createReservation(getTime(11, 0), getTime(11, 30));
        deletedReservation.setCourt(court);
        deletedReservation.setUser(user);
        deletedReservation.softDelete();

        em.persist(laterReservation);
        em.persist(earlierReservation);
        em.persist(deletedReservation);
        em.flush();
        em.clear();

        var actual = reservationRepository.findByUserPhoneNumberOrderByFrom(
                "123456789",
                false,
                getTime(13, 0)
        );

        assertThat(actual)
                .extracting("from")
                .containsExactly(getTime(12, 0), getTime(14, 0));
    }

    @Test
    public void findByUserPhoneNumberOrderByFrom_returnsFutureReservationsOnly() {
        var court = CourtFactory.createCourt(4);
        var user = UserFactory.createUser("user", "123456789");

        em.persist(court.getSurface());
        em.persist(court);
        em.persist(user);

        var pastReservation = ReservationFactory.createReservation(getTime(12, 0), getTime(13, 30));
        pastReservation.setCourt(court);
        pastReservation.setUser(user);

        var futureReservation = ReservationFactory.createReservation(getTime(14, 0), getTime(15, 50));
        futureReservation.setCourt(court);
        futureReservation.setUser(user);

        em.persist(pastReservation);
        em.persist(futureReservation);
        em.flush();
        em.clear();

        var actual = reservationRepository.findByUserPhoneNumberOrderByFrom(
                "123456789",
                true,
                getTime(13, 45)
        );

        assertThat(actual)
                .extracting("from")
                .containsExactly(getTime(14, 0));
    }

    @Test
    public void findByUserPhoneNumberOrderByFrom_returnsEmptyWhenUserDoesNotExist() {
        var actual = reservationRepository.findByUserPhoneNumberOrderByFrom(
                "123456789",
                false,
                getTime(13, 0)
        );

        assertThat(actual).isEmpty();
    }

    @Test
    public void findByUserPhoneNumberOrderByFrom_returnsEmptyWhenUserIsDeleted() {
        var court = CourtFactory.createCourt(4);
        var user = UserFactory.createUser("user", "123456789");
        user.softDelete();

        var reservation = ReservationFactory.createReservation(getTime(12, 0), getTime(13, 30));
        reservation.setCourt(court);
        reservation.setUser(user);

        em.persist(court.getSurface());
        em.persist(court);
        em.persist(user);
        em.persist(reservation);
        em.flush();
        em.clear();

        var actual = reservationRepository.findByUserPhoneNumberOrderByFrom(
                "123456789",
                false,
                getTime(13, 0)
        );

        assertThat(actual).isEmpty();
    }

    private Long persistReservation(LocalDateTime from, LocalDateTime to, boolean deleted) {
        var court = CourtFactory.createCourt(4);
        var user = UserFactory.createUser("user", "123456789");
        var reservation = ReservationFactory.createReservation(from, to);

        reservation.setCourt(court);
        reservation.setUser(user);

        if (deleted) {
            reservation.softDelete();
        }

        em.persist(court.getSurface());
        em.persist(court);
        em.persist(user);
        em.persist(reservation);
        em.flush();
        var courtId = court.getId();
        em.clear();
        return courtId;
    }

    private void updateCreationDate(Long id, LocalDateTime creationDate) {
        em.createNativeQuery("update \"reservations\" set creation_date = ? where id = ?")
                .setParameter(1, Timestamp.valueOf(creationDate))
                .setParameter(2, id)
                .executeUpdate();
    }

    private LocalDateTime getTime(int hour, int minute) {
        return LocalDateTime.of(2024, 12, 31, hour, minute);
    }
}
