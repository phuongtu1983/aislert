/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.buoctien.aisalert.bean;

import com.buoctien.aisalert.geoposition.Coordinates;
import dk.dma.enav.model.geometry.Position;

/**
 *
 * @author DELL
 */
public class AISBean {

    private String MMSI;
    private int navStatus;
    private Position position;
    private Coordinates simulatePosition;
    private int shipType;
    private double distance;
    private int navigation;
    private int navigationImage;
    private String name;
    private String alertArea;
    private long milisec;
    private long sog;

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

    public boolean setAlertArea(String alertArea) {
        boolean result = true;
        if (alertArea.equals(this.alertArea)) {
            result = false;
        }
        this.alertArea = alertArea;
        return result;
    }

    public void setMMSI(String MMSI) {
        this.MMSI = MMSI;
    }

    public AISBean() {
        this.MMSI = "";
        this.name = "";
        this.navStatus = -1;
        this.position = null;
        this.shipType = -1;
        this.navigation = 0;
        this.navigationImage = 0;
        this.alertArea = "";
        this.distance = 0;
        this.milisec = 0;
        this.sog = 0;
    }

    public AISBean(String MMSI, int navStatus, Position position, int shipType, String alertArea, double distance, long milisec, int navigationImage, long sog) {
        this.MMSI = MMSI;
        this.name = "";
        this.navStatus = navStatus;
        this.position = position;
        this.shipType = shipType;
        this.navigation = 0;
        this.navigationImage = navigationImage;
        this.alertArea = alertArea;
        this.distance = distance;
        this.milisec = milisec;
        this.sog = sog;
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
        this.simulatePosition = null;
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

    public long getMilisec() {
        return milisec;
    }

    public void setMilisec(long milisec) {
        this.milisec = milisec;
    }

    public int getNavigationImage() {
        return navigationImage;
    }

    public void setNavigationImage(int navigationImage) {
        this.navigationImage = navigationImage;
    }

    public long getSog() {
        return sog;
    }

    public void setSog(long sog) {
        this.sog = sog;
    }

    public Coordinates getSimulatePosition() {
        return simulatePosition;
    }

    public void setSimulatePosition(Coordinates simulatePosition) {
        this.simulatePosition = simulatePosition;
    }

    public static final String RED_ALERT = "RED";
    public static final String YELLOW_ALERT = "YELLOW";

}
