/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.buoctien.aisalert.util;

import com.buoctien.aisalert.bean.AISBean;
import gnu.io.SerialPort;
import java.io.OutputStream;

/**
 *
 * @author DELL
 */
public class ArduinoUtil {

    private SerialPort serialPort = null;

    public ArduinoUtil(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    public void turnAlert(String alert, int soundType) {
        if (serialPort == null) {
            return;
        }
        try {
            OutputStream outputStream = serialPort.getOutputStream();
            if (alert.equals(AISBean.RED_ALERT)) {
                switch (soundType) {
                    case 0:
                        outputStream.write(65);
                        break;
                    case 1:
                        outputStream.write(66);
                        break;
                    case 2:
                        outputStream.write(67);
                        break;
                }
            } else if (alert.equals(AISBean.YELLOW_ALERT)) {
                switch (soundType) {
                    case 0:
                        outputStream.write(68);
                        break;
                    case 1:
                        outputStream.write(69);
                        break;
                    case 2:
                        outputStream.write(70);
                        break;
                }
            } else if (alert.equals("")) {
                // turn off alert
                outputStream.write(71);
            }
            outputStream.close();
        } catch (Exception ex) {
        }
    }
}
