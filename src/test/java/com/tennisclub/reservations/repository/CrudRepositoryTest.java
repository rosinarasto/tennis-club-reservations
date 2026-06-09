package com.tennisclub.reservations.repository;

import com.tennisclub.reservations.model.entity.Surface;
import com.tennisclub.reservations.model.factory.SurfaceFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CrudRepositoryTest {

    @Autowired
    private CrudRepository<Surface> repository;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void saveEntity() {
        var surface = SurfaceFactory.createSurface("grass");
        var actual = repository.save(surface);

        assertThat(actual.getName()).isEqualTo(surface.getName());
    }

    @Test
    public void updateEntity() {
        var surface = SurfaceFactory.createSurface("grass");
        em.persist(surface);

        surface.setName("clay");
        var actual = repository.update(surface);
        assertThat(actual.getName()).isEqualTo("clay");
    }


    @Test
    public void findById_returnsEmpty() {
        var actual = repository.findById(4L);
        assertThat(actual).isEmpty();
    }

    @Test
    public void findById_returnsEntity() {
        var surface = SurfaceFactory.createSurface("grass");
        surface = repository.save(surface);

        var actual = repository.findById(surface.getId());
        assertThat(actual.get().getName()).isEqualTo("grass");
    }

    @Test
    public void findAll_returnsNoElements() {
       var actual = repository.findAll(PageRequest.of(0, 10 ));
       assertThat(actual.getTotalElements()).isEqualTo(0);
    }

    @Test
    public void findAll_returnsEntities() {
        em.persist(SurfaceFactory.createSurface("grass"));
        em.persist(SurfaceFactory.createSurface("clay"));

        var actual = repository.findAll(PageRequest.of(0, 10 ));

        assertThat(actual.getTotalElements()).isEqualTo(2);
        assertThat(actual.getContent().get(0).getName()).isEqualTo("grass");
        assertThat(actual.getContent().get(1).getName()).isEqualTo("clay");
    }

    @Test
    public void findAll_returnsTotalElementsForAllPages() {
        em.persist(SurfaceFactory.createSurface("grass"));
        em.persist(SurfaceFactory.createSurface("clay"));

        var actual = repository.findAll(PageRequest.of(1, 1));

        assertThat(actual.getTotalElements()).isEqualTo(2);
        assertThat(actual.getContent().size()).isEqualTo(1);
    }

    @Test
    public void softDeleteById() {
        var surface = repository.save(SurfaceFactory.createSurface("grass"));
        repository.softDeleteById(surface.getId());
        var actual = repository.findById(surface.getId());
        assertThat(actual).isEmpty();
    }

    @Test
    public void softDeleteAll() {
        em.persist(SurfaceFactory.createSurface("grass"));
        em.persist(SurfaceFactory.createSurface("clay"));

        repository.softDeleteAll(PageRequest.of(0, 10 ));
        var actual = repository.findAll(PageRequest.of(0, 10 ));

        assertThat(actual.getTotalElements()).isEqualTo(0);
    }
}
