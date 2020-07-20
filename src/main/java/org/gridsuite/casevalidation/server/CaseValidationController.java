package org.gridsuite.casevalidation.server; /**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowResult;
import com.powsybl.loadflow.json.JsonLoadFlowParameters;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Etienne Homer <etienne.homer at rte-france.com>
 */
@RestController
@RequestMapping(value = "/" + CaseValidationApi.API_VERSION + "/")
@Api(tags = "case-validation-server")
@ComponentScan(basePackageClasses = CaseValidationService.class)
public class CaseValidationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaseValidationController.class);

    @Inject
    private CaseValidationService caseValidationService;

    @PutMapping(value = "/networks/{networkUuid}/check", produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "check case validity", produces = APPLICATION_JSON_VALUE)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "IGM has been checked")})
    public ResponseEntity<CaseValidationReport> checkIGMValidity(@ApiParam(value = "Network UUID") @PathVariable("networkUuid") UUID networkUuid,
                                                   @RequestBody(required = false) String loadflowParams) {
        LoadFlowParameters parameters = loadflowParams != null
                ? JsonLoadFlowParameters.read(new ByteArrayInputStream(loadflowParams.getBytes()))
                : null;

        LoadFlowResult result = caseValidationService.checkLoadflowConvergence(networkUuid, parameters);
        CaseValidationReport caseValidationReport = new CaseValidationReport();
        caseValidationReport.setLoadFlowResult(result);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(caseValidationReport);
    }
}
