package com.college.xdick.findme.bean;

import java.util.List;

/**
 * Created by Administrator on 2018/5/7.
 */

public class Province {
    String departId;
    String departName;
    List<City> collegeLocations;

    public String getDepartId() {
        return departId;
    }

    public void setDepartId(String departId) {
        this.departId = departId;
    }

    public String getDepartName() {
        return departName;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
    }

    public List<City> getCollegeLocations() {
        return collegeLocations;
    }

    public void setCollegeLocations(List<City> collegeLocations) {
        this.collegeLocations = collegeLocations;
    }
}