package hu.bpe.ipmapper;


public class DataStruct {
    public double latitude, longitude, accuracy;
    public boolean fineLocation, isWiFi, isCellular, isVPN, isUsb, isBluetooth, isLocationValid;
    public String appToken;

    @Override
    public String toString() {
        return "DataStruct{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", accuracy=" + accuracy +
                ", fineLocation=" + fineLocation +
                ", isWiFi=" + isWiFi +
                ", isCellular=" + isCellular +
                ", isVPN=" + isVPN +
                ", isUsb=" + isUsb +
                ", isBluetooth=" + isBluetooth +
                ", isLocationValid=" + isLocationValid +
                ", appToken='" + appToken + '\'' +
                '}';
    }
}