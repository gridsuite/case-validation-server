/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.casevalidation.server;

import com.powsybl.loadflow.LoadFlowParameters;

import java.util.Map;

/**
 * @author Etienne Homer <etienne.homer at rte-france.com>
 */
public class LoadFlowCaseValidationReport {

    private Map<String, String> metrics;

    private boolean loadFlowOk;

    private LoadFlowParameters loadFlowParameters;

    public LoadFlowCaseValidationReport(Map<String, String> metrics, boolean loadFlowOk, LoadFlowParameters loadFlowParameters) {
        this.metrics = metrics;
        this.loadFlowOk = loadFlowOk;
        this.loadFlowParameters = loadFlowParameters;
    }

    public Map<String, String> getMetrics() {
        return metrics;
    }

    public boolean isLoadFlowOk() {
        return loadFlowOk;
    }

    public LoadFlowParameters getLoadFlowParameters() {
        return loadFlowParameters;
    }
}
