/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.tuya.internal;

import java.util.Collections;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link TuyaBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Alexander Seeliger - Initial contribution
 */
@NonNullByDefault
public class TuyaBindingConstants {

    /**
     * The binding id.
     */
    public static final String BINDING_ID = "tuya";

    /**
     * List of all Thing Type UIDs.
     */
    public static final ThingTypeUID THING_TYPE_WIFI_SOCKET = new ThingTypeUID(BINDING_ID, "wifiSocket");

    /**
     * List of all Channel ids
     */
    public static final String WIFI_SOCKET_CHANNEL_ID = "switch";

    /**
     * The supported thing types.
     */
    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.singleton(THING_TYPE_WIFI_SOCKET);

    // -------------- Configuration arguments ----------------
    /**
     * Wifi socket update interval configuration argument key.
     */
    public static final String UPDATE_INTERVAL_ARG = "updateInterval";
    /**
     * Host address configuration argument key.
     */
    public static final String HOST_ADDRESS_ARG = "hostAddress";
    /**
     * Tuya Id configuration argument key.
     */
    public static final String TUYA_ID_ARG = "tuyaId";
    /**
     * Tuya Key configuration argument key.
     */
    public static final String TUYA_KEY_ARG = "tuyaKey";

    // -------------- Default values ----------------
    /**
     * Default Wifi socket refresh interval.
     */
    public static final long DEFAULT_REFRESH_INTERVAL = 60;

    /**
     * Default Wifi socket default UDP port.
     */
    public static final int WIFI_SOCKET_DEFAULT_TCP_PORT = 6668;
}
