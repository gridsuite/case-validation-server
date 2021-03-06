/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.casevalidation.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author Etienne Homer <etienne.homer at rte-france.com>
 */
@Service
class CaseValidationService {

    @Autowired
    private LoadFlowCaseValidationService loadFlowCaseValidationService;

    public CaseValidationService(LoadFlowCaseValidationService loadFlowCaseValidationService) {
        this.loadFlowCaseValidationService = loadFlowCaseValidationService;
    }

    CaseValidationReport validate(UUID networkUuid, UUID reportUuid) {
        LoadFlowCaseValidationReport loadFlowReport = loadFlowCaseValidationService.validate(networkUuid, reportUuid);
        return new CaseValidationReport(loadFlowReport, loadFlowReport.getStatus() != LoadFlowCaseValidationReport.Status.FAILED);
    }
}
