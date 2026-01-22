package com.workbuddy.cluster.service;


import org.springframework.stereotype.Service;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

@Service
public class WOLService {

    public void sendMagicPacket(String macAddress) {
        try {
            byte[] macBytes = new byte[6];
            String[] hex = macAddress.split(":");

            for (int i = 0; i < 6; i++) {
                macBytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }

            byte[] bytes = new byte[102];
            for (int i = 0; i < 6; i++) bytes[i] = (byte) 0xFF;
            for (int i = 6; i < bytes.length; i += 6)
                System.arraycopy(macBytes, 0, bytes, i, 6);

            InetAddress address = InetAddress.getByName("255.255.255.255");
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, 9);

            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            socket.send(packet);
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
