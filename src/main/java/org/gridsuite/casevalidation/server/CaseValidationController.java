/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.casevalidation.server;

import io.swagger.annotations.*;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
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

    @Inject
    private CaseValidationService caseValidationService;

    @PutMapping(value = "/networks/{networkUuid}/validate", produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "check case validity", produces = APPLICATION_JSON_VALUE)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Network has been checked")})
    public ResponseEntity<CaseValidationReport> validate(@ApiParam(value = "Network UUID") @PathVariable("networkUuid") UUID networkUuid) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(caseValidationService.validate(networkUuid));
    }
}
