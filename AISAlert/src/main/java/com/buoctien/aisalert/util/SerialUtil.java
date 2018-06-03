/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.buoctien.aisalert.util;

import com.buoctien.aisalert.bean.SerialBean;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 *
 * @author DELL
 */
public class SerialUtil {

    public SerialPort getSerialPort(String portName, int baudrate) {
        try {
            Enumeration<?> e = CommPortIdentifier.getPortIdentifiers();
            while (e.hasMoreElements()) {
                CommPortIdentifier id = (CommPortIdentifier) e.nextElement();
                if (id.getPortType() == CommPortIdentifier.PORT_SERIAL && portName.equals(id.getName())) {
                    SerialPort sp = (SerialPort) id.open(this.getClass().getName(), 2000);
                    sp.setSerialPortParams(baudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                    return sp;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList getSerialPorts() {
        ArrayList ports = new ArrayList();
        try {
            Enumeration<?> e = CommPortIdentifier.getPortIdentifiers();
            while (e.hasMoreElements()) {
                CommPortIdentifier id = (CommPortIdentifier) e.nextElement();
                if (id.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                    SerialBean bean = new SerialBean();
                    bean.setId(id.getName());
                    bean.setName(id.getName());
                    ports.add(bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ports;
    }

//    public SerialPort getSerialPort() {
//        try {
//            Enumeration<?> e = CommPortIdentifier.getPortIdentifiers();
//            while (e.hasMoreElements()) {
//                CommPortIdentifier id = (CommPortIdentifier) e.nextElement();
//                if (id.getPortType() == CommPortIdentifier.PORT_SERIAL) {
//                    SerialPort sp = (SerialPort) id.open(this.getClass().getName(), 2000);
//                    sp.setSerialPortParams(4800, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
//                    return sp;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
