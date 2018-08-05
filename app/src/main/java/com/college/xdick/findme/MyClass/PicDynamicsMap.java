package com.college.xdick.findme.MyClass;

import com.college.xdick.findme.bean.Dynamics;

import java.io.Serializable;
import java.util.Map;

public class PicDynamicsMap implements Serializable {

    private Map<String,Dynamics> map;

    public Map<String, Dynamics> getMap() {
        return map;
    }

    public void setMap(Map<String, Dynamics> map) {
        this.map = map;
    }

}
