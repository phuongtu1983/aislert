/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.buoctien.aisalert;

import com.buoctien.aisalert.util.ConfigUtil;
import java.io.IOException;
import java.util.Properties;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Administrator
 */
public class ConfigServlet extends HttpServlet {

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
        try {
            String fileName = this.getServletContext().getRealPath("/config.properties");
            String start_time = request.getParameter("start_time");
            if (start_time != null && !start_time.isEmpty()) {
                try {
                    Properties props = new Properties();
                    props.setProperty("start_time", request.getParameter("start_time"));
                    props.setProperty("end_time", request.getParameter("end_time"));
                    ConfigUtil.saveConfig(fileName, props);
                    AISObjectList.setFromDate((String) props.get("start_time"));
                    AISObjectList.setToDate((String) props.get("end_time"));
                } catch (Exception ex) {

                }
            }
            Properties props = ConfigUtil.readConfig(fileName);
            request.setAttribute("properties", props);
            RequestDispatcher dispatcher = request.getRequestDispatcher("config.jsp");
            dispatcher.forward(request, response);
        } catch (Exception ex) {
            // I/O error
            System.out.print("Exception: " + ex.toString());
        }
    }
}
