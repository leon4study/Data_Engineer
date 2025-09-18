package de.example.hadoop.yarn;

public class Constants {
    /**
     * Environment key name pointing to the app master jar location
     */
    public static final String AM_JAR_PATH = "AM_JAR_PATH";

    /**
     * Environment key name denoting the file timestamp for the shell script.
     * Used to validate the local resource.
     */
    public static final String AM_JAR_TIMESTAMP = "AM_JAR_TIMESTAMP";

    /**
     * Environment key name denoting the file content length for the shell script.
     * Used to validate the local resource.
     */
    public static final String AM_JAR_LENGTH = "AM_JAR_LENGTH";

    public static final String AM_JAR_NAME = "AppMaster.jar";

    //AM_JAR_NAME : 하둡 경로에다가 yarn application사용할 전용 경로나 캐시에 요 이름을 바꿔서 올리게 됨.
}
