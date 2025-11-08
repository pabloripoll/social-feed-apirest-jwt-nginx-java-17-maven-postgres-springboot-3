package com.restapi.domain.geo;

import com.restapi.domain.geo.entity.Continent;
import com.restapi.domain.geo.repository.ContinentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ContinentRepositoryTest {

    @Autowired
    private ContinentRepository repo;

    @Test
    void existsByName_and_findByName_work() {
        Continent c = new Continent();
        c.setName("Testland");
        repo.saveAndFlush(c);

        assertThat(repo.existsByName("Testland")).isTrue();
        assertThat(repo.findByName("Testland"))
            .isPresent()
            .hasValueSatisfying(continent -> assertThat(continent.getName()).isEqualTo("Testland"));
    }
}
