/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.buoctien.aisalert.util;

import com.buoctien.aisalert.AISObjectList;
import com.buoctien.aisalert.bean.AISBean;
import com.buoctien.aisalert.bean.StaticBean;
import com.buoctien.aisalert.geoposition.Coordinates;
import com.buoctien.aisalert.geoposition.CoordinatesCalculations;
import dk.dma.ais.message.AisMessage;
import dk.dma.ais.message.AisMessage12;
import dk.dma.ais.message.AisMessage18;
import dk.dma.ais.message.AisPositionMessage;
import dk.dma.ais.message.AisStaticCommon;
import dk.dma.ais.message.IPositionMessage;
import dk.dma.ais.message.IVesselPositionMessage;
import dk.dma.ais.message.UTCDateResponseMessage;
import dk.dma.ais.reader.AisReader;
import dk.dma.ais.reader.AisReaders;
import dk.dma.enav.model.geometry.Position;
import gnu.io.SerialPort;
import java.io.FileInputStream;
import java.util.Date;

/**
 *
 * @author DELL
 */
public class AISUtil {

    public static AisReader readFromFile(String fileName) {
        try {
            AisReader reader = AisReaders.createReaderFromInputStream(new FileInputStream(fileName));
            return reader;
        } catch (Exception ex) {
            System.out.println("readFromFile: " + ex.toString());
        }
        return null;
    }

    public synchronized static AisReader readFromSerialPort(SerialPort port) {
        try {
            if (port != null) {
                AisReader reader = AisReaders.createReaderFromInputStream(port.getInputStream());
                return reader;
            }
        } catch (Exception ex) {
            System.out.println("readFromSerialPort: " + ex.toString());
        }
        return null;
    }

    public static AISBean acceptAisMessage(AisMessage aisMessage, boolean isSimulated) {
        AISBean bean = new AISBean();
        bean.setMMSI(aisMessage.getUserId() + "");
        try {
            Position pos = aisMessage.getValidPosition();
            if (pos != null) {
                bean.setPosition(new Coordinates(pos.getLatitude(), pos.getLongitude()));
            }
            getAisBean(aisMessage, bean);
            String key = String.valueOf(bean.getMMSI());
            AISBean oldBean = AISObjectList.get(key);
            if (oldBean != null && oldBean.getSimulatedPosition() != null) {
                bean.setNavigationImage(oldBean.getNavigationImage());
                if (bean.getSog() == 0) {
                    bean.setSog(oldBean.getSog());
                }
                AISObjectList.removeObject(oldBean);
            }
            hanldeAisMessage(aisMessage.getUserId() + "", bean, isSimulated);
        } catch (Exception ex) {
            System.out.println("acceptAisMessage : " + ex);
        }
        return bean;
    }

    private static void getAisBean(AisMessage aisMessage, AISBean aisBean) {
        try {
            if (aisMessage instanceof AisPositionMessage) {
                // 1, 2, 3
                AisPositionMessage mes = (AisPositionMessage) aisMessage;
                aisBean.setNavStatus(mes.getNavStatus());
            } else if (aisMessage instanceof AisStaticCommon) {
                // 5, 19, 24
                AisStaticCommon mes = (AisStaticCommon) aisMessage;
                aisBean.setShipType(mes.getShipType());
                aisBean.setName(mes.getName());
            } else if (aisMessage instanceof AisMessage18) {
                // 18
//                AisMessage18 mes = (AisMessage18) aisMessage;
            } else if (aisMessage instanceof UTCDateResponseMessage) {
                // 4, 11
//                UTCDateResponseMessage mes = (UTCDateResponseMessage) aisMessage;
            } else if (aisMessage instanceof IPositionMessage) {
                // 9, 21, 27
//                IPositionMessage mes = (IPositionMessage) aisMessage;
            } else if (aisMessage instanceof AisMessage12) {
                // 6, 7, 8, 10, 12, 13, 14, 17
            }
            if (aisMessage instanceof IVesselPositionMessage) {
                IVesselPositionMessage mes = (IVesselPositionMessage) aisMessage;
//                if (mes.isSogValid()) {
                aisBean.setSog(mes.getSog());
//                }
            }
        } catch (Exception ex) {
            System.out.println("getAisBean : " + ex);
        }
    }

    public static synchronized void hanldeAisMessage(String userId, AISBean aisBean, boolean isSimulated) {
        try {
            String key = String.valueOf(aisBean.getMMSI());
            AISBean oldBean = AISObjectList.get(key);
            Coordinates position = null;
            if (oldBean != null) {
                if (!isSimulated) {
                    oldBean.setMilisec(new Date().getTime());
                }
                oldBean.setSimulatedMilisec(new Date().getTime());
            }
            if (!isSimulated) {
                position = aisBean.getPosition();
            } else {
                position = aisBean.getSimulatedPosition();
            }
            if (position != null) {
                boolean result = checkOutsideArea(position);
                if (result) { // nam ngoai khu vuc hien thi
                    if (oldBean != null) {
                        AISObjectList.removeObject(oldBean);
                    }
                    return;
                }
                result = checkWithinArea(position, StaticBean.RedSmallRadius);
                if (result) { // nam trong khu vuc 200m
                    double distance = getDistance(position);
                    if (oldBean != null) {
                        if (!isSimulated) {
                            oldBean.setPosition(position);
                        } else {
                            oldBean.setSimulatedPosition(position);
                        }
                        if (aisBean.getSog() != 0) {
                            oldBean.setSog(aisBean.getSog());
                        }
                        oldBean.setNavigation(distance < oldBean.getDistance() ? -1 : 1);
                        oldBean.setDistance(distance);
                        oldBean.setAlertArea(AISBean.RED_ALERT);
                        oldBean.setShouldSimulated(1);
                        if (oldBean.getNavigationImage() == 0) {
                            oldBean.setNavigationImage(position.getLongitude() < StaticBean.MidPointLongtitude ? -1 : 1);
                        }
                    } else {
                        AISObjectList.addObject(new AISBean(userId, aisBean.getNavStatus(),
                                position, aisBean.getShipType(), AISBean.RED_ALERT, distance,
                                new Date().getTime(),
                                aisBean.getNavigationImage() == 0 ? (position.getLongitude() < StaticBean.MidPointLongtitude ? -1 : 1) : aisBean.getNavigationImage(),
                                aisBean.getSog()));
                    }
                } else {// khong nam trong khu vuc 200m
                    result = checkWithinArea(position, StaticBean.RedRadius);
                    if (result) { // nam trong khu vuc tu 200m den 300m
                        double distance = getDistance(position);
                        if (oldBean != null) {
                            if (!isSimulated) {
                                oldBean.setPosition(position);
                            } else {
                                oldBean.setSimulatedPosition(position);
                            }
                            if (aisBean.getSog() != 0) {
                                oldBean.setSog(aisBean.getSog());
                            }
                            oldBean.setNavigation(distance < oldBean.getDistance() ? -1 : 1);
                            oldBean.setDistance(distance);
                            if (oldBean.getNavigation() > 0) {
                                oldBean.setAlertArea("");
                                oldBean.setShouldSimulated(0);
                            } else {
                                oldBean.setAlertArea(AISBean.RED_ALERT);
                                oldBean.setShouldSimulated(1);
                            }
                            if (oldBean.getNavigationImage() == 0) {
                                oldBean.setNavigationImage(position.getLongitude() < StaticBean.MidPointLongtitude ? -1 : 1);
                            }
                        } else {
                            AISObjectList.addObject(new AISBean(userId, aisBean.getNavStatus(),
                                    position, aisBean.getShipType(), AISBean.RED_ALERT, distance,
                                    new Date().getTime(),
                                    aisBean.getNavigationImage() == 0 ? (position.getLongitude() < StaticBean.MidPointLongtitude ? -1 : 1) : aisBean.getNavigationImage(),
                                    aisBean.getSog()));
                        }
                    } else {// nam ngoai khu vuc 300m
                        result = checkWithinArea500(position);
                        if (result) { // nam trong khu vuc tu 300m den 500m
                            double distance = getDistance(position);
                            if (oldBean != null) {
                                if (!isSimulated) {
                                    oldBean.setPosition(position);
                                } else {
                                    oldBean.setSimulatedPosition(position);
                                }
                                if (aisBean.getSog() != 0) {
                                    oldBean.setSog(aisBean.getSog());
                                }
                                oldBean.setNavigation(distance < oldBean.getDistance() ? -1 : 1);
                                oldBean.setDistance(distance);
                                if (oldBean.getNavigation() > 0) {
                                    oldBean.setAlertArea("");
                                    oldBean.setShouldSimulated(0);
                                } else {
                                    oldBean.setAlertArea(AISBean.YELLOW_ALERT);
                                    oldBean.setShouldSimulated(1);
                                }
                                if (oldBean.getNavigationImage() == 0) {
                                    oldBean.setNavigationImage(position.getLongitude() < StaticBean.MidPointLongtitude ? -1 : 1);
                                }
                            } else {
                                AISObjectList.addObject(new AISBean(userId, aisBean.getNavStatus(),
                                        position, aisBean.getShipType(), AISBean.YELLOW_ALERT, distance,
                                        new Date().getTime(),
                                        aisBean.getNavigationImage() == 0 ? (position.getLongitude() < StaticBean.MidPointLongtitude ? -1 : 1) : aisBean.getNavigationImage(),
                                        aisBean.getSog()));
                            }
                        } else { // nam ngoai khu vuc 500m
                            // nen nam trong khu vuc hien thi (500 - outside)
                            double distance = getDistance(position);
                            if (oldBean != null) {
                                if (!isSimulated) {
                                    oldBean.setPosition(position);
                                } else {
                                    oldBean.setSimulatedPosition(position);
                                }
                                if (aisBean.getSog() != 0) {
                                    oldBean.setSog(aisBean.getSog());
                                }
                                oldBean.setNavigation(distance < oldBean.getDistance() ? -1 : 1);
                                oldBean.setShouldSimulated(oldBean.getNavigation() > 0 ? 0 : 1);
                                oldBean.setDistance(distance);
                                oldBean.setAlertArea("");
                                if (oldBean.getNavigationImage() == 0) {
                                    oldBean.setNavigationImage(position.getLongitude() < StaticBean.MidPointLongtitude ? -1 : 1);
                                }
                            } else {
                                AISObjectList.addObject(new AISBean(userId, aisBean.getNavStatus(),
                                        position, aisBean.getShipType(), "", distance,
                                        new Date().getTime(),
                                        aisBean.getNavigationImage() == 0 ? (position.getLongitude() < StaticBean.MidPointLongtitude ? -1 : 1) : aisBean.getNavigationImage(),
                                        aisBean.getSog()));
                            }
                        }
                    }
                }
            } else {
                if (aisBean.getShipType() != -1) {
                    if (oldBean != null) {
                        oldBean.setShipType(aisBean.getShipType());
                        oldBean.setName(aisBean.getName());
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("hanldeAisMessage: " + ex.toString());
        }
    }

    private static boolean checkOutsideArea(Coordinates position) {
        try {
            if (CoordinatesCalculations.getDistanceBetweenTwoPoints(position, new Coordinates(StaticBean.MidPointLatitude, StaticBean.MidPointLongtitude)) > StaticBean.OutsideRadius) {
                return true;
            }
        } catch (Exception ex) {
            System.out.println("checkWithinArea1000 : " + ex);
        }
        return false;
    }

    private static boolean checkWithinArea500(Coordinates position) {
        try {
            Coordinates center = null;
            for (int i = 0; i < StaticBean.YellowPointQuantity; i++) {
                center = StaticBean.ArrayCenterPoint.get(i);
                if (CoordinatesCalculations.isInCircleArea(position, center, StaticBean.YellowRadius)) {
                    return true;
                }
            }
        } catch (Exception ex) {
            System.out.println("checkWithinArea500 : " + ex);
        }
        return false;
    }

    private static boolean checkWithinArea(Coordinates position, int radius) {
        try {
            Coordinates center = null;
            for (int i = 0; i < StaticBean.ArrayCenterPoint.size(); i++) {
                center = StaticBean.ArrayCenterPoint.get(i);
                if (CoordinatesCalculations.isInCircleArea(position, center, radius)) {
                    return true;
                }
            }
        } catch (Exception ex) {
            System.out.println("checkWithinArea300 : " + ex);
        }
        return false;
    }

    private static double getDistance(Coordinates newPost) {
        return CoordinatesCalculations.getDistanceBetweenTwoPoints(new Coordinates(StaticBean.MidPointLatitude, StaticBean.MidPointLongtitude), newPost);
    }
}
