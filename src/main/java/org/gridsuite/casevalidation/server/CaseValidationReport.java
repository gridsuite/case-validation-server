package org.gridsuite.casevalidation.server; /**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.powsybl.loadflow.LoadFlowResult;

/**
 * @author Etienne Homer <etienne.homer at rte-france.com>
 */
public class CaseValidationReport {

    private LoadFlowResult loadFlowResult;

    public LoadFlowResult getLoadFlowResult() {
        return loadFlowResult;
    }

    public void setLoadFlowResult(LoadFlowResult loadFlowResult) {
        this.loadFlowResult = loadFlowResult;
    }
}
