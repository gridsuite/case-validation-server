/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.casevalidation.server;

import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowResult;
import com.powsybl.loadflow.json.JsonLoadFlowParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

/**
 * @author Etienne Homer<etienne.homer at rte-france.com
 */
@Service
public class LoadFlowService {

    private static final String LOAD_FLOW_API_VERSION = "v1";
    private static final String DELIMITER = "/";

    private RestTemplate loadFlowServerRest;

    @Autowired
    public LoadFlowService(RestTemplateBuilder builder,
                           @Value("${backing-services.loadflow-server.base-uri:http://loadflow-server/}") String loadFlowBaseUri) {
        this.loadFlowServerRest = builder.uriTemplateHandler(
            new DefaultUriBuilderFactory(loadFlowBaseUri)
        ).build();
    }

    public LoadFlowService(RestTemplate restTemplate) {
        this.loadFlowServerRest = restTemplate;
    }

    public LoadFlowResult run(UUID networkUuid, LoadFlowParameters params, UUID reportUuid, String reportName) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(DELIMITER + LOAD_FLOW_API_VERSION + "/networks/{networkUuid}/run");
        if (reportUuid != null && reportName != null) {
            uriBuilder.queryParam("reportId", reportUuid).queryParam("reportName", reportName).queryParam("overwrite", true);
        }
        String uri = uriBuilder.build().toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonLoadFlowParameters.write(params, baos);
        HttpEntity<byte[]> requestEntity = new HttpEntity<>(baos.toByteArray(), headers);

        return loadFlowServerRest.exchange(uri,
            HttpMethod.PUT,
            requestEntity,
            LoadFlowResult.class,
            networkUuid.toString()
        ).getBody();
    }
}
