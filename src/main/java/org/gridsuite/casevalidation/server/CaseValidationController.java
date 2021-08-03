/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.casevalidation.server;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Etienne Homer <etienne.homer at rte-france.com>
 */
@RestController
@RequestMapping(value = "/" + CaseValidationApi.API_VERSION + "/")
@Tag(name = "case-validation-server")
@ComponentScan(basePackageClasses = CaseValidationService.class)
public class CaseValidationController {

    @Autowired
    private CaseValidationService caseValidationService;

    @PutMapping(value = "/networks/{networkUuid}/validate", produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "check case validity")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Network has been checked")})
    public ResponseEntity<CaseValidationReport> validate(@Parameter(description = "Network UUID") @PathVariable("networkUuid") UUID networkUuid,
                                                         @Parameter(description = "Report UUID") @RequestParam(value = "reportId", required = false) UUID reportUuid) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(caseValidationService.validate(networkUuid, reportUuid));
    }
}
