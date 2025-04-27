package com.fastcampus.de.java.clip16;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;

public class TcpServer {
    public static void main(String[] args) {
        //자바에서 서버가 사용하는 가장 기본 소켓
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(8888);
            // 위 라인이 성공하면 포트를 얻었을 것이다.
            System.out.println("[" + LocalTime.now() + "] 서버가 준비되었습니다.");
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();
        }

        while (true){
            //연결 요청을 기다리는 코드 구현
            try {
                System.out.println("[" + LocalTime.now() + "] 연결 요청을 기다립니다.");
                Socket socket = serverSocket.accept(); // 실제 연결 요청할 때 까지 여기서 계속 기다림.
                // 연결이 되면 소켓을 리턴하면서 accept라는 함수가 끝나게 됨.
                System.out.println("[" + LocalTime.now() + "] 연결되었습니다.");


                //클라이언트가 데이터를 보내는 프로그램 구현
                InputStream inputStream = socket.getInputStream(); // InputStream은 최상위 클래스.
                // inputStream의 기본 read를 하면 몇 바이트를 보낼지 받아되고, 아니면 계속 읽어서 내가 원하는 단위로 쪼개는 작업을 해야함.
                // 이런 게 어렵기 때문에 BufferedReader 같은 라이브러리 사용
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String inputString = bufferedReader.readLine();
                System.out.println("[" + LocalTime.now() + "] Message from client : " + inputString);

                // 응답을 해줄 거임
                OutputStream outputStream = socket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                dataOutputStream.writeUTF("World");
                System.out.println("[" + LocalTime.now() + "] 데이터를 전송했습니다.");
                dataOutputStream.close();
                socket.close();


            } catch (IOException e) {
                System.out.println(e.getLocalizedMessage());
                e.printStackTrace();
            }
        }

    }
}
