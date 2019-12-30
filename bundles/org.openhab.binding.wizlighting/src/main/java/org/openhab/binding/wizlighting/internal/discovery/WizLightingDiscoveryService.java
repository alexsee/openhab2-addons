/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.wizlighting.internal.discovery;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.wizlighting.internal.WizLightingBindingConstants;
import org.openhab.binding.wizlighting.internal.handler.WizLightingMediator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the {@link DiscoveryService} for the Wizlighting Items.
 *
 * @author Sriram Balakrishnan - Initial contribution
 *
 */
@Component(configurationPid = "discovery.wizlighting", service = DiscoveryService.class, immediate = true)
@NonNullByDefault
public class WizLightingDiscoveryService extends AbstractDiscoveryService {

    private final Logger logger = LoggerFactory.getLogger(WizLightingDiscoveryService.class);
    private @Nullable WizLightingMediator mediator;

    /**
     * Used by OSGI to inject the mediator in the discovery service.
     *
     * @param mediator the mediator
     */
    @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.STATIC)
    public void setMediator(final WizLightingMediator mediator) {
        logger.trace("Mediator has been injected on discovery service.");

        this.mediator = mediator;
        mediator.setDiscoveryService(this);
    }

    /**
     * Used by OSGI to unset the mediator in the discovery service.
     *
     * @param mediator the mediator
     */
    public void unsetMediator(final WizLightingMediator mitsubishiMediator) {
        logger.trace("Mediator has been unsetted from discovery service.");

        WizLightingMediator mediator = this.mediator;
        if (mediator != null) {
            mediator.setDiscoveryService(null);
            this.mediator = null;
        }
    }

    /**
     * Constructor of the discovery service.
     *
     * @throws IllegalArgumentException if the timeout < 0
     */
    public WizLightingDiscoveryService() throws IllegalArgumentException {
        super(WizLightingBindingConstants.SUPPORTED_THING_TYPES_UIDS, 4);
    }

    @Override
    public Set<ThingTypeUID> getSupportedThingTypes() {
        return WizLightingBindingConstants.SUPPORTED_THING_TYPES_UIDS;
    }

    @Override
    protected void startScan() {
    }

    /**
     * Method called by mediator, when receive one packet from one unknown Wifi
     * Socket.
     *
     * @param bulbMacAddress the mac address from the device.
     * @param bulbIpAddress the host address from the device.
     */
    public void discoveredLight(final String lightMacAddress, final String lightIpAddress) {
        Map<String, Object> properties = new HashMap<>(1);
        properties.put(WizLightingBindingConstants.BULB_MAC_ADDRESS_ARG, lightMacAddress);
        properties.put(WizLightingBindingConstants.BULB_IP_ADDRESS_ARG, lightIpAddress);

        ThingUID newThingId = new ThingUID(WizLightingBindingConstants.THING_TYPE_WIZ_BULB, lightMacAddress);
        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(newThingId).withProperties(properties)
                .withLabel("Wizlighting Bulb").withRepresentationProperty(lightMacAddress).build();

        logger.debug("A new WiZ bulb appeared with mac address '{}' and host address '{}'", lightMacAddress,
                lightIpAddress);

        this.thingDiscovered(discoveryResult);
    }

    // SETTERS AND GETTERS
    /**
     * Gets the {@link WizLightingMediator} of this binding.
     *
     * @return {@link WizLightingMediator}.
     */
    public @Nullable WizLightingMediator getMediator() {
        return this.mediator;
    }
}
