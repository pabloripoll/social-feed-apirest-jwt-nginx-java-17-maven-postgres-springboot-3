package com.restapi.http.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restapi.domain.geo.entity.Continent;
import com.restapi.domain.geo.repository.ContinentRepository;
import com.restapi.domain.geo.request.ContinentRequests.CreateContinentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("null")
@SpringBootTest
@AutoConfigureMockMvc
class ContinentRouteIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ContinentRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        Continent c = new Continent();
        c.setName("Europe");
        repository.saveAndFlush(c);
    }

    @Test
    void list_returnsSummary_without_timestamps() throws Exception {
        mvc.perform(get("/api/v1/geo/continents"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").exists())
            .andExpect(jsonPath("$[0].name").value("Europe"))
            .andExpect(jsonPath("$[0].created_at").doesNotExist());
    }

    @Test
    void create_and_location_header() throws Exception {
        CreateContinentRequest req = new CreateContinentRequest("Oceania");
        mvc.perform(post("/api/v1/geo/continents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", containsString("/api/v1/geo/continents/")))
            .andExpect(jsonPath("$.name").value("Oceania"));
    }
}
