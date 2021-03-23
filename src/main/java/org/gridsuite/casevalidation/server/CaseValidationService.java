/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.casevalidation.server;

import com.powsybl.commons.PowsyblException;
import com.powsybl.iidm.network.Network;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.network.store.client.NetworkStoreService;
import com.powsybl.network.store.client.PreloadingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Etienne Homer <etienne.homer at rte-france.com>
 */
@ComponentScan(basePackageClasses = {NetworkStoreService.class})
@Service
class CaseValidationService {

    @Autowired
    private NetworkStoreService networkStoreService;

    @Autowired
    private LoadFlowCaseValidationService loadFlowCaseValidationService;

    public CaseValidationService(NetworkStoreService networkStoreService,
                                 LoadFlowCaseValidationService loadFlowCaseValidationService) {
        this.networkStoreService = networkStoreService;
        this.loadFlowCaseValidationService = loadFlowCaseValidationService;
    }

    private Network getNetwork(UUID networkUuid) {
        try {
            return networkStoreService.getNetwork(networkUuid, PreloadingStrategy.COLLECTION);
        } catch (PowsyblException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Network '" + networkUuid + "' not found");
        }
    }

    CaseValidationReport validate(UUID networkUuid) {
        Network network = getNetwork(networkUuid);
        List<LoadFlowCaseValidationReport> loadFlowReports = new ArrayList<>();

        //Validation with default loadflow parameters
        LoadFlowParameters params = new LoadFlowParameters()
                .setTransformerVoltageControlOn(true)
                .setSimulShunt(true)
                .setDistributedSlack(true)
                .setBalanceType(LoadFlowParameters.BalanceType.PROPORTIONAL_TO_GENERATION_P_MAX)
                .setReadSlackBus(true)
                .setVoltageInitMode(LoadFlowParameters.VoltageInitMode.DC_VALUES);

        LoadFlowCaseValidationReport report = loadFlowCaseValidationService.validate(network, params);
        loadFlowReports.add(report);
        if (report.isLoadFlowOk()) {
            return new CaseValidationReport(loadFlowReports, true);
        }

        //Validation with relaxed loadflow parameters
        params.setTransformerVoltageControlOn(false);
        params.setSimulShunt(false);

        report = loadFlowCaseValidationService.validate(network, params);
        loadFlowReports.add(report);
        return new CaseValidationReport(loadFlowReports, report.isLoadFlowOk());
    }
}
