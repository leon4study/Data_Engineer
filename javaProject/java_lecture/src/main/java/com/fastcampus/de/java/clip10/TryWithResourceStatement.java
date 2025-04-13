package com.fastcampus.de.java.clip10;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TryWithResourceStatement {
    public static void main(String[] args) throws IOException {
        try (FileOutputStream out = new FileOutputStream("test.txt")){
            out.write("Hello World!".getBytes(StandardCharsets.UTF_8));
            out.flush(); //write는 메모리에 써지는데 실제 파일에 남게 하려면 flush로 내려줘야 함.
        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
