package com.tennisclub.reservations.repository;

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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void findByPhoneNumber_returnsEmpty() {
        var actual = userRepository.findByPhoneNumber("123456789");
        assertThat(actual).isEmpty();
    }

    @Test
    public void findByPhoneNumber_returnsUser() {
        var user = UserFactory.createUser("jj", "123456789");
        em.persist(user);

        var actual = userRepository.findByPhoneNumber("123456789");

        assertThat(actual).contains(user);
    }

}
