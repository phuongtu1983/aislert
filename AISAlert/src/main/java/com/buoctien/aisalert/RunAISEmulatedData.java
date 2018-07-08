/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.buoctien.aisalert;

import com.buoctien.aisalert.bean.AISBean;
import com.buoctien.aisalert.util.AISUtil;
import com.buoctien.aisalert.util.FileUtil;
import dk.dma.ais.message.AisMessage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author DELL
 */
public class RunAISEmulatedData extends HttpServlet {

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String configFileName = request.getServletContext().getRealPath("/ais_data.txt");
        handleEmulateData(configFileName);
    }

    private void handleEmulateData(String configFileName) {
        if (AISObjectList.emulatedDataIndex < AISObjectList.emulatorAISData.size()) {
            AisMessage aisMessage = (AisMessage) AISObjectList.emulatorAISData.get(AISObjectList.emulatedDataIndex++);
//            AISBean aisBean = AISUtil.acceptAisMessage(aisMessage);
            AISUtil.acceptAisMessage(aisMessage, false);
//            AISUtil.hanldeAisMessage(aisMessage.getUserId() + "", aisBean, false);
//            if (bean != null) {
//                writeAISDataToFile(configFileName, bean);
//            }
        }
    }

    private void writeAISDataToFile(String configFileName, AISBean bean) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd_HH:mm:SS");
            String strCurrDate = formatter.format(new java.util.Date());
            String str = "";
            str += strCurrDate + "\t";
            str += "MMSI:" + bean.getMMSI() + "\t";
            str += "ShipType:" + bean.getShipType() + "\t";
            str += "Position:" + bean.getPosition().getLatitude() + "," + bean.getPosition().getLongitude() + "\t";
            str += "Distance:" + bean.getDistance() + "\t";
            str += "Navigation:" + (bean.getNavigationImage() == 0 ? "Not set" : bean.getNavigationImage() > 0 ? "VT to SG" : "SG to VT") + "\t";
            str += "Alert:" + bean.getAlertArea() + "\t";
            str += "Speed:" + bean.getSog() + "\t";
            long diffSec = (new Date().getTime() - bean.getMilisec()) / 1000;
            String diffSecString = "";
            if (diffSec < 60) {
                diffSecString = diffSec + " s";
            } else {
                diffSecString = (int) diffSec / 60 + " min";
                if (diffSec > 0) {
                    diffSecString += " " + diffSec % 60 + " s";
                }
            }
            str += "ReportAge:" + diffSecString + "\t";
            FileUtil.writeToFile(configFileName, str);
        } catch (Exception ex) {
            System.out.println("writeAISDataToFile : " + ex);
            FileUtil.writeToFile(configFileName, "writeAISDataToFile : " + ex);
        }
    }
}
