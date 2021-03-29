/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.casevalidation.server;

/**
 * @author Etienne Homer <etienne.homer at rte-france.com>
 */
public class LoadFlowCaseValidationReport {

    private boolean ok;

    private Status status;

    public LoadFlowCaseValidationReport(boolean ok, Status status) {
        this.ok = ok;
        this.status = status;
    }

    public boolean isOk() {
        return ok;
    }

    public Status getStatus() {
        return status;
    }

    public enum Status {
        CONVERGED_ON_1ST_LF,
        CONVERGED_ON_2D_LF,
        FAILED;
    }
}
