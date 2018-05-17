package com.college.xdick.findme.bean;

import java.util.List;

/**
 * Created by Administrator on 2018/5/8.
 */

public class City {

    String locationId;
    String departId;
    String locationName;
    List<School> collegeNames;


    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getDepartId() {
        return departId;
    }

    public void setDepartId(String departId) {
        this.departId = departId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public List<School> getCollegeNames() {
        return collegeNames;
    }

    public void setCollegeNames(List<School> collegeNames) {
        this.collegeNames = collegeNames;
    }
}
