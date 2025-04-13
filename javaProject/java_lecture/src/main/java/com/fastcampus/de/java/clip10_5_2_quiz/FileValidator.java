package com.fastcampus.de.java.clip10_5_2_quiz;

import java.io.File;

public class FileValidator {

    public static boolean validate(String path) throws IllegalPathAccessError {
        if (path.startsWith("/Users/")){
            File file = new File(path);
            return file.exists();
        }else {
            throw new IllegalPathAccessError(path);
        }
    }
}
