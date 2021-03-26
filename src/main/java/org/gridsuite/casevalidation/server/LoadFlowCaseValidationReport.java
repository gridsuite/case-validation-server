/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.casevalidation.server;

import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowResult;

import java.util.List;

/**
 * @author Etienne Homer <etienne.homer at rte-france.com>
 */
public class LoadFlowCaseValidationReport {

    private boolean loadFlowOk;

    private List<LoadFlowResult.ComponentResult> componentResults;

    private LoadFlowParameters loadFlowParameters;

    public LoadFlowCaseValidationReport(boolean loadFlowOk, List<LoadFlowResult.ComponentResult> componentResults, LoadFlowParameters loadFlowParameters) {
        this.loadFlowOk = loadFlowOk;
        this.componentResults = componentResults;
        this.loadFlowParameters = loadFlowParameters;
    }

    public boolean isLoadFlowOk() {
        return loadFlowOk;
    }

    public List<LoadFlowResult.ComponentResult> getComponentResults() {
        return componentResults;
    }

    public LoadFlowParameters getLoadFlowParameters() {
        return loadFlowParameters;
    }
}
