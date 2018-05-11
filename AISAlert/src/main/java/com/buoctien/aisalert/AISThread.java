/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.buoctien.aisalert;

import com.buoctien.aisalert.geoposition.Coordinates;
import com.buoctien.aisalert.geoposition.CoordinatesCalculations;
import com.buoctien.aisalert.bean.AISBean;
import com.buoctien.aisalert.bean.StaticBean;
import com.buoctien.aisalert.util.AISUtil;
import com.buoctien.aisalert.util.ArduinoUtil;
import com.buoctien.aisalert.util.FileUtil;
import dk.dma.ais.filter.DownSampleFilter;
import dk.dma.ais.filter.DuplicateFilter;
import dk.dma.ais.filter.MessageHandlerFilter;
import dk.dma.ais.message.AisMessage;
import dk.dma.ais.message.AisMessage1;
import dk.dma.ais.message.AisMessage12;
import dk.dma.ais.message.AisMessage18;
import dk.dma.ais.message.AisPosition;
import dk.dma.ais.message.AisPositionMessage;
import dk.dma.ais.message.AisStaticCommon;
import dk.dma.ais.message.IPositionMessage;
import dk.dma.ais.message.UTCDateResponseMessage;
import dk.dma.ais.reader.AisReader;
import dk.dma.enav.model.geometry.Position;
import gnu.io.SerialPort;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 *
 * @author DELL
 */
public class AISThread extends Thread {

    private final SerialPort dataPort;
    private final String fileName;
    private final String writtenFileName;
    private boolean stoped;
    private String currentAlert = ""; // ''=off; 'RED'=bao dong do; 'YELLOW'=bao dong vang

    private ArrayList emulatorAISData = new ArrayList();

    public AISThread(String fileName, String writtenFileName, SerialPort dataPort) {
        this.fileName = fileName;
        this.writtenFileName = writtenFileName;
        this.dataPort = dataPort;
        this.stoped = false;
    }

    @Override
    public void run() {
        try {
            System.out.println("Thread ID: " + this.getId());
//            Properties props = ConfigUtil.readConfig(fileName);
//            runFromFile2();
//            runFromSerialPort();
            createEmulatorData();
            handleEmulateData();
//            this.stoped = true;
        } catch (Exception ex) {
            System.out.println("run : " + ex);
        }
    }

    public boolean isStoped() {
        return stoped;
    }

    private void runFromSerialPort() {
        try {
            AisReader reader = AISUtil.readFromSerialPort(dataPort);
            if (reader != null) {
                Consumer<AisMessage> handler = new Consumer<AisMessage>() {
                    @Override
                    public void accept(AisMessage aisMessage) {
                        aisMessageHandle(aisMessage);
                    }
                };
                // Make down sampling filter with sampling rate 30 sec and
                // make handler the recipient of down sampled messages
                MessageHandlerFilter downsampleFilter = new MessageHandlerFilter(new DownSampleFilter(30));
                downsampleFilter.registerReceiver(handler);

                // Make doublet filter with default window of 10 secs.
                // Set down sample filter as recipient of doublet filered messages
                MessageHandlerFilter doubletFilter = new MessageHandlerFilter(new DuplicateFilter(10));
                doubletFilter.registerReceiver(downsampleFilter);

                reader.registerHandler(doubletFilter);
                reader.start();
                reader.join();
            }
        } catch (Exception ex) {
            System.out.println("runFromSerialPort : " + ex);
        }
    }

    private void aisMessageHandle(AisMessage aisMessage) {
        try {
            AISBean aisBean = acceptAisMessage(aisMessage);
            String key = String.valueOf(aisBean.getMMSI());
            if (aisBean.getPosition() != null) {
                boolean result = checkWithinArea200(aisBean.getPosition());
                if (result) { // nam trong khu vuc 200m
                    System.out.println("Within 200");
                    AISBean oldBean = AISObjectList.get(key);
                    double distance = getDistance(aisBean.getPosition());
                    if (oldBean != null) {
                        oldBean.setPosition(aisBean.getPosition());
                        oldBean.setNavigation(distance < oldBean.getDistance() ? -1 : 1);
                        oldBean.setDistance(distance);
                        oldBean.setAlertArea(AISBean.RED_ALERT);
                    } else {
                        AISObjectList.addObject(new AISBean(aisMessage.getUserId() + "", aisBean.getNavStatus(),
                                aisBean.getPosition(), aisBean.getShipType(), AISBean.RED_ALERT, distance));
                    }
                } else {// khong nam trong khu vuc 200m
                    result = checkWithinArea500(aisBean.getPosition());
                    if (result) { // nam trong khu vuc tu 200m den 500m
                        System.out.println("Within 200 - 500");
                        AISBean oldBean = AISObjectList.get(key);
                        double distance = getDistance(aisBean.getPosition());
                        if (oldBean != null) {
                            oldBean.setPosition(aisBean.getPosition());
                            oldBean.setNavigation(distance < oldBean.getDistance() ? -1 : 1);
                            oldBean.setDistance(distance);
                            if (oldBean.getNavigation() > 0) {
                                oldBean.setAlertArea("");
                            } else {
                                oldBean.setAlertArea(AISBean.YELLOW_ALERT);
                            }
                        } else {
                            AISObjectList.addObject(new AISBean(aisMessage.getUserId() + "", aisBean.getNavStatus(),
                                    aisBean.getPosition(), aisBean.getShipType(), AISBean.YELLOW_ALERT, distance));
                        }
                    } else { // nam ngoai khu vuc 500m
                        result = checkOutsideArea(aisBean.getPosition());
                        if (result) { // nam ngoai khu vuc hien thi
                            AISBean oldBean = AISObjectList.get(key);
                            if (oldBean != null) {
                                AISObjectList.removeObject(oldBean);
                            }
                        } else {// nam trong khu vuc hien thi (500 - 1000)
                            AISBean oldBean = AISObjectList.get(key);
                            double distance = getDistance(aisBean.getPosition());
                            if (oldBean != null) {
                                oldBean.setPosition(aisBean.getPosition());
                                oldBean.setNavigation(distance < oldBean.getDistance() ? -1 : 1);
                                oldBean.setDistance(distance);
                                oldBean.setAlertArea("");
                            } else {
                                AISObjectList.addObject(new AISBean(aisMessage.getUserId() + "", aisBean.getNavStatus(),
                                        aisBean.getPosition(), aisBean.getShipType(), "", distance));
                            }
                        }
                    }
                }
                turnAlert();
            }
        } catch (Exception ex) {
            System.out.println("aisMessageHandle : " + ex);
        }
    }

    private void turnAlert() {
        try {
            String alertArea = AISObjectList.getAlert();
            System.out.println("alertArea: " + alertArea);
            System.out.println("currentAlert: " + currentAlert);
            if (alertArea.equals(AISBean.RED_ALERT) && !currentAlert.equals(AISBean.RED_ALERT)) {
                // call Uno_Red
                currentAlert = alertArea;
                ArduinoUtil.redAlert();
            } else if (alertArea.equals(AISBean.YELLOW_ALERT) && !currentAlert.equals(AISBean.YELLOW_ALERT)) {
                // call Uno_Yellow
                currentAlert = alertArea;
                ArduinoUtil.yellowAlert();
            } else if (!alertArea.equals("") && !currentAlert.equals("")) {
                // turn off alert
                currentAlert = "";
                ArduinoUtil.turnOffAlert();
            }
            System.out.println("currentAlert 2: " + currentAlert);
        } catch (Exception ex) {
            System.out.println("turnAlert : " + ex);
        }
    }

    private AISBean acceptAisMessage(AisMessage aisMessage) {
        AISBean bean = new AISBean();
        bean.setMMSI(aisMessage.getUserId() + "");
        try {
            Position pos = aisMessage.getValidPosition();
//            String lat = "";
//            String lon = "";
            if (pos != null) {
//                lat = pos.getLatitude() + "; " + pos.getLatitudeAsString();
//                lon = pos.getLongitude() + "; " + pos.getLongitudeAsString();
                bean.setPosition(pos);
            }
//            str += ", userId: " + aisMessage.getUserId()
//                    + ", lat: " + lat
//                    + ", lon: " + lon
//                    + ", pos: " + pos
//                    + ", str: " + getStrToWrite(aisMessage, bean)
//                    + ", raw: " + aisMessage.toString()
//                    + ", time: " + System.currentTimeMillis()
//                    + ", class: " + aisMessage.getClass();
//            System.out.println(str);
//            FileUtil.writeToFile(writtenFileName, str);
//            FileUtil.writeToFile(writtenFileName, aisMessage.reassemble());
        } catch (Exception ex) {
            System.out.println("acceptAisMessage : " + ex);
        }
        return bean;
    }

    private String getStrToWrite(AisMessage aisMessage, AISBean aisBean) {
        String result = "";
        try {
            if (aisMessage instanceof AisPositionMessage) {
                // 1, 2, 3
                AisPositionMessage mes = (AisPositionMessage) aisMessage;
                result += ", navStatus: " + mes.getNavStatus();
                result += ", posMes1: " + mes.getPos();
                aisBean.setNavStatus(mes.getNavStatus());
            } else if (aisMessage instanceof AisStaticCommon) {
                // 5, 19, 24
                AisStaticCommon mes = (AisStaticCommon) aisMessage;
                result += ", name: " + mes.getName();
                result += ", shipType: " + mes.getShipType();
                aisBean.setShipType(mes.getShipType());
                aisBean.setName(mes.getName());
            } else if (aisMessage instanceof AisMessage18) {
                // 18
                AisMessage18 mes = (AisMessage18) aisMessage;
                result += ", posMes18: " + mes.getPos();
            } else if (aisMessage instanceof UTCDateResponseMessage) {
                // 4, 11
                UTCDateResponseMessage mes = (UTCDateResponseMessage) aisMessage;
                result += ", posMes4: " + mes.getPos();
            } else if (aisMessage instanceof IPositionMessage) {
                // 9, 21, 27
                IPositionMessage mes = (IPositionMessage) aisMessage;
                result += ", posMes9: " + mes.getPos();
            } else if (aisMessage instanceof AisMessage12) {
                // 6, 7, 8, 10, 12, 13, 14, 17
            }
        } catch (Exception ex) {
            System.out.println("getStrToWrite : " + ex);
        }
        return result;
    }

    private boolean checkOutsideArea(dk.dma.enav.model.geometry.Position position) {
        try {
            if (CoordinatesCalculations.getDistanceBetweenTwoPoints(new Coordinates(position.getLatitude(), position.getLongitude()), new Coordinates(StaticBean.MidPointLatitude, StaticBean.MidPointLongtitude)) > StaticBean.OutsideRadius) {
                return true;
            }
        } catch (Exception ex) {
            System.out.println("checkWithinArea1000 : " + ex);
        }
        return false;
    }

    private boolean checkWithinArea500(dk.dma.enav.model.geometry.Position position) {
        try {
            Coordinates boatPosition = new Coordinates(position.getLatitude(), position.getLongitude());
            Coordinates center = null;
            for (int i = 0; i < StaticBean.YellowPointQuantity; i++) {
                center = StaticBean.ArrayCenterPoint.get(i);
                if (CoordinatesCalculations.isInCircleArea(boatPosition, center, StaticBean.YellowRadius)) {
                    return true;
                }
            }
        } catch (Exception ex) {
            System.out.println("checkWithinArea500 : " + ex);
        }
        return false;
    }

    private boolean checkWithinArea200(dk.dma.enav.model.geometry.Position position) {
        try {
            Coordinates boatPosition = new Coordinates(position.getLatitude(), position.getLongitude());
            Coordinates center = null;
            for (int i = 0; i < StaticBean.ArrayCenterPoint.size(); i++) {
                center = StaticBean.ArrayCenterPoint.get(i);
                if (CoordinatesCalculations.isInCircleArea(boatPosition, center, StaticBean.RedRadius)) {
                    return true;
                }
            }
        } catch (Exception ex) {
            System.out.println("checkWithinArea200 : " + ex);
        }
        return false;
    }

    private double getDistance(dk.dma.enav.model.geometry.Position newPost) {
        return CoordinatesCalculations.getDistanceBetweenTwoPoints(
                new Coordinates(StaticBean.MidPointLatitude, StaticBean.MidPointLongtitude),
                new Coordinates(newPost.getLatitude(), newPost.getLongitude()));
    }

    private void createEmulatorData() {
        createMessage(10.657903, 106.805794);
        createMessage(10.658346, 106.8047);
        createMessage(10.658788, 106.803756);
        createMessage(10.658978, 106.803198);
        createMessage(10.659337, 106.802597);
        createMessage(10.659526, 106.802018);
        createMessage(10.659758, 106.801417);
        createMessage(10.660307, 106.800473);
        createMessage(10.660297, 106.800344);
        createMessage(10.660402, 106.80013);
        createMessage(10.660476, 106.799937);
        createMessage(10.660603, 106.799765);
        createMessage(10.660697, 106.79954);
        createMessage(10.660792, 106.799325);
        createMessage(10.660898, 106.799143);
        createMessage(10.661024, 106.798982);
        createMessage(10.661151, 106.798724);
        createMessage(10.661277, 106.798456);
        createMessage(10.661425, 106.798242);
        createMessage(10.661573, 106.798059);
        createMessage(10.661731, 106.797866);
        createMessage(10.661826, 106.797705);
        createMessage(10.661952, 106.797512);
        createMessage(10.662131, 106.797351);
        createMessage(10.662216, 106.797147);
        createMessage(10.662353, 106.796997);
        createMessage(10.662500, 106.796804);
        createMessage(10.662680, 106.796664);
        createMessage(10.662901, 106.796396);
        createMessage(10.663112, 106.796246);
        createMessage(10.663486, 106.795822);
        createMessage(10.663876, 106.795415);
        createMessage(10.664193, 106.795136);
        createMessage(10.664488, 106.794824);
        createMessage(10.664720, 106.794545);
        createMessage(10.664904, 106.794356);
        createMessage(10.665178, 106.794088);
        createMessage(10.665336, 106.793906);
        createMessage(10.665568, 106.79368);
        createMessage(10.665737, 106.793487);
        createMessage(10.666053, 106.79308);
        createMessage(10.666264, 106.792726);
        createMessage(10.666443, 106.792447);
        createMessage(10.666686, 106.792082);
        createMessage(10.666928, 106.791803);
        createMessage(10.667076, 106.79147);
        createMessage(10.667656, 106.790644);
        createMessage(10.668183, 106.789936);
        createMessage(10.668583, 106.789335);
        createMessage(10.669005, 106.788756);
        createMessage(10.669321, 106.78809);
        createMessage(10.669827, 106.787146);
        createMessage(10.670323, 106.786224);
        createMessage(10.670628, 106.785344);
    }

    private void createMessage(double lat, double lon) {
        AisMessage1 aisMessage = null;
        AisPosition pos = null;
        aisMessage = new AisMessage1();
        aisMessage.setUserId(123456789);
        pos = new AisPosition();
        pos.setLatitude(Math.round(lat * 10000.0 * 60.0));
        pos.setLongitude(Math.round(lon * 10000.0 * 60.0));
        aisMessage.setPos(pos);
        emulatorAISData.add(aisMessage);
    }

    private void handleEmulateData() {
        for (int i = 0; i < emulatorAISData.size(); i++) {
            AisMessage aisMessage = (AisMessage) emulatorAISData.get(i);
            aisMessageHandle(aisMessage);
            try {
                Thread.sleep(6000);
            } catch (Exception ex) {

            }
        }
        this.stoped = true;
    }
}
