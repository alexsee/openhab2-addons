package org.openhab.binding.tuya.internal.utils;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.openhab.binding.tuya.internal.entities.TuyaWifiSocketGetRequest;
import org.openhab.binding.tuya.internal.entities.TuyaWifiSocketResponse;
import org.openhab.binding.tuya.internal.entities.TuyaWifiSocketSetRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Transforms requests and responses to corresponding data packets which can be sent to the Tuya device.
 *
 * @author Alexander Seeliger - Initial contribution
 *
 */
public class WifiSocketPacketConverter {

    // packet protocol
    private static final byte[] prefixBytes = new byte[] { 0x00, 0x00, 0x55, (byte) 0xaa };
    private static final byte[] versionBytes = new byte[] { 0x00, 0x00, 0x00, 0x00 };
    private static final byte[] commandBytes = new byte[] { 0x00, 0x00, 0x00, 0x00 };
    private static final byte[] payloadLengthBytes = new byte[] { 0x00, 0x00, 0x00, 0x00 };

    // Payload (data, checksum, suffix).
    private static final byte[] checksumBytes = new byte[] { 0x00, 0x00, 0x00, 0x00 };
    private static final byte[] suffixBytes = new byte[] { 0x00, 0x00, (byte) 0xaa, 0x55 };

    // Command values from https://github.com/codetheweb/tuyapi/wiki/TUYA-Commands
    private static final byte setStatusCommand = 0x07;
    private static final byte getStatusCommand = 10;
    private static final byte getSSIDCommand = 0x0b;

    // Json converter
    private Gson tuyaGsonBuilder;

    /**
     * Default constructor of the packet converter.
     */
    public WifiSocketPacketConverter() {
        tuyaGsonBuilder = new GsonBuilder().create();
    }

    /**
     * Transforms a get request to a packet.
     *
     * @param request the get request object.
     * @param tuyaKey the Tuya key of the corresponding device.
     * @return a data packet.
     */
    public byte[] transformToByteMessage(final TuyaWifiSocketGetRequest request, final String tuyaKey) {
        String jsonCmd = this.tuyaGsonBuilder.toJson(request);
        return sendJsonStringForCommandToDevice(jsonCmd, getStatusCommand, tuyaKey);
    }

    /**
     * Transforms a set request to a packet.
     *
     * @param request the set request object.
     * @param tuyaKey the Tuya key of the corresponding device.
     * @return a data packet.
     */
    public byte[] transformToByteMessage(final TuyaWifiSocketSetRequest request, final String tuyaKey) {
        String jsonCmd = this.tuyaGsonBuilder.toJson(request);
        return sendJsonStringForCommandToDevice(jsonCmd, setStatusCommand, tuyaKey);
    }

    /**
     * Transforms a response packet of the device into {@link TuyaWifiSocketResponse}.
     *
     * @param packet
     * @return {@link TuyaWifiSocketResponse}
     */
    public TuyaWifiSocketResponse transformToTuyaResponse(byte[] packet) {
        String jsonResponse = dataStringFromPacket(packet);
        if (!jsonResponse.isEmpty()) {
            return this.tuyaGsonBuilder.fromJson(jsonResponse, TuyaWifiSocketResponse.class);
        }

        return new TuyaWifiSocketResponse();
    }

    private String dataStringFromPacket(byte[] packetBytes) {
        // Length
        int headerLength = prefixBytes.length + versionBytes.length + commandBytes.length + payloadLengthBytes.length;
        int suffixLength = checksumBytes.length + suffixBytes.length;

        // data
        byte[] packetDataBytes = Arrays.copyOfRange(packetBytes, headerLength, packetBytes.length - suffixLength);

        // to string
        try {
            String packetDataString = new String(packetDataBytes, "UTF-8");
            return packetDataString.trim();
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
    }

    private byte[] sendJsonStringForCommandToDevice(String json, byte command, final String tuyaKey) {
        try {
            // request
            byte[] dataBytes = command == getStatusCommand ? json.getBytes("UTF-8")
                    : encryptedBytesFromJSONForDevice(json, tuyaKey);
            byte[] packetBytes = packetFromDataForCommand(dataBytes, command);

            // send
            return packetBytes;
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
    }

    private byte[] encryptedBytesFromJSONForDevice(final String json, final String tuyaKey) {
        try {
            // key
            byte[] key = tuyaKey.getBytes("UTF-8");

            // encrypt with key.
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

            Cipher encrypt = Cipher.getInstance("AES/ECB/PKCS5Padding");
            encrypt.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] jsonBytes = json.getBytes("UTF-8");
            byte[] encrBytes = encrypt.doFinal(jsonBytes);

            String encryptedJsonBase64String = Base64.getEncoder().encodeToString(encrBytes);

            // create hash
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(("data=" + encryptedJsonBase64String + "||lpv=3.1||" + tuyaKey).getBytes("UTF-8"));

            byte[] digest = md.digest();
            String hashString = DatatypeConverter.printHexBinary(digest).replaceAll("-", "").toLowerCase().substring(8);
            hashString = hashString.substring(0, 16);

            // stitch together.
            return ("3.1" + hashString + encryptedJsonBase64String).getBytes("UTF-8");
        } catch (Exception ex) {
            return null;
        }
    }

    private byte[] packetFromDataForCommand(byte[] dataBytes, byte command) {
        // Set command.
        commandBytes[3] = command;

        // Count payload length (data with checksum and suffix).
        int value = dataBytes.length + checksumBytes.length + suffixBytes.length;
        payloadLengthBytes[3] = (byte) value;
        payloadLengthBytes[2] = (byte) (value >> 8);
        payloadLengthBytes[1] = (byte) (value >> 16);
        payloadLengthBytes[0] = (byte) (value >> 24);

        ByteArrayOutputStream memoryStream = new ByteArrayOutputStream();

        // Header (prefix, version, command, payload length).
        memoryStream.write(prefixBytes, 0, prefixBytes.length);
        memoryStream.write(versionBytes, 0, versionBytes.length);
        memoryStream.write(commandBytes, 0, commandBytes.length);
        memoryStream.write(payloadLengthBytes, 0, payloadLengthBytes.length);

        memoryStream.write(dataBytes, 0, dataBytes.length);
        memoryStream.write(checksumBytes, 0, checksumBytes.length);
        memoryStream.write(suffixBytes, 0, suffixBytes.length);

        return memoryStream.toByteArray();
    }

}
