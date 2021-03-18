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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

/**
 * @author Etienne Homer <etienne.homer at rte-france.com>
 */
@ComponentScan(basePackageClasses = {NetworkStoreService.class})
@Service
class LoadFlowCaseValidationService {
    private static final String COMPONENT_0_STATUS = "network_0_status";
    private static final String CONVERGED = "CONVERGED";

    LoadFlowCaseValidationReport validate(Network network, LoadFlowParameters params) {
        // launch the load flow on the network
        LoadFlowResult result = LoadFlow.run(network, params);
        return new LoadFlowCaseValidationReport(result.getMetrics(), isMainComponentConverging(result), params);
    }

    boolean isMainComponentConverging(LoadFlowResult result) {
        return result.getMetrics().get(COMPONENT_0_STATUS).equals(CONVERGED);
    }
}
