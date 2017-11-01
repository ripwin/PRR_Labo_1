/**
 * file:        Protocol.java
 * created:     5.10.2017
 */
package ch.heigvd.prr.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * The Protocol class contains the protocol constants and settings used by the
 * Slave and Master applications to communicate.
 */
public class Protocol {

    /**
     * Enum to identify the PTP messages
     */
    public enum Code {
        SYNC, FOLLOW_UP, DELAY_REQUEST, DELAY_RESPONSE
    };

    // The used multicast port 
    public static final int MULTICAST_PORT = 12000;

    // The used port to communicate DELAY_REQUESTS
    public static final int DELAY_COMMUNICATION_PORT = 13000;

    //  SYNC waiting interval
    public static final int INTERVAL_SYNC = 4000;

    // Min of waiting range for DELAY_REQUEST messages
    public static final int INTERVAL_DELAY_MIN = INTERVAL_SYNC * 4;

    // MAX of waiting range for DELAY_REQUEST messages
    public static final int INTERVAL_DELAY_MAX = INTERVAL_SYNC * 60;

    /**
     * Convert Code enum value to byte.
     *
     * @param code the Code enum code
     * @return the corresponding byte value
     */
    public static byte getByte(Protocol.Code code) {
        return (byte) code.ordinal();
    }

    /**
     * Convert byte value to corresponding Code enum value
     *
     * @param byteCode the byte value
     * @return the Code enum value
     */
    public static Code getEnum(byte byteCode) {
        if (byteCode >= Code.values().length) {
            throw new IllegalArgumentException();
        }

        return Code.values()[byteCode];
    }

    /**
     * Get the multicast address. This method forces the use of IPV4 on MacOS
     * Source: https://stackoverflow.com/questions/18747134/
     * getting-cant-assign-requested-address-java-net-socketexception-using-ehcache
     * 01.11.2017
     *
     * @return the multicast address.
     * @throws UnknownHostException
     */
    public static InetAddress getMulticastAddress() throws UnknownHostException {
        // To prevent MacOS using ipv6 addresses by default
        System.setProperty("java.net.preferIPv4Stack", "true");
        return InetAddress.getByName("239.0.0.1");
    }
}
