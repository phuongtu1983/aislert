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
//            if (aisDataPort == null) {
//                aisDataPort = (new SerialUtil()).getSerialPort();
//            }
//            if (aisDataPort != null) {
            String writtenFileName = this.getServletContext().getRealPath("/result.txt");
            if (aisTimer == null) {

                String dataFileName = this.getServletContext().getRealPath("/data.txt");
//                String fromDate = "", toDate = "";
//                    try {
//                        Properties props = ConfigUtil.readConfig(fileName);
//                        if (!props.isEmpty()) {
//                            fromDate = (String) props.get("start_time");
//                            toDate = (String) props.get("end_time");
//                        }
//                    } catch (Exception ex) {
//
//                    }
//                new AISObjectList(fromDate, toDate);
                new AISObjectList();
                aisTimer = new AISTimerTask(aisDataPort, dataFileName, writtenFileName);
                aisTimer.run();
                aisTimer.schedule(0, 10000);
            }
            if (alertTimer == null) {
                String configFileName = this.getServletContext().getRealPath("/config.properties");
                alertTimer = new AlertTimerTask(configFileName);
                alertTimer.run();
                alertTimer.schedule(0, 2000);
            }
//            }
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
