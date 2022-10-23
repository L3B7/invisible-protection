package hu.bpe.ipmapper;

import javax.persistence.*;

@Entity
@Table(name = "ipmapperdb")
public class POJOdata {

    public POJOdata(DataStruct ds, String addr, int _flag){
        latitude=ds.latitude;
        longitude=ds.longitude;
        accuracy = ds.accuracy;
        finelocation = ds.fineLocation;
        iswifi = ds.isWiFi;
        iscellular =ds.isCellular;
        isvpn =ds.isVPN;
        isusb =ds.isUsb;
        isbluetooth =ds.isBluetooth;
        islocationvalid =ds.isLocationValid;
        apptoken =ds.appToken;
        ipaddress =addr;
        flag=_flag;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    public POJOdata() {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public boolean isFinelocation() {
        return finelocation;
    }

    public void setFinelocation(boolean fineLocation) {
        this.finelocation = fineLocation;
    }

    public boolean isWiFi() {
        return iswifi;
    }

    public void setWiFi(boolean iswifi) {
        this.iswifi = iswifi;
    }

    public boolean isCellular() {
        return iscellular;
    }

    public void setIscellular(boolean cellular) {
        iscellular = cellular;
    }

    public boolean isVPN() {
        return isvpn;
    }

    public void setIsvpn(boolean VPN) {
        isvpn = VPN;
    }

    public boolean isUsb() {
        return isusb;
    }

    public void setUsb(boolean isusb) {
        this.isusb = isusb;
    }

    public boolean isBluetooth() {
        return isbluetooth;
    }

    public void setIsbluetooth(boolean bluetooth) {
        isbluetooth = bluetooth;
    }

    public boolean isLocationValid() {
        return islocationvalid;
    }

    public void setIslocationvalid(boolean locationValid) {
        islocationvalid = locationValid;
    }

    public String getApptoken() {
        return apptoken;
    }

    public void setApptoken(String appToken) {
        this.apptoken = appToken;
    }


    public String getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(String ip) {
        this.ipaddress = ip;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public double latitude, longitude, accuracy;
    public boolean finelocation, iswifi, iscellular, isvpn, isusb, isbluetooth, islocationvalid;
    public String apptoken, ipaddress;
    int flag;

    @Override
    public String toString() {
        return "POJOdata{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", accuracy=" + accuracy +
                ", fineLocation=" + finelocation +
                ", isWiFi=" + iswifi +
                ", isCellular=" + iscellular +
                ", isVPN=" + isvpn +
                ", isUsb=" + isusb +
                ", isBluetooth=" + isbluetooth +
                ", isLocationValid=" + islocationvalid +
                ", appToken='" + apptoken + '\'' +
                ", ip='" + ipaddress + '\'' +
                ", flag=" + flag +
                '}';
    }
}
