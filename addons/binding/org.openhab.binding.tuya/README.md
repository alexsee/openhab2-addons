# Tuya Binding

This binding integrates the Tuya devices .

## Supported Things

- Tuya Switch


## Discovery

Automatic discovery is currently not supported.

## Binding Configuration

The binding does not need any configuration.

## Thing Configuration

To configure a device manually, the binding needs the host address, the tuya id, and the tuya key of the socket device.

Wifi Socket thing parameters:

| Parameter ID | Parameter Type | Mandatory | Description | Default |
|--------------|----------------|------|------------------|-----|
| hostAddress | text | true | The socket Host address |  |
| tuyaId | text | true | The tuya id of the socket |  |
| tuyaKey | text | true | The tuya local key of the socket |  |
| updateInterval | integer | false | Update time interval in seconds to request the status of the socket. | 60 |


E.g.

```
Thing tuya:wifiSocket:socketLamp [ hostAddress = "192.168.10.XX", tuyaId = "XXX", tuyaKey = "XXX" ]
```

## Channels

The Binding support the following channel:

| Channel Type ID | Item Type | Description                                          | Access |
|-----------------|-----------|------------------------------------------------------|--------|
| switch          | Switch    | Power state of the Switch (ON/OFF)                   | R/W    |