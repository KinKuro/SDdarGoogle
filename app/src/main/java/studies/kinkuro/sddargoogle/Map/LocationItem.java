package studies.kinkuro.sddargoogle.Map;

/**
 * Created by alfo6-2 on 2018-05-17.
 */

public class LocationItem {

    String address;
    int contentId;
    String district;
    double latitude;
    double longitude;
    String name;
    int numHolder;

    public LocationItem(String address, int contentId, String district, double latitude, double longitude, String name, int numHolder) {
        this.address = address;
        this.contentId = contentId;
        this.district = district;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.numHolder = numHolder;
    }

    public String getAddress() {        return address;    }
    public void setAddress(String address) {        this.address = address;    }

    public int getContentId() {        return contentId;    }
    public void setContentId(int contentId) {        this.contentId = contentId;    }

    public String getDistrict() {        return district;    }
    public void setDistrict(String district) {        this.district = district;    }

    public double getLatitude() {        return latitude;    }
    public void setLatitude(double latitude) {        this.latitude = latitude;    }

    public double getLongitude() {        return longitude;    }
    public void setLongitude(double longitude) {        this.longitude = longitude;    }

    public String getName() {        return name;    }
    public void setName(String name) {        this.name = name;    }

    public int getNumHolder() {        return numHolder;    }
    public void setNumHolder(int numHolder) {        this.numHolder = numHolder;    }

}
