/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.buoctien.aisalert;

import com.buoctien.aisalert.bean.AISBean;
import com.buoctien.aisalert.util.AISUtil;
import com.buoctien.aisalert.util.FileUtil;
import dk.dma.ais.filter.DownSampleFilter;
import dk.dma.ais.filter.DuplicateFilter;
import dk.dma.ais.filter.MessageHandlerFilter;
import dk.dma.ais.message.AisMessage;
import dk.dma.ais.message.AisMessage1;
import dk.dma.ais.message.AisPosition;
import dk.dma.ais.reader.AisReader;
import gnu.io.SerialPort;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

/**
 *
 * @author DELL
 */
public class AISThread extends Thread {

    private final SerialPort dataPort;
    private final String writtenFileName;
    private boolean stoped;

    public AISThread(String writtenFileName, SerialPort dataPort) {
        this.writtenFileName = writtenFileName;
        this.dataPort = dataPort;
        this.stoped = false;
    }

    @Override
    public void run() {
        try {
//            runFromSerialPort();
//            this.stoped = true;
            createEmulatorData();
        } catch (Exception ex) {
            this.stoped = true;
            System.out.println("run : " + ex);
            FileUtil.writeToFile(writtenFileName, "run : " + ex);
        }
    }

    public boolean isStoped() {
        return stoped;
    }

    private void runFromSerialPort() {
        try {
            AisReader reader = AISUtil.readFromSerialPort(dataPort);
            if (reader != null) {
                Consumer<AisMessage> handler = new Consumer<AisMessage>() {
                    @Override
                    public void accept(AisMessage aisMessage) {
                        aisMessageHandle(aisMessage);
                    }
                };
                // Make down sampling filter with sampling rate 30 sec and
                // make handler the recipient of down sampled messages
                MessageHandlerFilter downsampleFilter = new MessageHandlerFilter(new DownSampleFilter(30));
                downsampleFilter.registerReceiver(handler);

                // Make doublet filter with default window of 10 secs.
                // Set down sample filter as recipient of doublet filered messages
                MessageHandlerFilter doubletFilter = new MessageHandlerFilter(new DuplicateFilter(10));
                doubletFilter.registerReceiver(downsampleFilter);

                reader.registerHandler(doubletFilter);
                reader.start();
                reader.join();
            }
        } catch (Exception ex) {
            this.stoped = true;
            System.out.println("runFromSerialPort : " + ex);
            FileUtil.writeToFile(writtenFileName, "runFromSerialPort : " + ex);
        }
    }

    private synchronized void aisMessageHandle(AisMessage aisMessage) {
        try {
//            AISBean aisBean = AISUtil.acceptAisMessage(aisMessage, false);
            AISUtil.acceptAisMessage(aisMessage, false);
//            if (bean != null) {
//                writeAISDataToFile(bean);
//            }
        } catch (Exception ex) {
            System.out.println("aisMessageHandle : " + ex);
            FileUtil.writeToFile(writtenFileName, "aisMessageHandle : " + ex);
        }
    }

    private void createEmulatorData() {
        createMessage(123456789, 10.657903, 106.805794);
        createMessage(123456789, 10.658346, 106.8047);
        createMessage(123456789, 10.658788, 106.803756);
        createMessage(987654321, 10.670628, 106.785344);
        createMessage(123456789, 10.658978, 106.803198);
        createMessage(987654321, 10.670323, 106.786224);
        createMessage(123456789, 10.659337, 106.802597);
        createMessage(987654321, 10.669827, 106.787146);
        createMessage(123456789, 10.659526, 106.802018);
        createMessage(123456789, 10.659758, 106.801417);
        createMessage(987654321, 10.669321, 106.78809);
        createMessage(987654321, 10.669005, 106.788756);
        createMessage(987654321, 10.668583, 106.789335);
        createMessage(123456789, 10.660307, 106.800473);
        createMessage(987654321, 10.668183, 106.789936);
        createMessage(123456789, 10.660297, 106.800344);
        createMessage(987654321, 10.667656, 106.790644);
        createMessage(123456789, 10.660402, 106.80013);
        createMessage(987654321, 10.667076, 106.79147);
        createMessage(123456789, 10.660476, 106.799937);
        createMessage(123456789, 10.660603, 106.799765);
        createMessage(123456789, 10.660697, 106.79954);
        createMessage(987654321, 10.666928, 106.791803);
        createMessage(987654321, 10.666686, 106.792082);
        createMessage(987654321, 10.666443, 106.792447);
        createMessage(987654321, 10.666264, 106.792726);
        createMessage(123456789, 10.660792, 106.799325);
        createMessage(987654321, 10.666053, 106.79308);
        createMessage(123456789, 10.660898, 106.799143);
        createMessage(987654321, 10.665737, 106.793487);
        createMessage(123456789, 10.661024, 106.798982);
        createMessage(987654321, 10.665568, 106.79368);
        createMessage(123456789, 10.661151, 106.798724);
        createMessage(987654321, 10.665336, 106.793906);
        createMessage(123456789, 10.661277, 106.798456);
        createMessage(987654321, 10.665178, 106.794088);
        createMessage(123456789, 10.661425, 106.798242);
        createMessage(987654321, 10.664904, 106.794356);
        createMessage(123456789, 10.661573, 106.798059);
        createMessage(123456789, 10.661731, 106.797866);
        createMessage(123456789, 10.661826, 106.797705);
        createMessage(987654321, 10.664720, 106.794545);
        createMessage(123456789, 10.661952, 106.797512);
        createMessage(987654321, 10.664488, 106.794824);
        createMessage(123456789, 10.662131, 106.797351);
        createMessage(987654321, 10.664193, 106.795136);
        createMessage(123456789, 10.662216, 106.797147);
        createMessage(987654321, 10.664193, 106.795136);
        createMessage(987654321, 10.663876, 106.795415);
        createMessage(987654321, 10.663486, 106.795822);
        createMessage(123456789, 10.662353, 106.796997);
        createMessage(987654321, 10.663112, 106.796246);
        createMessage(123456789, 10.662500, 106.796804);
        createMessage(987654321, 10.662901, 106.796396);
        createMessage(123456789, 10.662680, 106.796664);
        createMessage(987654321, 10.662680, 106.796664);
        createMessage(123456789, 10.662901, 106.796396);
        createMessage(987654321, 10.662500, 106.796804);
        createMessage(123456789, 10.663112, 106.796246);
        createMessage(987654321, 10.662353, 106.796997);
        createMessage(123456789, 10.663486, 106.795822);
        createMessage(123456789, 10.663876, 106.795415);
        createMessage(123456789, 10.664193, 106.795136);
        createMessage(987654321, 10.662216, 106.797147);
        createMessage(987654321, 10.662131, 106.797351);
        createMessage(987654321, 10.661952, 106.797512);
        createMessage(123456789, 10.664488, 106.794824);
        createMessage(987654321, 10.661826, 106.797705);
        createMessage(123456789, 10.664720, 106.794545);
        createMessage(987654321, 10.661731, 106.797866);
        createMessage(123456789, 10.664904, 106.794356);
        createMessage(987654321, 10.661573, 106.798059);
        createMessage(123456789, 10.665178, 106.794088);
        createMessage(987654321, 10.661425, 106.798242);
        createMessage(123456789, 10.665336, 106.793906);
        createMessage(987654321, 10.661277, 106.798456);
        createMessage(123456789, 10.665568, 106.79368);
        createMessage(987654321, 10.661151, 106.798724);
        createMessage(123456789, 10.665737, 106.793487);
        createMessage(987654321, 10.661024, 106.798982);
        createMessage(123456789, 10.666053, 106.79308);
        createMessage(987654321, 10.660898, 106.799143);
        createMessage(123456789, 10.666264, 106.792726);
        createMessage(987654321, 10.660792, 106.799325);
        createMessage(123456789, 10.666443, 106.792447);
        createMessage(987654321, 10.660697, 106.79954);
        createMessage(123456789, 10.666686, 106.792082);
        createMessage(123456789, 10.666928, 106.791803);
        createMessage(123456789, 10.667076, 106.79147);
        createMessage(987654321, 10.660603, 106.799765);
        createMessage(987654321, 10.660476, 106.799937);
        createMessage(987654321, 10.660402, 106.80013);
        createMessage(987654321, 10.660297, 106.800344);
        createMessage(987654321, 10.660307, 106.800473);
        createMessage(987654321, 10.659758, 106.801417);
        createMessage(123456789, 10.667656, 106.790644);
        createMessage(123456789, 10.668183, 106.789936);
        createMessage(123456789, 10.668583, 106.789335);
        createMessage(987654321, 10.659526, 106.802018);
        createMessage(123456789, 10.669005, 106.788756);
        createMessage(987654321, 10.659337, 106.802597);
        createMessage(123456789, 10.669321, 106.78809);
        createMessage(987654321, 10.658978, 106.803198);
        createMessage(123456789, 10.669827, 106.787146);
        createMessage(987654321, 10.658788, 106.803756);
        createMessage(123456789, 10.670323, 106.786224);
        createMessage(123456789, 10.670628, 106.785344);
        createMessage(987654321, 10.658346, 106.8047);
        createMessage(987654321, 10.657903, 106.805794);
    }

    private void createMessage(int id, double lat, double lon) {
        AisMessage1 aisMessage = null;
        AisPosition pos = null;
        aisMessage = new AisMessage1();
        aisMessage.setUserId(id);
        pos = new AisPosition();
        pos.setLatitude(Math.round(lat * 10000.0 * 60.0));
        pos.setLongitude(Math.round(lon * 10000.0 * 60.0));
        aisMessage.setPos(pos);
        aisMessage.setSog(20);
        AISObjectList.emulatorAISData.add(aisMessage);
    }

    private void writeAISDataToFile(AISBean bean) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd_HH:mm:SS");
            String strCurrDate = formatter.format(new java.util.Date());
            String str = "";
            str += strCurrDate + "\t";
            str += "MMSI:" + bean.getMMSI() + "\t";
            str += "ShipType:" + bean.getShipType() + "\t";
            str += "Position:" + bean.getPosition().getLatitude() + "," + bean.getPosition().getLongitude() + "\t";
            str += "Distance:" + bean.getDistance() + "\t";
            str += "Navigation:" + (bean.getNavigationImage() == 0 ? "Not set" : bean.getNavigationImage() > 0 ? "VT to SG" : "SG to VT") + "\t";
            str += "Alert:" + bean.getAlertArea() + "\t";
            str += "Speed:" + bean.getSog() + "\t";
            long diffSec = (new Date().getTime() - bean.getMilisec()) / 1000;
            String diffSecString = "";
            if (diffSec < 60) {
                diffSecString = diffSec + " s";
            } else {
                diffSecString = (int) diffSec / 60 + " min";
                if (diffSec > 0) {
                    diffSecString += " " + diffSec % 60 + " s";
                }
            }
            str += "ReportAge:" + diffSecString + "\t";
            FileUtil.writeToFile(writtenFileName, str);
        } catch (Exception ex) {
            System.out.println("writeAISDataToFile : " + ex);
            FileUtil.writeToFile(writtenFileName, "writeAISDataToFile : " + ex);
        }
    }
}
