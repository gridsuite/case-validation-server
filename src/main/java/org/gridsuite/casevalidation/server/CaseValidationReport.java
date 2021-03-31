/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.casevalidation.server;

/**
 * @author Etienne Homer <etienne.homer at rte-france.com>
 */
public class CaseValidationReport {

    private LoadFlowCaseValidationReport loadFlowReport;

    private boolean validationOk;

    public CaseValidationReport(LoadFlowCaseValidationReport loadFlowReport, boolean validationOk) {
        this.loadFlowReport = loadFlowReport;
        this.validationOk = validationOk;
    }

    public LoadFlowCaseValidationReport getLoadFlowReport() {
        return loadFlowReport;
    }

    public boolean isValidationOk() {
        return validationOk;
    }
}

