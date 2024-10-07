/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.casevalidation.server;

import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowResult;
import com.powsybl.loadflow.LoadFlowResultImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * @author Etienne Homer <etienne.homer at rte-france.com>
 */
@ExtendWith(MockitoExtension.class)
class LoadFlowServiceTest {

    @Mock
    private RestTemplate loadFlowServerRest;

    private LoadFlowService loadFlowService;

    @BeforeEach
    void setUp() {
        loadFlowService = new LoadFlowService(loadFlowServerRest);
    }

    @Test
    void test() {
        UUID testNetworkId = UUID.fromString("7928181c-7977-4592-ba19-88027e4254e4");
        UUID reportUuid = UUID.fromString("12345679-9876-6543-1478-123698745698");
        List<LoadFlowResult.ComponentResult> componentResults = Collections.singletonList(new LoadFlowResultImpl.ComponentResultImpl(0, 0, LoadFlowResult.ComponentResult.Status.CONVERGED, 5, "slackBusId", 0, 0));
        LoadFlowParameters parameters = new LoadFlowParameters();
        when(loadFlowServerRest.exchange(anyString(),
                eq(HttpMethod.PUT),
                any(),
                eq(LoadFlowResult.class),
                eq(testNetworkId.toString())
                ))
                .thenReturn(ResponseEntity.ok(new LoadFlowResultImpl(true, Collections.emptyMap(), null, componentResults)));
        LoadFlowResult res = loadFlowService.run(testNetworkId, parameters, reportUuid, "reportName");
        assertTrue(res.isOk());
    }
}
