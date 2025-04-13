package com.fastcampus.de.java.clip10_5_2_e;

import com.fastcampus.de.java.clip10_5_2_quiz.IllegalPathAccessError;

import java.io.File;

public class FileValidator {
    public static boolean validate(String path){
        if (path.startsWith("/Users/")){
            //success
            File file = new File(path);
            return file.exists();
        }else {
            throw new IllegalPathAccessError(path);
        }
    }
}
