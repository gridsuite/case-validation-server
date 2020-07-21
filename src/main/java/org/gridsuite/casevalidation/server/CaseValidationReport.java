/**
=======
package org.gridsuite.casevalidation.server; /**
>>>>>>> master
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.casevalidation.server;

import java.util.Map;

/**
 * @author Etienne Homer <etienne.homer at rte-france.com>
 */
public class CaseValidationReport {

    private Map<String, String> metrics;

    public Map<String, String> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<String, String> metrics) {
        this.metrics = metrics;
    }

    public boolean isLoadFlowOk() {
        return loadFlowOk;
    }

    public void setLoadFlowOk(boolean loadFlowOk) {
        this.loadFlowOk = loadFlowOk;
    }

    private boolean loadFlowOk;

    public CaseValidationReport(Map<String, String> metrics, boolean loadFlowOk) {
        this.metrics = metrics;
        this.loadFlowOk = loadFlowOk;
    }

}
