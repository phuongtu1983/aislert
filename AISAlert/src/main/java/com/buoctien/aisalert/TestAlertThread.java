/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.buoctien.aisalert;

import com.buoctien.aisalert.util.ConfigUtil;
import com.buoctien.aisalert.util.SerialUtil;
import gnu.io.SerialPort;
import java.io.OutputStream;
import java.util.Properties;

/**
 *
 * @author DELL
 */
public class TestAlertThread extends Thread {

    String configFileName = "";

    public TestAlertThread(String configFileName) {
        this.configFileName = configFileName;
    }

    @Override
    public void run() {
        try {
            Properties props = ConfigUtil.readConfig(configFileName);
            String aisPort = props.getProperty("ais_port");
            String aisBaudrate = props.getProperty("ais_baudrate");
            SerialUtil serialUtil = new SerialUtil();
            SerialPort port = serialUtil.getSerialPort(aisPort, Integer.parseInt(aisBaudrate));
            if (port != null) {
                OutputStream outputStream = port.getOutputStream();
                outputStream.write(65);
                outputStream.write(66);
                outputStream.write(67);
                outputStream.write(68);
                outputStream.write(69);
                outputStream.write(70);
                outputStream.write(71);
                outputStream.write(72);
                
                Thread.sleep(10000);
                
                outputStream.write(97);
                outputStream.write(98);
                outputStream.write(99);
                outputStream.write(100);
                outputStream.write(101);
                outputStream.write(102);
                outputStream.write(103);
                outputStream.write(104);
                
                outputStream.close();
                port.close();
                port = null;
            }
        } catch (Exception ex) {
        }
    }

}
