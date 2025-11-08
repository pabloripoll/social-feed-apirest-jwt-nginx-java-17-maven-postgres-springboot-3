package com.restapi.domain.geo;

//import com.restapi.domain.geo.dto.ContinentDto;
import com.restapi.domain.geo.dto.ContinentSummaryDto;
import com.restapi.domain.geo.entity.Continent;
import com.restapi.domain.geo.controller.ContinentController;
import com.restapi.domain.geo.exception.NameConflictException;
import com.restapi.domain.geo.repository.ContinentRepository;
import com.restapi.domain.geo.request.ContinentRequests.CreateContinentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
class ContinentControllerUnitTest {

    @Mock
    private ContinentRepository repository;

    @InjectMocks
    private ContinentController controller;

    @BeforeEach
    void setUp() {
        // nothing for now
    }

    @Test
    void listAll_returnsSummaries() {
        Continent c1 = new Continent(); c1.setName("Europe");
        Continent c2 = new Continent(); c2.setName("Asia");
        when(repository.findAll()).thenReturn(List.of(c1, c2));

        List<ContinentSummaryDto> list = controller.listAll();

        assertThat(list).hasSize(2)
            .extracting(ContinentSummaryDto::name)
            .containsExactly("Europe", "Asia");
        verify(repository).findAll();
    }

    @Test
    void create_whenNameExists_throwsNameConflict() {
        CreateContinentRequest req = new CreateContinentRequest("Europe");
        when(repository.existsByName("Europe")).thenReturn(true);

        assertThatThrownBy(() -> controller.create(req))
            .isInstanceOf(NameConflictException.class)
            .hasMessageContaining("name already exists");

        verify(repository).existsByName("Europe");
        verify(repository, never()).save(ArgumentMatchers.any());
    }
}
