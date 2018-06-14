/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.buoctien.aisalert;

import com.buoctien.aisalert.bean.AlertBean;
import com.buoctien.aisalert.util.ArduinoUtil;
import com.buoctien.aisalert.util.SerialUtil;
import com.buoctien.aisalert.util.TimerUtil;
import gnu.io.SerialPort;
import java.util.TimerTask;

/**
 *
 * @author DELL
 */
public class AlertTimerTask extends TimerTask {

    private SerialPort alertDataPort = null;
    private final String configFileName;
    private boolean scheduled;

    public AlertTimerTask(String configFileName) {
        this.configFileName = configFileName;
        this.scheduled = false;
        if (this.alertDataPort == null) {
            this.alertDataPort = initAlertPort();
        }
    }

    public synchronized void schedule(long delay, long period) {
        if (!scheduled) {
            scheduled = true;
            TimerUtil.getInstance().schedule(this, delay, period);
        }
    }

    @Override
    public boolean cancel() {
        if (alertDataPort != null) {
            ArduinoUtil util = new ArduinoUtil(alertDataPort);
            util.turnAlert("", 0);
            alertDataPort.close();
            alertDataPort = null;
        }
        return super.cancel();
    }

    @Override
    public void run() {
        if (alertDataPort == null) {
            this.alertDataPort = initAlertPort();
        }
        if (alertDataPort == null) {
            return;
        }
        AlertBean alert = AISObjectList.getAlert();
        if (alert != null) {
            ArduinoUtil util = new ArduinoUtil(alertDataPort);
            util.turnAlert(alert.getAlertArea(), alert.getSoundType());
        }
    }

    private SerialPort initAlertPort() {
        return SerialUtil.initAlertPort(configFileName, "wireless_port", "wireless_baudrate");
//    return null;
    }
}
