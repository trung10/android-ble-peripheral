package itan.com.bluetoothle;

import java.util.UUID;

/**
 * Created by itanbarpeled on 28/01/2018.
 */

public class Constants {


    public static final int SERVER_MSG_FIRST_STATE = 1;
    public static final int SERVER_MSG_SECOND_STATE = 2;

    /*
    TODO bluetooth
    better to use different Bluetooth Service,
    instead of Heart Rate Service:
    https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.service.heart_rate.xml.

    maybe Object Transfer Service is more suitable:
    https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.service.object_transfer.xml
     */
    public static final UUID HEART_RATE_SERVICE_UUID = UUID.fromString("25b28cef-a1a7-4eca-b3bf-8b91e3925bd8");
    public static final UUID APP_COMMANDS_UUID = UUID.fromString("c1f481dc-1ec6-4477-9e8b-b61cba95fed2");

    public static final UUID PCM_COMMANDS_UUID = UUID.fromString("126a52ab-c007-488a-bdf0-10d2e805dc77");
    public static final UUID EVENT_UUID = UUID.fromString("D76F7FD4-8501-4142-AB81-D464ED0B2435");
    public static final UUID ERROR_UUID = UUID.fromString("96ae3a99-2fbc-4090-b522-1098bfceb051");

    private static UUID convertFromInteger(int i) {
        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = i & 0xFFFFFFFF;
        return new UUID(MSB | (value << 32), LSB);
    }
}
