/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.buoctien.aisalert;

import com.buoctien.aisalert.bean.AISBean;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author DELL
 */
public class AISMapJsonServlet extends HttpServlet {

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
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        try {
            ArrayList list = AISObjectList.getList();
            AISBean obj = null;
            String jsonResult = "";
            long currentMilisec = new Date().getTime();
            long diffSec = 0;
            String diffSecString = "";
//            int[] allowNavigation = {0, 7, 8, 9, 10, 14};
            for (int i = 0; i < list.size(); i++) {
                obj = (AISBean) list.get(i);
//                if (!ArrayUtils.contains(allowNavigation, obj.getNavStatus())) {
//                    continue;
//                }
                String latitude = "", longtitude = "";
                diffSec = (currentMilisec - obj.getMilisec()) / 1000;
                if (diffSec < 60) {
                    diffSecString = diffSec + " s";
                } else {
                    diffSecString = (int) diffSec / 60 + " min";
                    if (diffSec > 0) {
                        diffSecString += " " + diffSec % 60 + " s";
                    }
                }
                if (obj.getSimulatedPosition() == null) {
                    latitude = obj.getPosition().getLatitude() + "";
                    longtitude = obj.getPosition().getLongitude() + "";
                } else {
                    latitude = obj.getSimulatedPosition().getLatitude() + "";
                    longtitude = obj.getSimulatedPosition().getLongitude() + "";
                }
                if (!jsonResult.isEmpty()) {
                    jsonResult += ",";
                }
                jsonResult += "{" + "\"name\":\"" + obj.getName() + "\",\"id\":\"" + obj.getMMSI()
                        + "\",\"latitude\":" + latitude + ",\"longtitude\":" + longtitude
                        + ",\"distance\":" + (int) obj.getDistance()
                        + ",\"isSimulation\":\"" + (obj.getSimulatedPosition() == null ? 0 : 1) + "\""
                        + ",\"reportAge\":\"" + diffSecString + "\""
                        + ",\"navigationImage\":" + obj.getNavigationImage() + ",\"shipType\":" + obj.getShipType() + "}";
            }
            jsonResult = "{\"alert\":\"" + AISObjectList.getAlert().getAlertArea() + "\",\"aisList\":[" + jsonResult + "]}";

            PrintWriter out = response.getWriter();
            out.print(jsonResult);
//            System.out.println(current_time_str + " ; jsonResult: " + jsonResult);
        } catch (Exception ex) {
        }
    }
}
