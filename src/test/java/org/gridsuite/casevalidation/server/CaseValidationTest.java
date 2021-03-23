/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.casevalidation.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.powsybl.commons.PowsyblException;
import com.powsybl.iidm.network.*;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import com.powsybl.network.store.client.NetworkStoreService;
import com.powsybl.network.store.client.PreloadingStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Etienne Homer <etienne.homer at rte-france.com>
 */
@RunWith(SpringRunner.class)
@WebMvcTest(CaseValidationController.class)
@ContextConfiguration(classes = {CaseValidationApplication.class})
public class CaseValidationTest {
    private Network network;
    private Bus bus1;
    private Bus bus2;
    private Bus bus3;
    private Bus bus4;
    private Line line12;
    private Line line24;
    private TwoWindingsTransformer t2wt;
    private TwoWindingsTransformer t2wt2;
    private ThreeWindingsTransformer t3wt;
    private Load load2;
    private Load load3;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private NetworkStoreService networkStoreService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test() throws Exception {
        UUID testNetworkId = UUID.fromString("7928181c-7977-4592-ba19-88027e4254e4");
        UUID notFoundNetworkId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

        given(networkStoreService.getNetwork(notFoundNetworkId, PreloadingStrategy.COLLECTION)).willThrow(new PowsyblException());

        // network not existing
        mvc.perform(put("/v1/networks/{networkUuid}/validate", notFoundNetworkId))
                .andExpect(status().isNotFound());

        Network network = createNetworkWithT2wt();

        t2wt.getRatioTapChanger().setRegulationTerminal(load2.getTerminal());
        t2wt.getRatioTapChanger().setTargetDeadband(0).setRegulating(true);
        line12.setX(0.0001);

        given(networkStoreService.getNetwork(testNetworkId, PreloadingStrategy.COLLECTION)).willReturn(network);
        MvcResult result = mvc.perform(put("/v1/networks/{networkUuid}/validate", testNetworkId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ObjectMapper om = new ObjectMapper();
        JsonNode loadFlowReports = om.readTree(result.getResponse().getContentAsString()).path("loadFlowReports");
        JsonNode validationOk = om.readTree(result.getResponse().getContentAsString()).path("validationOk");
        assertEquals(loadFlowReports.size(), 2);
        assertTrue(validationOk.asBoolean());

        //Make loadflow diverge with default and relaxed parameters
        network.getGenerator("GEN_1").setTargetV(10);

        given(networkStoreService.getNetwork(testNetworkId, PreloadingStrategy.COLLECTION)).willReturn(network);
        result = mvc.perform(put("/v1/networks/{networkUuid}/validate", testNetworkId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        System.out.println(result.getResponse().getContentAsString());
        assertTrue(result.getResponse().getContentAsString().contains("\"validationOk\":false"));

        loadFlowReports = om.readTree(result.getResponse().getContentAsString()).path("loadFlowReports");
        validationOk = om.readTree(result.getResponse().getContentAsString()).path("validationOk");
        assertEquals(loadFlowReports.size(), 2);
        assertFalse(validationOk.asBoolean());
    }

    public Network createNetwork() {
        return EurostagTutorialExample1Factory.create();
    }

    /**
     * A very small network to test with a T2wt.
     *
     *     G1        LD2      LD3
     *     |    L12   |        |
     *     |  ------- |        |
     *     B1         B2      B3
     *                  \    /
     *                   T2WT
     */
    private Network createNetworkWithT2wt() {
        Network network = createBaseNetwork("two-windings-transformer-control");
        t2wt = network.getSubstation("SUBSTATION").newTwoWindingsTransformer()
                .setId("T2wT")
                .setVoltageLevel1("VL_2")
                .setVoltageLevel2("VL_3")
                .setRatedU1(132.0)
                .setRatedU2(33.0)
                .setR(17.0)
                .setX(10.0)
                .setG(0.00573921028466483)
                .setB(0.000573921028466483)
                .setBus1("BUS_2")
                .setBus2("BUS_3")
                .add();

        t2wt.newRatioTapChanger()
                .beginStep()
                .setRho(0.9)
                .setR(0.1089)
                .setX(0.01089)
                .setG(0.8264462809917356)
                .setB(0.08264462809917356)
                .endStep()
                .beginStep()
                .setRho(1.0)
                .setR(0.121)
                .setX(0.0121)
                .setG(0.8264462809917356)
                .setB(0.08264462809917356)
                .endStep()
                .beginStep()
                .setRho(1.1)
                .setR(0.1331)
                .setX(0.01331)
                .setG(0.9090909090909092)
                .setB(0.09090909090909092)
                .endStep()
                .setTapPosition(0)
                .setLoadTapChangingCapabilities(true)
                .setRegulating(false)
                .setTargetV(33.0)
                .setRegulationTerminal(load3.getTerminal())
                .add();

        return network;
    }

    private Network createBaseNetwork(String id) {

        Network network = Network.create(id, "test");

        Substation substation1 = network.newSubstation()
                .setId("SUBSTATION1")
                .setCountry(Country.FR)
                .add();
        VoltageLevel vl1 = substation1.newVoltageLevel()
                .setId("VL_1")
                .setNominalV(132.0)
                .setLowVoltageLimit(118.8)
                .setHighVoltageLimit(145.2)
                .setTopologyKind(TopologyKind.BUS_BREAKER)
                .add();
        bus1 = vl1.getBusBreakerView().newBus()
                .setId("BUS_1")
                .add();
        vl1.newGenerator()
                .setId("GEN_1")
                .setBus("BUS_1")
                .setMinP(0.0)
                .setMaxP(140)
                .setTargetP(25)
                .setTargetV(135)
                .setVoltageRegulatorOn(true)
                .add();

        Substation substation = network.newSubstation()
                .setId("SUBSTATION")
                .setCountry(Country.FR)
                .add();
        VoltageLevel vl2 = substation.newVoltageLevel()
                .setId("VL_2")
                .setNominalV(132.0)
                .setLowVoltageLimit(118.8)
                .setHighVoltageLimit(145.2)
                .setTopologyKind(TopologyKind.BUS_BREAKER)
                .add();
        bus2 = vl2.getBusBreakerView().newBus()
                .setId("BUS_2")
                .add();
        load2 = vl2.newLoad()
                .setId("LOAD_2")
                .setBus("BUS_2")
                .setP0(11.2)
                .setQ0(7.5)
                .add();

        VoltageLevel vl3 = substation.newVoltageLevel()
                .setId("VL_3")
                .setNominalV(33.0)
                .setLowVoltageLimit(0)
                .setHighVoltageLimit(100)
                .setTopologyKind(TopologyKind.BUS_BREAKER)
                .add();
        bus3 = vl3.getBusBreakerView().newBus()
                .setId("BUS_3")
                .add();
        load3 = vl3.newLoad()
                .setId("LOAD_3")
                .setBus("BUS_3")
                .setQ0(0)
                .setP0(5)
                .add();

        line12 = network.newLine()
                .setId("LINE_12")
                .setVoltageLevel1("VL_1")
                .setVoltageLevel2("VL_2")
                .setBus1("BUS_1")
                .setBus2("BUS_2")
                .setR(1.05)
                .setX(10.0)
                .setG1(0.0000005)
                .setG2(0.)
                .setB1(0.)
                .setB2(0.)
                .add();

        return network;
    }
}
