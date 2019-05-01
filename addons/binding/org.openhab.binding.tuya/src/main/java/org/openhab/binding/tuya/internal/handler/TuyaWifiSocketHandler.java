/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.tuya.internal.handler;

import static org.openhab.binding.tuya.internal.TuyaBindingConstants.WIFI_SOCKET_CHANNEL_ID;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.tuya.internal.TuyaBindingConstants;
import org.openhab.binding.tuya.internal.entities.TuyaWifiSocketGetRequest;
import org.openhab.binding.tuya.internal.entities.TuyaWifiSocketResponse;
import org.openhab.binding.tuya.internal.entities.TuyaWifiSocketSetRequest;
import org.openhab.binding.tuya.internal.enums.TuyaWifiSocketRequestType;
import org.openhab.binding.tuya.internal.utils.WifiSocketPacketConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link TuyaWifiSocketHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Alexander Seeliger - Initial contribution
 */
public class TuyaWifiSocketHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(TuyaWifiSocketHandler.class);

    private String hostAddress;
    private String tuyaId;
    private String tuyaKey;

    private Long updateInterval = TuyaBindingConstants.DEFAULT_REFRESH_INTERVAL;

    private final WifiSocketPacketConverter converter = new WifiSocketPacketConverter();
    private ScheduledFuture<?> keepAliveJob;
    private long latestUpdate = -1;

    /**
     * Default constructor.
     *
     * @param thing the thing of the handler.
     * @throws MacAddressNotValidException if the mac address isn't valid.
     */
    public TuyaWifiSocketHandler(final Thing thing) {
        super(thing);
        this.saveHostAddressFromConfiguration(this.getConfig());
        this.saveTuyaIdFromConfiguration(this.getConfig());
        this.saveTuyaKeyFromConfiguration(this.getConfig());
        this.saveUpdateIntervalFromConfiguration(this.getConfig());
    }

    @Override
    public void handleCommand(final ChannelUID channelUID, final Command command) {
        if (channelUID.getId().equals(WIFI_SOCKET_CHANNEL_ID)) {
            logger.debug("Tuya socket command received: {}", command);

            if (command == OnOffType.ON) {
                this.sendCommand(TuyaWifiSocketRequestType.ON);
            } else if (command == OnOffType.OFF) {
                this.sendCommand(TuyaWifiSocketRequestType.OFF);
            } else if (command == RefreshType.REFRESH) {
                this.sendCommand(TuyaWifiSocketRequestType.GET);
            }
        }
    }

    @Override
    public void handleRemoval() {
        // stop update thread
        this.keepAliveJob.cancel(true);
        super.handleRemoval();
    }

    /**
     * Starts one thread that querys the state of the socket, after the defined refresh interval.
     */
    private void initGetStatusAndKeepAliveThread() {
        if (this.keepAliveJob != null) {
            this.keepAliveJob.cancel(true);
        }
        // try with handler port if is null
        Runnable runnable = () -> {
            logger.debug(
                    "Begin of Socket keep alive thread routine. Current configuration update interval: {} seconds.",
                    TuyaWifiSocketHandler.this.updateInterval);

            long now = System.currentTimeMillis();
            long timePassedFromLastUpdateInSeconds = (now - TuyaWifiSocketHandler.this.latestUpdate) / 1000;

            logger.trace("Latest Update: {} Now: {} Delta: {} seconds", TuyaWifiSocketHandler.this.latestUpdate, now,
                    timePassedFromLastUpdateInSeconds);

            boolean considerThingOffline = (TuyaWifiSocketHandler.this.latestUpdate < 0)
                    || (timePassedFromLastUpdateInSeconds > (TuyaWifiSocketHandler.this.updateInterval * 4));
            if (considerThingOffline) {
                logger.debug(
                        "No updates have been received for a long long time will put the thing with host address {} OFFLINE.",
                        TuyaWifiSocketHandler.this.getHostAddress());
                TuyaWifiSocketHandler.this.updateStatus(ThingStatus.OFFLINE);
            }

            // request gpio status
            TuyaWifiSocketHandler.this.sendCommand(TuyaWifiSocketRequestType.GET);
        };
        this.keepAliveJob = this.scheduler.scheduleWithFixedDelay(runnable, 1,
                TuyaWifiSocketHandler.this.updateInterval, TimeUnit.SECONDS);
    }

    @Override
    public void initialize() {
        this.initGetStatusAndKeepAliveThread();
        updateStatus(ThingStatus.ONLINE);
        this.saveConfigurationsUsingCurrentStates();
    }

    /**
     * Method called by {@link TuyaWifiSocketMediator} when one new message has been received for this handler.
     *
     * @param receivedMessage the received {@link TuyaWifiSocketResponse}.
     */
    public void newReceivedResponseMessage(final TuyaWifiSocketResponse receivedMessage) {
        if (receivedMessage.getDps().size() > 0) {
            // get first option
            Object val1 = receivedMessage.getDps().get("1");

            if (val1 instanceof Boolean) {
                this.updateState(TuyaBindingConstants.WIFI_SOCKET_CHANNEL_ID,
                        (Boolean) val1 ? OnOffType.ON : OnOffType.OFF);
            }
        }

        this.updateStatus(ThingStatus.ONLINE);
        this.latestUpdate = System.currentTimeMillis();
    }

    /**
     * Saves the host address from configuration in field.
     *
     * @param configuration The {@link Configuration}
     */
    private void saveHostAddressFromConfiguration(final Configuration configuration) {
        if ((configuration != null) && (configuration.get(TuyaBindingConstants.HOST_ADDRESS_ARG) != null)) {
            this.hostAddress = String.valueOf(configuration.get(TuyaBindingConstants.HOST_ADDRESS_ARG));
        }
    }

    /**
     * Saves the host address from configuration in field.
     *
     * @param configuration The {@link Configuration}
     */
    private void saveUpdateIntervalFromConfiguration(final Configuration configuration) {
        this.updateInterval = TuyaBindingConstants.DEFAULT_REFRESH_INTERVAL;
        if ((configuration != null)
                && (configuration.get(TuyaBindingConstants.UPDATE_INTERVAL_ARG) instanceof BigDecimal)
                && (((BigDecimal) configuration.get(TuyaBindingConstants.UPDATE_INTERVAL_ARG)).longValue() > 0)) {
            this.updateInterval = ((BigDecimal) configuration.get(TuyaBindingConstants.UPDATE_INTERVAL_ARG))
                    .longValue();
        }
    }

    /**
     * Saves the tuya id from configuration in field.
     *
     * @param configuration The {@link Configuration}
     */
    private void saveTuyaIdFromConfiguration(final Configuration configuration) {
        if ((configuration != null) && (configuration.get(TuyaBindingConstants.TUYA_ID_ARG) != null)) {
            this.tuyaId = String.valueOf(configuration.get(TuyaBindingConstants.TUYA_ID_ARG));
        }
    }

    /**
     * Saves the tuya key from configuration in field.
     *
     * @param configuration The {@link Configuration}
     */
    private void saveTuyaKeyFromConfiguration(final Configuration configuration) {
        if ((configuration != null) && (configuration.get(TuyaBindingConstants.TUYA_KEY_ARG) != null)) {
            this.tuyaKey = String.valueOf(configuration.get(TuyaBindingConstants.TUYA_KEY_ARG));
        }
    }

    /**
     * Sends one command to the Wifi Socket. If the host address is not set, it will trigger the lookup of the
     * host address and discard the command queried.
     *
     * @param type the {@link TuyaWifiSocketRequestType} of the command.
     */
    private void sendCommand(final TuyaWifiSocketRequestType type) {
        logger.debug("Send command for host addr: {} with type: {} with hostaddress: {}", this.getHostAddress(),
                type.name(), this.hostAddress);

        InetAddress address;
        try {
            address = InetAddress.getByName(this.hostAddress);
            this.sendRequestPacket(type, address);
        } catch (UnknownHostException e) {
            logger.debug("Host Address not found: {}. Will lookup Mac address.");
        }
    }

    /**
     * Sends {@link TuyaWifiSocketRequest} to the passed {@link InetAddress}.
     *
     * @param requestPacket the {@link TuyaWifiSocketRequest}.
     * @param address the {@link InetAddress}.
     */
    private void sendRequestPacket(final TuyaWifiSocketRequestType request, final InetAddress address) {
        Socket socket = null;
        try {
            byte[] message = getRequestPacket(request);
            logger.trace("Preparing packet to send...");

            if (address != null && message != null) {
                socket = new Socket(address, TuyaBindingConstants.WIFI_SOCKET_DEFAULT_TCP_PORT);

                // send request to socket
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.write(message);
                logger.debug("Sent packet to address: {} and port {}", address,
                        TuyaBindingConstants.WIFI_SOCKET_DEFAULT_TCP_PORT);

                // receive response
                BufferedInputStream in = new BufferedInputStream(socket.getInputStream());

                byte[] tmpBuffer = new byte[1024];
                int numReceivedBytes = in.read(tmpBuffer);

                byte[] responsePacket = Arrays.copyOf(tmpBuffer, numReceivedBytes);
                TuyaWifiSocketResponse response = converter.transformToTuyaResponse(responsePacket);

                newReceivedResponseMessage(response);
            }
        } catch (Exception exception) {
            logger.debug("Something wrong happen sending the packet to address: {} and port {}... msg: {}", address,
                    TuyaBindingConstants.WIFI_SOCKET_DEFAULT_TCP_PORT, exception.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private byte[] getRequestPacket(TuyaWifiSocketRequestType request) {
        if (request.equals(TuyaWifiSocketRequestType.GET)) {
            TuyaWifiSocketGetRequest getRequest = new TuyaWifiSocketGetRequest(tuyaId, tuyaId);
            return converter.transformToByteMessage(getRequest, tuyaKey);
        } else if (request.equals(TuyaWifiSocketRequestType.ON)) {
            int t = (int) (System.currentTimeMillis() / 1000);
            Map<String, Object> dps = new HashMap<>();
            dps.put("1", true);
            dps.put("2", 0);

            TuyaWifiSocketSetRequest setRequest = new TuyaWifiSocketSetRequest(tuyaId, tuyaId, t, "", dps);
            return converter.transformToByteMessage(setRequest, tuyaKey);
        } else if (request.equals(TuyaWifiSocketRequestType.OFF)) {
            int t = (int) (System.currentTimeMillis() / 1000);
            Map<String, Object> dps = new HashMap<>();
            dps.put("1", false);
            dps.put("2", 0);

            TuyaWifiSocketSetRequest setRequest = new TuyaWifiSocketSetRequest(tuyaId, tuyaId, t, "", dps);
            return converter.transformToByteMessage(setRequest, tuyaKey);
        }

        return null;
    }

    @Override
    protected void updateConfiguration(final Configuration configuration) {
        this.latestUpdate = -1;

        this.saveHostAddressFromConfiguration(configuration);
        this.saveTuyaIdFromConfiguration(configuration);
        this.saveTuyaKeyFromConfiguration(configuration);
        this.saveUpdateIntervalFromConfiguration(configuration);

        this.initGetStatusAndKeepAliveThread();
        this.saveConfigurationsUsingCurrentStates();
    }

    /**
     * Save the current runtime configuration of the handler in configuration mechanism.
     */
    private void saveConfigurationsUsingCurrentStates() {
        Map<String, Object> map = new HashMap<>();
        map.put(TuyaBindingConstants.HOST_ADDRESS_ARG, this.hostAddress);
        map.put(TuyaBindingConstants.TUYA_ID_ARG, this.tuyaId);
        map.put(TuyaBindingConstants.TUYA_KEY_ARG, this.tuyaKey);
        map.put(TuyaBindingConstants.UPDATE_INTERVAL_ARG, this.updateInterval);

        Configuration newConfiguration = new Configuration(map);
        super.updateConfiguration(newConfiguration);
    }

    // SETTERS AND GETTERS
    public String getHostAddress() {
        return this.hostAddress;
    }

    public String getTuyaId() {
        return this.tuyaId;
    }

    public String getTuyaKey() {
        return this.tuyaKey;
    }
}
