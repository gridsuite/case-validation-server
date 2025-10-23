/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.casevalidation.server;

import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author Etienne Homer <etienne.homer at rte-france.com>
 */
@Service
class LoadFlowCaseValidationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadFlowCaseValidationService.class);

    @Autowired
    private LoadFlowService loadFlowService;

    LoadFlowCaseValidationReport validate(UUID networkUuid, UUID reportUuid) {
        //Validation with default loadflow parameters
        LoadFlowParameters params = new LoadFlowParameters()
                .setTransformerVoltageControlOn(true)
                .setShuntCompensatorVoltageControlOn(true)
                .setDistributedSlack(true)
                .setBalanceType(LoadFlowParameters.BalanceType.PROPORTIONAL_TO_GENERATION_P_MAX)
                .setReadSlackBus(true)
                .setVoltageInitMode(LoadFlowParameters.VoltageInitMode.DC_VALUES);
        if (validate(networkUuid, params, reportUuid, "loadflowDefaultParameters")) {
            return new LoadFlowCaseValidationReport(LoadFlowCaseValidationReport.Status.CONVERGED_ON_1ST_LF);
        }

        //Validation with relaxed loadflow parameters
        params.setTransformerVoltageControlOn(false);
        params.setShuntCompensatorVoltageControlOn(false);

        boolean isLoadFlowOk = validate(networkUuid, params, reportUuid, "relaxedLoadflow");
        return new LoadFlowCaseValidationReport(isLoadFlowOk ? LoadFlowCaseValidationReport.Status.CONVERGED_ON_2D_LF : LoadFlowCaseValidationReport.Status.FAILED);
    }

    boolean validate(UUID networkUuid, LoadFlowParameters params, UUID reportUuid, String reportName) {
        LoadFlowResult result = loadFlowService.run(networkUuid, params, reportUuid, reportName);
        LOGGER.info("Loadflow validation for case {} with loadflow parameters : {}", networkUuid, params);
        boolean isLoadFlowOk = isMainComponentConverging(result);
        LOGGER.info("Loadflow status: {}", isLoadFlowOk ? "Converged" : "Failed");
        return isLoadFlowOk;
    }

    boolean isMainComponentConverging(LoadFlowResult result) {
        //Open LoadFlow and HADES 2 return the main synchronous component result as component 0;
        //TODO: result.getComponentResults() will never return an empty list in next release. This check has to me removed.
        if (result.getComponentResults().isEmpty()) {
            return false;
        }
        return result.getComponentResults().get(0).getStatus() == LoadFlowResult.ComponentResult.Status.CONVERGED;
    }

}
