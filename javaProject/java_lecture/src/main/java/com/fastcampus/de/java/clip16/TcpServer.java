package com.fastcampus.de.java.clip16;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;

public class TcpServer {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(8888);
            System.out.println("[" + LocalTime.now() + "] 서버가 준비되었습니다.");
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();
        }

        while (true){

            try {
                
                Socket socket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println(e.getLocalizedMessage());
                e.printStackTrace();
            }
        }

    }
}
