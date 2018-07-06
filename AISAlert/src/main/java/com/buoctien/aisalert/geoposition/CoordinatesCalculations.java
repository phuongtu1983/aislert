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
        double φ1 = Math.toRadians(c1.getLatitude());
        double φ2 = Math.toRadians(c2.getLatitude());
        double Δφ = Math.toRadians(c2.getLatitude() - c1.getLatitude());
        double Δλ = Math.toRadians(c2.getLongitude() - c1.getLongitude());

        double a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2)
                + Math.cos(φ1) * Math.cos(φ2) * Math.sin(Δλ / 2) * Math.sin(Δλ / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    public static double getBearing(Coordinates startPoint, Coordinates endPoint) {
        double result = 0;
        try {
            double lat1 = Math.toRadians(startPoint.getLatitude());
            double lat2 = Math.toRadians(endPoint.getLatitude());
            double lng2_lng1 = Math.toRadians(endPoint.getLongitude() - startPoint.getLongitude());

            double y = Math.sin(lng2_lng1) * Math.cos(lat2);
            double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(lng2_lng1);
            result = Math.atan2(y, x);
            result = toDegree(result);
            result = (result + 360) % 360;
        } catch (Exception ex) {
            System.out.println("getBearing : " + ex);
        }
        return result;
    }

    public static Coordinates getNextPoint(Coordinates point, double bearing, double distance) {
        Coordinates result = null;
        try {
            double δ = distance / EARTH_RADIUS;
            double θ = Math.toRadians(bearing);
            double φ1 = Math.toRadians(point.getLatitude());
            double λ1 = Math.toRadians(point.getLongitude());

            double sinφ1 = Math.sin(φ1), cosφ1 = Math.cos(φ1);
            double sinδ = Math.sin(δ), cosδ = Math.cos(δ);
            double sinθ = Math.sin(θ), cosθ = Math.cos(θ);

            double sinφ2 = sinφ1 * cosδ + cosφ1 * sinδ * cosθ;
            double φ2 = Math.asin(sinφ2);
            double y = sinθ * sinδ * cosφ1;
            double x = cosδ - sinφ1 * sinφ2;
            double λ2 = λ1 + Math.atan2(y, x);
            result = new Coordinates(toDegree(φ2), (toDegree(λ2) + 540) % 360 - 180);
        } catch (Exception ex) {
            System.out.println("getNextPoint : " + ex);
        }
        return result;
    }

    private static double toDegree(double number) {
        return number * 180 / Math.PI;
    }
}
