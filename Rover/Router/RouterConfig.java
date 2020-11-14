package Rover.Router;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author gautamgadipudi
 *
 * This class has all the config details for the router.
 */
public final class RouterConfig {

    String multicastIP = "224.0.0.9";
    int multicastPort = 1337;
    int fileExchangePort = 1338;

    List<String> AddressTemplate = Arrays.asList("10", "0", "-1", "0");

    byte RouterId;
    String Address;

    public RouterConfig(byte routerId) {
        this.RouterId = routerId;
        this.Address = getAddress();
    }

    public int getMulticastPort() {
        return multicastPort;
    }

    /**
     * Replace -1 in AddressTemplate with router id.
     */
    public String getAddress () {
        Collections.replaceAll(AddressTemplate, "-1", Byte.toString(this.RouterId));

        return String.join(".", AddressTemplate);
    }
}
