package org.openhab.binding.tuya.internal.entities;

import java.util.Map;

/**
 * This POJO represents one Wifi Socket request.
 *
 * @author Alexander Seeliger - Initial contribution
 *
 */
public class TuyaWifiSocketSetRequest {

    private String devId;
    private String gwId;
    private int t;
    private String uId;

    private Map<String, Object> dps;

    /**
     * Default constructor.
     *
     * @param devId the device id.
     * @param gwId the gateway id (not needed for most devices).
     * @param t the current time in seconds.
     * @param uId the uId must be set to the empty string (may be used for other tuya devices).
     * @param dps the states for the device.
     */
    public TuyaWifiSocketSetRequest(final String devId, final String gwId, final int t, final String uId,
            final Map<String, Object> dps) {
        this.devId = devId;
        this.gwId = gwId;
        this.t = t;
        this.uId = uId;
        this.dps = dps;
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

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public Map<String, Object> getDps() {
        return dps;
    }

    public void setDps(Map<String, Object> dps) {
        this.dps = dps;
    }

}
