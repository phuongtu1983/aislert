/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.buoctien.aisalert.geoposition;

/**
 *
 * @author DELL
 */
public class CoordinatesCalculations {

    private static final long EARTH_RADIUS = 6371000; // m

    public static boolean isInCircleArea(Coordinates coordinate, Coordinates center, int radius) {
        boolean res = false;
        if (getDistanceBetweenTwoPoints(coordinate, center) <= radius) {
            res = true;
        }
        return res;
    }

    public static double getDistanceBetweenTwoPoints(Coordinates c1, Coordinates c2) {
        double dLat = Math.toRadians(c2.getLatitude() - c1.getLatitude());
        double dLon = Math.toRadians(c2.getLongitude() - c1.getLongitude());
        double lat1 = Math.toRadians(c1.getLatitude());
        double lat2 = Math.toRadians(c2.getLatitude());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = EARTH_RADIUS * c;

        return d;
    }

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
