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
import com.buoctien.aisalert.util.ArduinoUtil;
import com.buoctien.aisalert.util.GeographicUtil;
import com.buoctien.aisalert.util.SerialUtil;
import com.buoctien.aisalert.util.TimerUtil;
import dk.dma.enav.model.geometry.Position;
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
            return;
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
            long diffSec = 0;
            int autoTime = StaticBean.AUTO_TIME;
            double distance = 0;
            Coordinates centerPoint = new Coordinates(StaticBean.AutoMidPointLatitude, StaticBean.AutoMidPointLongtitude);
            for (int i = 0; i < list.size(); i++) {
                obj = (AISBean) list.get(i);
                diffSec = (currentMilisec - obj.getMilisec()) / 1000;
                if (diffSec > autoTime) {
                    distance = getDistance(obj.getSimulatePosition() == null ? new Coordinates(obj.getPosition().getLatitude(), obj.getPosition().getLongitude()) : obj.getSimulatePosition());
                    obj.setDistance(distance);
                    coor = new Coordinates(obj.getPosition().getLatitude(), obj.getPosition().getLongitude());
                    double bearing = GeographicUtil.getBearing(coor, centerPoint);
                    Coordinates nextPoint = GeographicUtil.getNextPoint(coor, bearing, distance - StaticBean.KNOT * diffSec);
                    if (nextPoint != null) {
                        obj.setSimulatePosition(nextPoint);
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("createAutoRoute : " + ex);
        }
    }

    private double getDistance(Coordinates newPost) {
        return CoordinatesCalculations.getDistanceBetweenTwoPoints(
                new Coordinates(StaticBean.AutoMidPointLatitude, StaticBean.AutoMidPointLongtitude), newPost);
    }

}
