package de.example.hadoop.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

public class InputReadAndFileWriter {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java InputReadAndFileWriter <input file> <output file>");
            System.exit(1);
        }

        String filePath = args[0];
        String contents = args[1];

        try{
            Configuration configuration = new Configuration();
            FileSystem hdfs = FileSystem.get(configuration);

            // Check path & delete if exists
            Path path = new Path(filePath);
            if (hdfs.exists(path)) {
                hdfs.delete(path, true); //recursive true
                System.out.println("#-#-#" + filePath+ " is deleted");
            }

            // Write contents as file
            FSDataOutputStream outputStream = hdfs.create(path);
            outputStream.writeUTF(contents);
            outputStream.close();

            // Make inputStream from file
            FSDataInputStream inputStream = hdfs.open(path);
            String result = inputStream.readUTF();
            inputStream.close();

            // 어떤 컨텐츠를 저장했다.
            System.out.println("#-#-# Saved contents: " + result);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
