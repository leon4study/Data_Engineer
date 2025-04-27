package com.fastcampus.de.java.clip16;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;

public class TcpClient {
    public static void main(String[] args) {

        try {
            String serverIP = "localhost"; //127.0.0.1 넣어도 됨.
            System.out.println("[" + LocalTime.now() + "] 서버에 연결합니다.");
            System.out.println("[" + LocalTime.now() + "] 서버 IP : " + serverIP);
            // 소켓이 서버한테 연결요청을 한 다음에 연결요청이 성공하면 socket 객체가 나온다.
            Socket socket = new Socket(serverIP, 8888);

            // 이 소켓 객체로 outputStream을 만들어서 outputStream을 받은 다음에
            OutputStream outputStream = socket.getOutputStream();
            // Hello라고 라고 쓰고
            outputStream.write("Hello\n".getBytes(StandardCharsets.UTF_8));
            // 보내고
            outputStream.flush();

            // 뭔데 데이터 줄 거를 읽어줌
            InputStream inputStream = socket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            // 읽은다음 출력. 서버로부터 받은 메세지를 dataInputStream에서 읽는다.
            System.out.println("[" + LocalTime.now() + "] message from server : "+ dataInputStream.readUTF());
            // 연결 종료
            dataInputStream.close();
            socket.close();
            System.out.println("[" + LocalTime.now() + "] 연결이 종료되었습니다.");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
