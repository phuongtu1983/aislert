/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.buoctien.aisalert;

import com.buoctien.aisalert.bean.AISBean;
import java.util.ArrayList;

/**
 *
 * @author DELL
 */
public class AISObjectList {

    private final static ArrayList aisList = new ArrayList();

    public static void addObject(AISBean bean) {
        AISBean obj = null;
        for (int i = 0; i < aisList.size(); i++) {
            obj = (AISBean) aisList.get(i);
            if (obj.getMMSI().equals(bean.getMMSI())) {
                obj.setPosition(bean.getPosition());
                return;
            }
        }
        aisList.add(bean);
    }

    public static void removeObject(AISBean bean) {
        AISBean obj = null;
        for (int i = 0; i < aisList.size(); i++) {
            obj = (AISBean) aisList.get(i);
            if (obj.getMMSI().equals(bean.getMMSI())) {
                aisList.remove(i);
                return;
            }
        }
    }

    public static boolean isContains(String key) {
        AISBean obj = null;
        for (int i = 0; i < aisList.size(); i++) {
            obj = (AISBean) aisList.get(i);
            if (obj.getMMSI().equals(key)) {
                return true;
            }
        }
        return false;
    }

    public static AISBean get(String key) {
        AISBean obj = null;
        for (int i = 0; i < aisList.size(); i++) {
            obj = (AISBean) aisList.get(i);
            if (obj.getMMSI().equals(key)) {
                return obj;
            }
        }
        return null;
    }

    public static ArrayList getList() {
        return aisList;
    }

    public static String getAlert() {
        AISBean obj = null;
        String alert = "";
        for (int i = 0; i < aisList.size(); i++) {
            obj = (AISBean) aisList.get(i);
            if (obj.getAlertArea().equals(AISBean.RED_ALERT)) {
                return AISBean.RED_ALERT;
            } else if (obj.getAlertArea().equals(AISBean.YELLOW_ALERT)) {
                alert = obj.getAlertArea();
            }
        }
        return alert;
    }

}
