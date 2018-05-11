/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.buoctien.aisalert.bean;

import dk.dma.enav.model.geometry.Position;

/**
 *
 * @author DELL
 */
public class AISBean {

    private String MMSI;
    private int navStatus;
    private Position position;
    private int shipType;
    private double distance;
    private int navigation;
    private String name;
    private String alertArea;

    public String getMMSI() {
        return MMSI;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlertArea() {
        return alertArea;
    }

    public void setAlertArea(String alertArea) {
        this.alertArea = alertArea;
    }

    public void setMMSI(String MMSI) {
        this.MMSI = MMSI;
    }

    public AISBean() {
        this.MMSI = "";
        this.navStatus = -1;
        this.position = null;
        this.shipType = -1;
        this.navigation = 0;
        this.distance = 0;
        this.alertArea = "";
    }

    public AISBean(String MMSI, int navStatus, Position position, int shipType, String alertArea, double distance) {
        this.MMSI = MMSI;
        this.navStatus = navStatus;
        this.position = position;
        this.shipType = shipType;
        this.navigation = 0;
        this.alertArea = alertArea;
        this.distance = distance;
        this.navigation = 0;
    }

    public int getNavStatus() {
        return navStatus;
    }

    public void setNavStatus(int navStatus) {
        this.navStatus = navStatus;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getShipType() {
        return shipType;
    }

    public void setShipType(int shipType) {
        this.shipType = shipType;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getNavigation() {
        return navigation;
    }

    public void setNavigation(int navigation) {
        this.navigation = navigation;
    }

    public static final String RED_ALERT = "RED";
    public static final String YELLOW_ALERT = "YELLOW";

}