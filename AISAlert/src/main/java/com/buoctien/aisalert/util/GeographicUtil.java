/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.buoctien.aisalert.util;

import com.buoctien.aisalert.geoposition.Coordinates;

/**
 *
 * @author DELL
 */
public class GeographicUtil {

    public static final long EARTH_RADIUS = 6371000;

    public static double getBearing(Coordinates startPoint, Coordinates endPoint) {
        double result = 0;
        try {
            double y = Math.sin(endPoint.getLongitude() - startPoint.getLongitude()) * Math.cos(endPoint.getLatitude());
            double x = Math.cos(startPoint.getLatitude()) * Math.sin(endPoint.getLatitude())
                    - Math.sin(startPoint.getLatitude()) * Math.cos(endPoint.getLatitude()) * Math.cos(endPoint.getLongitude() - startPoint.getLongitude());
            result = Math.atan2(y, x);
        } catch (Exception ex) {
            System.out.println("getBearing : " + ex);
        }
        return result;
    }

    public static Coordinates getNextPoint(Coordinates point, double bearing, double distance) {
        Coordinates result = null;
        try {
            double d_r = distance / EARTH_RADIUS;
            double lat = Math.asin(Math.sin(point.getLatitude()) * Math.cos(d_r)
                    + Math.cos(point.getLatitude()) * Math.sin(d_r) * Math.cos(bearing));
            double lng = point.getLongitude()
                    + Math.atan2(Math.cos(d_r) - Math.sin(point.getLatitude() * Math.sin(lat)),
                            Math.sin(bearing) * Math.sin(d_r) * Math.cos(point.getLatitude()));
            result = new Coordinates(lat, lng);
        } catch (Exception ex) {
            System.out.println("getNextPoint : " + ex);
        }
        return result;
    }
}
