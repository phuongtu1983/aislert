/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.buoctien.aisalert;

import com.buoctien.aisalert.util.TimerUtil;
import gnu.io.SerialPort;
import java.util.TimerTask;

/**
 *
 * @author DELL
 */
public class AISTimerTask extends TimerTask {

    private AISThread aisThread = null;
    private final SerialPort dataPort;
    private final String fileName;
    private final String writtenFileName;
    private boolean scheduled;

    public AISTimerTask(SerialPort dataPort, String fileName, String writtenFileName) {
        this.dataPort = dataPort;
        this.fileName = fileName;
        this.writtenFileName = writtenFileName;
        scheduled = false;
    }

    public synchronized void schedule(long delay, long period) {
        if (!scheduled) {
            scheduled = true;
            TimerUtil.getInstance().schedule(this, delay, period);
        }
    }

    @Override
    public boolean cancel() {
        return super.cancel();
    }

    @Override
    public void run() {
        if (aisThread == null || aisThread.isStoped()) {
            if (AISObjectList.isValidPeriod()) {
                aisThread = new AISThread(fileName, writtenFileName, dataPort);
                aisThread.start();
            }
        }
    }
}
