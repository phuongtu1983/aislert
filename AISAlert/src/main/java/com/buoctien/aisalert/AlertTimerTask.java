/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.buoctien.aisalert;

import com.buoctien.aisalert.bean.AISBean;
import com.buoctien.aisalert.bean.AlertBean;
import com.buoctien.aisalert.bean.StaticBean;
import com.buoctien.aisalert.geoposition.Coordinates;
import com.buoctien.aisalert.geoposition.CoordinatesCalculations;
import com.buoctien.aisalert.util.AISUtil;
import com.buoctien.aisalert.util.ArduinoUtil;
import com.buoctien.aisalert.util.SerialUtil;
import com.buoctien.aisalert.util.TimerUtil;
import gnu.io.SerialPort;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimerTask;

/**
 *
 * @author DELL
 */
public class AlertTimerTask extends TimerTask {

    private SerialPort alertDataPort = null;
    private final String configFileName;
    private boolean scheduled;

    public AlertTimerTask(String configFileName) {
        this.configFileName = configFileName;
        this.scheduled = false;
        if (this.alertDataPort == null) {
            this.alertDataPort = initAlertPort();
        }
    }

    public synchronized void schedule(long delay, long period) {
        if (!scheduled) {
            scheduled = true;
            TimerUtil.getInstance().schedule(this, delay, period);
        }
    }

    @Override
    public boolean cancel() {
        if (alertDataPort != null) {
            ArduinoUtil util = new ArduinoUtil(alertDataPort);
            util.turnAlert("", 0);
            alertDataPort.close();
            alertDataPort = null;
        }
        return super.cancel();
    }

    @Override
    public void run() {
        if (alertDataPort == null) {
            this.alertDataPort = initAlertPort();
        }
        if (alertDataPort == null) {
//            return;
        }

        if (StaticBean.IS_AUTO == 1) {
            createAutoRoute();
        }

        AlertBean alert = AISObjectList.getAlert();
        if (alert != null) {
            ArduinoUtil util = new ArduinoUtil(alertDataPort);
            util.turnAlert(alert.getAlertArea(), alert.getSoundType());
        }
    }

    private SerialPort initAlertPort() {
        return SerialUtil.initAlertPort(configFileName, "wireless_port", "wireless_baudrate");
    }

    private void createAutoRoute() {
        try {
            ArrayList list = AISObjectList.getList();
            AISBean obj = null;
            Coordinates coor = null;
            long currentMilisec = new Date().getTime();
            int autoTime = StaticBean.AUTO_TIME;
            double bearing = 0;
            Coordinates centerPoint = new Coordinates(StaticBean.MidPointLatitude, StaticBean.MidPointLongtitude);
            Coordinates autoCenterPoint = new Coordinates(StaticBean.AutoMidPointLatitude, StaticBean.AutoMidPointLongtitude);
            Coordinates leftCenterPoint = new Coordinates(StaticBean.AutoLeftPointLatitude, StaticBean.AutoLeftPointLongtitude);
            Coordinates rightCenterPoint = new Coordinates(StaticBean.AutoRightPointLatitude, StaticBean.AutoRightPointLongtitude);
            Coordinates nextCenterPoint = null;
            for (int i = 0; i < list.size(); i++) {
                obj = (AISBean) list.get(i);
                if (obj.getShouldSimulated() == 0) {
                    continue;
                }
                if ((currentMilisec - obj.getMilisec()) / 1000 > autoTime) {
                    if (obj.getSimulatedPosition() == null) {
                        coor = new Coordinates(obj.getPosition().getLatitude(), obj.getPosition().getLongitude());
                    } else {
                        coor = new Coordinates(obj.getSimulatedPosition().getLatitude(), obj.getSimulatedPosition().getLongitude());
                    }
                    if (obj.getNavigationImage() == 0) {
                        nextCenterPoint = autoCenterPoint;
                    } else if (obj.getNavigationImage() > 0) {// from right to left
//                        if (coor.getLongitude() < StaticBean.MidPointLongtitude) { // left side
                            nextCenterPoint = leftCenterPoint;
//                        } else { // right side
//                            nextCenterPoint = autoCenterPoint;
//                        }
                    } else { // from left to right
//                        if (coor.getLongitude() < StaticBean.MidPointLongtitude) { // left side
//                            nextCenterPoint = autoCenterPoint;
//                        } else { // right side
                            nextCenterPoint = rightCenterPoint;
//                        }
                    }
                    bearing = CoordinatesCalculations.getBearing(coor, nextCenterPoint);
                    Coordinates nextPoint = CoordinatesCalculations.getNextPoint(coor, bearing, obj.getSog() * StaticBean.KNOT * (currentMilisec - obj.getSimulatedMilisec()) / 1000);
                    if (nextPoint != null) {
                        obj.setDistance(getDistance(coor, centerPoint));
                        obj.setSimulatedPosition(nextPoint);
                        AISUtil.hanldeAisMessage(obj.getMMSI(), obj, true);
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("createAutoRoute : " + ex);
        }
    }

    private double getDistance(Coordinates newPost, Coordinates centerPoint) {
        return CoordinatesCalculations.getDistanceBetweenTwoPoints(centerPoint, newPost);
    }

}
