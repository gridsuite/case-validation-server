/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.casevalidation.server;

import com.powsybl.iidm.network.Network;
import com.powsybl.loadflow.LoadFlow;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Etienne Homer <etienne.homer at rte-france.com>
 */
@Service
class LoadFlowCaseValidationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadFlowCaseValidationService.class);

    List<LoadFlowCaseValidationReport> validate(Network network) {
        List<LoadFlowCaseValidationReport> loadFlowReports = new ArrayList<>();

        //Validation with default loadflow parameters
        LoadFlowParameters params = new LoadFlowParameters()
                .setTransformerVoltageControlOn(true)
                .setSimulShunt(true)
                .setDistributedSlack(true)
                .setBalanceType(LoadFlowParameters.BalanceType.PROPORTIONAL_TO_GENERATION_P_MAX)
                .setReadSlackBus(true)
                .setVoltageInitMode(LoadFlowParameters.VoltageInitMode.DC_VALUES);

        LoadFlowCaseValidationReport report = validate(network, params);
        loadFlowReports.add(report);
        if (report.isLoadFlowOk()) {
            return loadFlowReports;
        }

        //Validation with relaxed loadflow parameters
        params.setTransformerVoltageControlOn(false);
        params.setSimulShunt(false);

        report = validate(network, params);
        loadFlowReports.add(report);
        return loadFlowReports;
    }

    LoadFlowCaseValidationReport validate(Network network, LoadFlowParameters params) {
        LoadFlowResult result = LoadFlow.run(network, params);
        LOGGER.info("Loadflow validation for case {} with loadflow parameters : {}", network.getId(), params);
        boolean isLoadFlowOk = isMainComponentConverging(result);
        LOGGER.info("Loadflow status: {}", isLoadFlowOk ? "OK" : "KO");
        LOGGER.info("Loadflow metrics: {}", result.getMetrics());
        return new LoadFlowCaseValidationReport(isLoadFlowOk, result.getComponentResults(), params);
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
