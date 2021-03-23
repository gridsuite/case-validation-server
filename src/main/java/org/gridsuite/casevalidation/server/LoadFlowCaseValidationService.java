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
import com.powsybl.network.store.client.NetworkStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

/**
 * @author Etienne Homer <etienne.homer at rte-france.com>
 */
@ComponentScan(basePackageClasses = {NetworkStoreService.class})
@Service
class LoadFlowCaseValidationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadFlowCaseValidationService.class);

    LoadFlowCaseValidationReport validate(Network network, LoadFlowParameters params) {
        // launch the load flow on the network
        LoadFlowResult result = LoadFlow.run(network, params);
        LOGGER.info("Loadflow validation for case {} with loadflow parameters : {}", network.getId(), params);
        boolean isLoadFlowOk = isMainComponentConverging(result);
        LOGGER.info("Loadflow status: {}", isLoadFlowOk ? "OK" : "KO");
        LOGGER.info("Loadflow metrics: {}", result.getMetrics());
        return new LoadFlowCaseValidationReport(result.getMetrics(), isLoadFlowOk, params);
    }

    boolean isMainComponentConverging(LoadFlowResult result) {
        //Open LoadFlow and HADES 2 return the main synchronous component result as component 0;
        //return result.getMetrics().get(COMPONENT_0_STATUS).equals(CONVERGED);
        return result.getComponentResults().get(0).getStatus() == LoadFlowResult.ComponentResult.Status.CONVERGED;
    }

}
