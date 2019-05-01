# Tuya Binding

This binding integrates the Tuya devices .

## Supported Things

- Tuya Switch


## Discovery

TODO

## Binding Configuration

TODO

## Thing Configuration

To configure a device manually we need its ip address, mac address and homeId. These can be found in the ios or android app.

Wifi Socket thing parameters:

| Parameter ID | Parameter Type | Mandatory | Description | Default |
|--------------|----------------|------|------------------|-----|
| macAddress | text | true | The bulb MAC address |  |
| ipAddress | text | true | The bulb Ip address |  |
| homeId | text | true | The homeId the bulb belongs to |  |
| updateInterval | integer | false | Update time interval in seconds to request the status of the bulb. | 60 |


E.g.

```
Thing wizlighting:wizBulb:lamp [ macAddress="accf23343c50", ipAddress="192.168.0.183", homeId=18529 ]
```

## Channels

The Binding support the following channel:

| Channel Type ID | Item Type | Description                                          | Access |
|-----------------|-----------|------------------------------------------------------|--------|
| switch          | Switch    | Power state of the Bulb (ON/OFF)                     | R/W    |
| color           | Color     | Color of the RGB LEDs                                | R/W    |
| white           | Dimmer    | Brightness of the first (warm) white LEDs (min=0, max=100) | R/W    |
| white2          | Dimmer    | Brightness of the second (warm) white LEDs (min=0, max=100) | R/W    |
| scene           | String    | Program to run by the controller (i.e. color cross fade, strobe, etc.) | R/W |
| speed           | Dimmer    | Speed of the program                                 | R/W    |

