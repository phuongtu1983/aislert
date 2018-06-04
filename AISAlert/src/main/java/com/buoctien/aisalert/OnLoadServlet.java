/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.buoctien.aisalert;

import com.buoctien.aisalert.util.SerialUtil;
import gnu.io.SerialPort;
import javax.servlet.http.HttpServlet;

/**
 *
 * @author Administrator
 */
public class OnLoadServlet extends HttpServlet {

    private SerialPort aisDataPort = null;

    AISTimerTask aisTimer = null;
    AlertTimerTask alertTimer = null;

    @Override
    public void init() {
        System.out.println("On Load Servlet start");
        try {
            String configFileName = this.getServletContext().getRealPath("/config.properties");
            if (aisDataPort == null) {
                aisDataPort = SerialUtil.initAlertPort(configFileName, "ais_port", "ais_baudrate");
            }
            String writtenFileName = this.getServletContext().getRealPath("/result.txt");
            if (aisTimer == null) {
                new AISObjectList();
                aisTimer = new AISTimerTask(aisDataPort, configFileName, writtenFileName);
                aisTimer.run();
                aisTimer.schedule(0, 10000);
            }
            if (alertTimer == null) {
                alertTimer = new AlertTimerTask(configFileName);
                alertTimer.run();
                alertTimer.schedule(0, 2000);
            }
        } catch (Exception ex) {
        }
        System.out.println("On Load Servlet started");
    }

    @Override
    public void destroy() {
        try {
            if (aisDataPort != null) {
                aisDataPort.close();
                aisDataPort = null;
            }
            if (aisTimer != null) {
                aisTimer.cancel();
                aisTimer = null;
            }
            if (alertTimer != null) {
                alertTimer.cancel();
                alertTimer = null;
            }
            System.out.println("On Load Servlet stopped");
        } catch (Exception ex) {

        }
        super.destroy();
    }

}
