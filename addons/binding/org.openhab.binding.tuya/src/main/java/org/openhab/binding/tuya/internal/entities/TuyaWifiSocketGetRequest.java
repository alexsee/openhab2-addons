package org.openhab.binding.tuya.internal.entities;

/**
 * This POJO represents one Wifi Socket request.
 *
 * @author Alexander Seeliger - Initial contribution
 *
 */
public class TuyaWifiSocketGetRequest {

    private String devId;
    private String gwId;

    /**
     * Default constructor.
     *
     * @param devId the device id.
     * @param gwId the gateway id (not needed for most devices)
     */
    public TuyaWifiSocketGetRequest(final String devId, final String gwId) {
        this.devId = devId;
        this.gwId = gwId;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getGwId() {
        return gwId;
    }

    public void setGwId(String gwId) {
        this.gwId = gwId;
    }

}
