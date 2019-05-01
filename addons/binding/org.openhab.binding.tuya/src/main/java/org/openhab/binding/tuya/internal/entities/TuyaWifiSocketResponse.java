package org.openhab.binding.tuya.internal.entities;

import java.util.HashMap;
import java.util.Map;

/**
 * This POJO represents one Wifi Socket Response.
 *
 * @author Alexander Seeliger - Initial contribution
 *
 */
public class TuyaWifiSocketResponse {

    private String devId;

    private Map<String, Object> dps;

    /**
     * Default constructor.
     */
    public TuyaWifiSocketResponse() {
        this.dps = new HashMap<>();
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public Map<String, Object> getDps() {
        return dps;
    }

    public void setDps(Map<String, Object> dps) {
        this.dps = dps;
    }

}
