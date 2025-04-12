package ch.uzh.ifi.hase.soprafs24.constant;

public enum BasemapType {
    SATELLITE, SATELLITE_HYBRID, OPEN_STREET_MAP, DEFAULT;

    public static BasemapType getDefault() {
        return BasemapType.OPEN_STREET_MAP; 
    }
}
