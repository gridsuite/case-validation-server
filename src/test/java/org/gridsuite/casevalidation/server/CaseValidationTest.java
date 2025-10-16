/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.casevalidation.server;

import com.powsybl.commons.PowsyblException;
import com.powsybl.loadflow.LoadFlowResult;
import com.powsybl.loadflow.LoadFlowResultImpl;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Etienne Homer <etienne.homer at rte-france.com>
 */
@WebMvcTest(CaseValidationController.class)
@ContextConfiguration(classes = {CaseValidationApplication.class})
class CaseValidationTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private LoadFlowService loadFlowService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testUnfoundNetwork() {
        assertThrows(ServletException.class, () -> {
            UUID notFoundNetworkId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

            // network not existing
            given(loadFlowService.run(eq(notFoundNetworkId), any(), any(), any())).willThrow(new PowsyblException());
            mvc.perform(put("/v1/networks/{networkUuid}/validate", notFoundNetworkId))
                    .andExpect(result -> assertInstanceOf(PowsyblException.class, result.getResolvedException()));
        });
    }

    @Test
    void test() throws Exception {
        UUID testNetworkId = UUID.fromString("7928181c-7977-4592-ba19-88027e4254e4");
        UUID reportId = UUID.fromString("12345679-9876-6543-1478-123698745698");

        //Loadlow converges with default parameters
        List<LoadFlowResult.ComponentResult> componentResults = Collections.singletonList(new LoadFlowResultImpl.ComponentResultImpl(0, 0, LoadFlowResult.ComponentResult.Status.CONVERGED, 5, "slackBusId", 0, 0));
        given(loadFlowService.run(eq(testNetworkId), argThat(params -> params.isTransformerVoltageControlOn()), any(), any())).willReturn(new LoadFlowResultImpl(true, Collections.emptyMap(), null, componentResults));

        MvcResult result = mvc.perform(put("/v1/networks/{networkUuid}/validate", testNetworkId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.validationOk", is(true)))
                .andExpect(jsonPath("$.loadFlowReport", hasEntry("status", "CONVERGED_ON_1ST_LF")))
                .andReturn();

        //Loadlow diverges with default parameters and converges with relaxed ones
        //Validation with default loadflow parameters
        componentResults = Collections.singletonList(new LoadFlowResultImpl.ComponentResultImpl(0, 0, LoadFlowResult.ComponentResult.Status.MAX_ITERATION_REACHED, 5, "slackBusId", 0, 0));
        given(loadFlowService.run(eq(testNetworkId), argThat(params -> params.isTransformerVoltageControlOn()), any(), any())).willReturn(new LoadFlowResultImpl(true, Collections.emptyMap(), null, componentResults));

        //Validation with relaxed loadflow parameters
        componentResults = Collections.singletonList(new LoadFlowResultImpl.ComponentResultImpl(0, 0, LoadFlowResult.ComponentResult.Status.CONVERGED, 5, "slackBusId", 0, 0));
        given(loadFlowService.run(eq(testNetworkId), argThat(params -> !params.isTransformerVoltageControlOn()), any(), any())).willReturn(new LoadFlowResultImpl(true, Collections.emptyMap(), null, componentResults));
        result = mvc.perform(put("/v1/networks/{networkUuid}/validate", testNetworkId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.validationOk", is(true)))
                .andExpect(jsonPath("$.loadFlowReport", hasEntry("status", "CONVERGED_ON_2D_LF")))
                .andReturn();

        //Loadflow diverges with both default and relaxed parameters
        componentResults = Collections.singletonList(new LoadFlowResultImpl.ComponentResultImpl(0, 0, LoadFlowResult.ComponentResult.Status.MAX_ITERATION_REACHED, 5, "slackBusId", 0, 0));
        given(loadFlowService.run(eq(testNetworkId), any(), any(), any())).willReturn(new LoadFlowResultImpl(true, Collections.emptyMap(), null, componentResults));
        result = mvc.perform(put("/v1/networks/{networkUuid}/validate", testNetworkId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.validationOk", is(false)))
                .andExpect(jsonPath("$.loadFlowReport", hasEntry("status", "FAILED")))
                .andReturn();

        given(loadFlowService.run(eq(testNetworkId), any(), any(), any())).willReturn(new LoadFlowResultImpl(true, Collections.emptyMap(), null, componentResults));
        result = mvc.perform(put("/v1/networks/{networkUuid}/validate?reportId={reportId}", testNetworkId, reportId))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.validationOk", is(false)))
            .andExpect(jsonPath("$.loadFlowReport", hasEntry("status", "FAILED")))
            .andReturn();
    }
}
