/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.casevalidation.server;

import com.google.common.collect.Iterables;
import com.powsybl.commons.PowsyblException;
import com.powsybl.iidm.network.Network;
import com.powsybl.network.store.client.NetworkStoreService;
import com.powsybl.network.store.client.PreloadingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * @author Etienne Homer <etienne.homer at rte-france.com>
 */
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
        List<LoadFlowCaseValidationReport> loadFlowReports = loadFlowCaseValidationService.validate(network);
        return new CaseValidationReport(loadFlowReports, Iterables.getLast(loadFlowReports).isLoadFlowOk());
    }
}
