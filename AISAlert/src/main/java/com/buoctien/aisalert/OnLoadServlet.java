/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.buoctien.aisalert;

import com.buoctien.aisalert.util.ConfigUtil;
import gnu.io.SerialPort;
import java.util.Properties;
import javax.servlet.http.HttpServlet;

/**
 *
 * @author Administrator
 */
public class OnLoadServlet extends HttpServlet {

    private SerialPort dataPort = null;
    AISTimerTask aisTimer = null;

    @Override
    public void init() {
        System.out.println("On Load Servlet start");
        try {
//            if (dataPort == null) {
//                dataPort = (new SerialUtil()).getSerialPort();
//            }
//            if (dataPort != null) {
//            String fileName = this.getServletContext().getRealPath("/ais_sample4.txt");
            String writtenFileName = this.getServletContext().getRealPath("/result.txt");
            if (aisTimer == null) {

                String fileName = this.getServletContext().getRealPath("/config.properties");
                String fromDate = "", toDate = "";
                try {
                    Properties props = ConfigUtil.readConfig(fileName);
                    if (!props.isEmpty()) {
                        fromDate = (String) props.get("start_time");
                        toDate = (String) props.get("end_time");
                    }
                } catch (Exception ex) {

                }
                new AISObjectList(fromDate, toDate);
                aisTimer = new AISTimerTask(dataPort, "", writtenFileName);
                aisTimer.run();
                aisTimer.schedule(0, 10000);
            }
//            }
        } catch (Exception ex) {
        }
        System.out.println("On Load Servlet started");
    }

    @Override
    public void destroy() {
        try {
            if (dataPort != null) {
                dataPort.close();
                dataPort = null;
            }
            if (aisTimer != null) {
                aisTimer.cancel();
                aisTimer = null;
            }
            System.out.println("On Load Servlet stopped");
        } catch (Exception ex) {

        }
        super.destroy();
    }

}
