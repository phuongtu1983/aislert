/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.buoctien.aisalert;

import com.buoctien.aisalert.bean.AISBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author DELL
 */
public class AISObjectList {

    private final static ArrayList aisList = new ArrayList();
    private static Date fromDate = null;
    private static Date toDate = null;
//    public static String currentAlert = ""; // ''=off; 'RED'=bao dong do; 'YELLOW'=bao dong vang

    public AISObjectList(String _fromDate, String _toDate) {
        try {
            fromDate = parseDate(_fromDate);
            toDate = parseDate(_toDate);
        } catch (Exception ex) {

        }
    }

    public static void setFromDate(String _fromDate) {
        try {
            fromDate = parseDate(_fromDate);
        } catch (Exception ex) {

        }
    }

    public static void setToDate(String _toDate) {
        try {
            toDate = parseDate(_toDate);
        } catch (Exception ex) {

        }
    }

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

    private static Date parseDate(String date) {
        try {
            SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
            return parser.parse(date);
        } catch (java.text.ParseException e) {
        }
        return null;
    }

    public static boolean isValidPeriod() {
        return true;
//        try {
//            Calendar now = Calendar.getInstance();
//            int hour = now.get(Calendar.HOUR_OF_DAY);
//            int minute = now.get(Calendar.MINUTE);
//            String strDate = hour + ":" + minute;
//            Date date = parseDate(strDate);
//            if (date != null && fromDate != null && toDate != null) {
//                if (fromDate.before(date) && toDate.after(date)) {
//                    return true;
//                }
//            }
//        } catch (Exception ex) {
//
//        }
//        return false;
    }
}
