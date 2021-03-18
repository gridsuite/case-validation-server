/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.casevalidation.server;

import java.util.List;

/**
 * @author Etienne Homer <etienne.homer at rte-france.com>
 */
public class CaseValidationReport {

    List<LoadFlowCaseValidationReport> loadFlowReports;

    private boolean validationOk;

    public CaseValidationReport(List<LoadFlowCaseValidationReport> loadFlowReports, boolean validationOk) {
        this.loadFlowReports = loadFlowReports;
        this.validationOk = validationOk;
    }

    public List<LoadFlowCaseValidationReport> getLoadFlowReports() {
        return loadFlowReports;
    }

    public boolean isValidationOk() {
        return validationOk;
    }
}

