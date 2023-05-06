package org.microburstdetection;


import org.microburstdetection.packethandler.TcpUdpHandler;
import io.pkts.Pcap;

public class Main {
    public static void main(String[] args) {
        // args[0]: path of pcap file
        String sourceFilePath = args[0];
        // read pcap file
        try {
            Pcap pcap = Pcap.openStream(sourceFilePath);
            pcap.loop(new TcpUdpHandler());
            pcap.close();
        }catch (Exception e){
            System.out.println(e);
        }

    }
}
