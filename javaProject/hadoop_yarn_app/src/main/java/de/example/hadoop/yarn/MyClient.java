package de.example.hadoop.yarn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.ApplicationConstants.Environment;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationResponse;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.URL;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.YarnClientApplication;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.Records;

public class MyClient {

    // Start time for client
    private final long clientStartTime = System.currentTimeMillis();

    // Configuration
    private Configuration conf;

    private YarnClient yarnClient;

    // Application master specific info to register a new Application with RM/ASM
    private String appName = "";

    // App master priority
    private int amPriority = 0;

    // Queue for App master
    private String amQueue = "";

    // Amt. of memory resource to request for to run the App Master
    private long amMemory = 10;

    // Amt. of virtual core resource to request for to run the App Master
    private int amVCores = 1;

    // ApplicationMaster jar file
    private String appMasterJarPath = "";

    // Container priority
    private int requestPriority = 0;

    // Amt of memory to request for container in which the HelloYarn will be executed
    private int containerMemory = 10;

    // Amt. of virtual cores to request for container in which the HelloYarn will be executed
    private int containerVirtualCores = 1;

    // No. of containers in which the HelloYarn needs to be executed
    private int numContainers = 1;

    // Timeout threshold for client. Kill app after time interval expires.
    private long clientTimeout = 600000;

    // Command line options
    private Options opts;

    /**
     * Constructor
     *
     */
    public MyClient() throws Exception {
        createYarnClient();
        initOptions();
    }

    private void createYarnClient() {
        // 맨 처음 yarn 클라이언트가 yarn에 있는 리소스 매니저한테 뭔가 요청을 하는데 그 전에 클라이언트가 있어야겠지?
        // 그 클라이언트를 말한다. 잡을 요청하는 쪽에서 동작하는 친구
        /*
        Application 실행 요청 1 - YarnClient 생성 및 초기화
        Yarn 클라이언트 생성 → ResourceManager(RM)와 통신할 준비
         */
        yarnClient = YarnClient.createYarnClient();
        this.conf = new YarnConfiguration();
        yarnClient.init(conf);
    }

    private void initOptions() {
        opts = new Options();
        // Yarn으로 이 작업을 서밋할 때 파라미터로 넣을 수 있게 만든 옵션
        opts.addOption("appname", true, "Application Name. Default value - HelloYarn");
        opts.addOption("priority", true, "Application Priority. Default 0");
        opts.addOption("queue", true, "RM Queue in which this application is to be submitted");
        opts.addOption("timeout", true, "Application timeout in milliseconds");
        opts.addOption("master_memory", true,
                "Amount of memory in MB to be requested to run the application master");
        opts.addOption("master_vcores", true,
                "Amount of virtual cores to be requested to run the application master");
        opts.addOption("jar", true, "Jar file containing the application master");
        opts.addOption("container_memory", true, "Amount of memory in MB to be requested to run the HelloYarn");
        opts.addOption("container_vcores", true,
                "Amount of virtual cores to be requested to run the HelloYarn");
        opts.addOption("num_containers", true, "No. of containers on which the HelloYarn needs to be executed");
        opts.addOption("help", false, "Print usage");
    }

    /**
     * Helper function to print out usage
     */
    private void printUsage() {
        new HelpFormatter().printHelp("Client", opts);
    }

    /**
     * Parse command line options
     * @param args Parsed command line options
     * @return Whether the init was successful to run the client
     * @throws org.apache.commons.cli.ParseException
     */

    public boolean init(String[] args) throws ParseException {

        CommandLine cliParser = new GnuParser().parse(opts, args);

        /*
        GNU 스타일 커맨드라인 옵션을 지원 (--option 형태 가능)
        예: --appname MyApp --priority 5
        반환값: CommandLine 객체 → 각 옵션의 값 조회 가능

        Commons CLI 1.3 이후로는 DefaultParser를 사용하기를 권장합니다. GnuParser는 구버전 스타일임
         */

        if (args.length == 0) {
            throw new IllegalArgumentException("No args specified for client to initialize");
        }

        if (cliParser.hasOption("help")) {
            printUsage();
            return false;
        }

        appName = cliParser.getOptionValue("appname", "HelloYarn");
        amPriority = Integer.parseInt(cliParser.getOptionValue("priority", "0"));
        amQueue = cliParser.getOptionValue("queue", "default");
        amMemory = Integer.parseInt(cliParser.getOptionValue("master_memory", "10"));
        amVCores = Integer.parseInt(cliParser.getOptionValue("master_vcores", "1"));

        if (amMemory < 0) {
            throw new IllegalArgumentException("Invalid memory specified for application master, exiting."
                    + " Specified memory=" + amMemory);
        }
        if (amVCores < 0) {
            throw new IllegalArgumentException(
                    "Invalid virtual cores specified for application master, exiting."
                            + " Specified virtual cores=" + amVCores);
        }

        if (!cliParser.hasOption("jar")) {
            throw new IllegalArgumentException("No jar file specified for application master");
        }

        appMasterJarPath = cliParser.getOptionValue("jar");

        // Setting Container
        containerMemory = Integer.parseInt(cliParser.getOptionValue("container_memory", "10"));
        containerVirtualCores = Integer.parseInt(cliParser.getOptionValue("container_vcores", "1"));
        numContainers = Integer.parseInt(cliParser.getOptionValue("num_containers", "1"));

        if (containerMemory < 0 || containerVirtualCores < 0 || numContainers < 1) {
            throw new IllegalArgumentException("Invalid no. of containers or container memory/vcores specified,"
                    + " exiting."
                    + " Specified containerMemory=" + containerMemory
                    + ", containerVirtualCores=" + containerVirtualCores
                    + ", numContainer=" + numContainers);
        }

        // 클라이언트가 AM 응답을 기다리는 시간 (밀리초 단위)
        // 기본값 600,000ms → 10분
        clientTimeout = Integer.parseInt(cliParser.getOptionValue("timeout", "600000"));

        return true;
    }

    /**
     * Main run function for the client
     * @return true if application completed successfully
     * @throws java.io.IOException
     * @throws org.apache.hadoop.yarn.exceptions.YarnException
     */
    public boolean run() throws IOException, YarnException {

        System.out.println("Running Client");
        yarnClient.start();


        /*
        Application 실행 요청 2 - 새로운 Application 요청
        - yarnClient.createApplication() 호출 → RM에게 신규 Application ID 발급 요청
        - GetNewApplicationResponse → 클러스터가 지원 가능한 최대 리소스 정보 포함
        - 클라이언트는 ClientRMService 의 createNewApplication() 을 호출해서 Application ID 발급을 요청한다.
            ClientRMService는 클라이언트의 요청에 새로운 Application ID와 Yarn 클러스터에서 최대로 할당할 수 있는 리소스 정보가 설정되어있는
            GetNewApplicationResponse 객체를 전달한다.
         */

        // Get a new application id
        YarnClientApplication app = yarnClient.createApplication();

        GetNewApplicationResponse appResponse = app.getNewApplicationResponse();
        // 여기서 id 가져옴
        // GetNewApplicationResponse → 클러스터가 지원할 수 있는 최대 리소스 정보 포함
        // app.getNewApplicationResponse() → 최대 리소스 정보 확인

        long maxMem = appResponse.getMaximumResourceCapability().getMemorySize();
        // yarn이 너한테 할당해 줄 수 있는 최대 메모리 사이즈 얼만큼이야 알려줌
        System.out.println("Max mem capabililty of resources in this cluster " + maxMem);

        // A resource ask cannot exceed the max.
        if (amMemory > maxMem) {
            System.out.println("AM memory specified above max threshold of cluster. Using max value."
                    + ", specified=" + amMemory
                    + ", max=" + maxMem);
            amMemory = maxMem;
        }

        int maxVCores = appResponse.getMaximumResourceCapability().getVirtualCores();
        System.out.println("Max virtual cores capabililty of resources in this cluster " + maxVCores);

        // AM를 띄우는 걸 우선하는 코드
        if (amVCores > maxVCores) {
            System.out.println("AM virtual cores specified above max threshold of cluster. "
                    + "Using max value." + ", specified=" + amVCores
                    + ", max=" + maxVCores);
            amVCores = maxVCores;
        }


        /*
        Application 실행 요청 3 - ApplicationSubmissionContext 설정
        - ApplicationSubmissionContext → RM에 제출할 Application 정보 포함
            - Application ID, 이름, 우선순위, 큐, 필요한 리소스, AM 컨테이너 정보 등
         */
        // set the application name
        // Yarn을 제출할 때 사용할 context 객체 하나 만들고
        ApplicationSubmissionContext appContext = app.getApplicationSubmissionContext();
        ApplicationId appId = appContext.getApplicationId();
        appContext.setApplicationName(appName);

        // AM이 요구하는 메모리와 vCores를 지정 - YARN ResourceManager가 스케줄링 시 참고
        // Set up resource type requirements
        // For now, both memory and vcores are supported, so we set memory and vcores requirements
        Resource capability = Records.newRecord(Resource.class);
        capability.setMemorySize(amMemory);
        capability.setVirtualCores(amVCores);
        appContext.setResource(capability);

        // Set the priority for the application master
        // AM 우선순위 설정 → 큐 스케줄링에 영향
        Priority priority = Records.newRecord(Priority.class);
        priority.setPriority(amPriority);
        appContext.setPriority(priority);

        // Set the queue to which this application is to be submitted in the RM
        // 제출할 YARN 큐 지정 → multi-tenant 환경에서 중요
        appContext.setQueue(amQueue);

        // AM 컨테이너 런치 스펙 지정
        // Set the ContainerLaunchContext to describe the Container with which the ApplicationMaster is launched.
        // 실제 AM을 띄우는 컨테이너의 스펙 지정 - 실행할 JAR, 환경 변수, 클래스패스, 명령어
        // getAMContainerSpec()는 이 컨테이너를 정의하는 메소드
        appContext.setAMContainerSpec(getAMContainerSpec(appId.getId()));

        /*
        YARN 구조를 보면
        1. 클라이언트 → AM 제출 요청
        2. ResourceManager(RM) → AM 실행 컨테이너 스케줄링
        3. AM → 실제 작업(컨테이너)을 요청하고 실행
        즉, 클라이언트가 직접 AM을 띄우는 게 아니라, YARN RM에 "이 AM 실행해 줘"라고 요청하는 단계
         */
        // AM을 YARN에 제출
        // Submit the application to the applications manager
        // SubmitApplicationResponse submitResp = applicationsManager.submitApplication(appRequest);
        // Ignore the response as either a valid response object is returned on success
        // or an exception thrown to denote some form of a failure

        /*
        Application 실행 요청 4 -  Application 제출
        - yarnClient.submitApplication(appContext) : 실제 RM에 Application 제출 → AM 실행 요청

        Application Master 실행 요청 단계 1 - 어플리케이션 등록 및 AM 컨테이너 요청
        - RMAppManager가 내부 스케줄러에게 AM 실행 컨테이너 요청
        - 클라이언트 코드 : yarnClient.submitApplication(appContext)
        - RM 내부: RMAppManager.handle()
         */

        System.out.println("Submitting application to ASM");
        yarnClient.submitApplication(appContext);



        // Monitor the application
        // 모니터는 임시로 구현한 것
        // appId : 모니터링할 애플리케이션의 YARN Application ID
        return monitorApplication(appId);
    }

    private ContainerLaunchContext getAMContainerSpec(int appId) throws IOException, YarnException {

        // Set up the container launch context for the application master
        // YARN이 컨테이너를 실행할 때 참조하는 모든 정보가 이 객체에 담김
        ContainerLaunchContext amContainer = Records.newRecord(ContainerLaunchContext.class);

        // AM JAR 파일 등 리소스를 복사하고 접근하기 위함
        FileSystem fs = FileSystem.get(conf);


        // set local resources for the application master (localResources → AM 컨테이너에서 사용할 파일/아카이브 목록)
        // local files or archives as needed
        // In this scenario, the jar file for the application master is part of the local resources
        Map<String, LocalResource> localResources = new HashMap<String, LocalResource>();

        System.out.println("Copy App Master jar from local filesystem and add to local environment");
        // addToLocalResources → HDFS에 JAR 파일을 복사하고 YARN이 접근할 수 있는 리소스 객체 생성
        // Copy the application master jar to the filesystem
        // Create a local resource to point to the destination jar path
        addToLocalResources(fs, appMasterJarPath, Constants.AM_JAR_NAME, appId, localResources, null);

        // 최종적으로 amContainer.setLocalResources()로 컨테이너에 등록
        // Set local resource info into app master container launch context
        amContainer.setLocalResources(localResources);


        // AM 컨테이너에서 실행될 환경 변수 설정 (CLASSPATH, PATH, JAVA_HOME 등 )
        // YARN 에서 컨테이너가 시작될 때 환경 변수로 사용
        // Set the env variables to be setup in the env where the application master will be run
        System.out.println("Set the environment for the application master");
        amContainer.setEnvironment(getAMEnvironment(localResources, fs));


        // 실행 명령어(command) 구성 - 실행할 Java 명령어를 순서대로 추가
        // Set the necessary command to execute the application master
        Vector<CharSequence> vargs = new Vector<CharSequence>(30);
        // Set java executable command
        System.out.println("Setting up app master command");
        vargs.add(Environment.JAVA_HOME.$$() + "/bin/java");  //java 실행
        // Set Xmx based on am memory size
        vargs.add("-Xmx" + amMemory + "m"); // 힙 메모리(-Xmx)
        // Set class name - AM 클래스 이름
        vargs.add("de.example.hadoop.yarn.MyApplicationMaster");
        // Set params for Application Master
        vargs.add("--container_memory " + String.valueOf(containerMemory));
        vargs.add("--container_vcores " + String.valueOf(containerVirtualCores));
        vargs.add("--num_containers " + String.valueOf(numContainers));
        vargs.add("--priority " + String.valueOf(requestPriority));

        // 표준 출력/오류 리디렉션 → YARN 로그 디렉토리(LOG_DIR_EXPANSION_VAR)로 저장
        // 1>,2> 리눅스 redirect를 써서 요 경로에다가 해줘라
        // LOG_DIR_EXPANSION_VAR 는 어플리케이션 마스터가 어느 노드에 뜰지 리소스 매니저랑 노드 매니저가 통신하면서 서로 결정하는데
        // 결정된 context가 있는 그 경로로 돌려 봐야 정해지는 경로다.
        vargs.add("1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/AppMaster.stdout");
        vargs.add("2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/AppMaster.stderr");


        // Get final command - 최종 명령어 문자열 생성
        // Vector에 담긴 명령어를 한 줄로 합침
        StringBuilder command = new StringBuilder();
        for (CharSequence str : vargs) {
            command.append(str).append(" ");
        }

        System.out.println("Completed setting up app master command " + command.toString());
        List<String> commands = new ArrayList<String>();
        commands.add(command.toString());

        // amContainer.setCommands()에 전달 → 컨테이너 실행 시 YARN이 실행
        amContainer.setCommands(commands);
        return amContainer;
    }


    // 로컬 파일을 HDFS(또는 YARN이 접근 가능한 파일시스템)에 복사
    // LocalResource 객체를 생성하여 컨테이너 런치 컨텍스트에 등록
    private void addToLocalResources(FileSystem fs, String fileSrcPath,
                                     String fileDstPath, int appId, Map<String, LocalResource> localResources,
                                     String resources) throws IOException {

        // HDFS 에서 저장될 경로 생성
        // 예시 ) "/user/username/HelloYarn/123/AppMaster.jar"
        String suffix = appName + "/" + appId + "/" + fileDstPath; // suffix : n. 접미사
        Path dst = new Path(fs.getHomeDirectory(), suffix);


        // 컨테이너가 실행될 때 참조할 수 있는 HDFS 리소스 확보 - 파일 생성 또는 복사
        if (fileSrcPath == null) {
            // null 이면 resources 문자열 내용을 HDFS에 직접 씀
            FSDataOutputStream ostream = null;
            try {
                ostream = FileSystem.create(fs, dst, new FsPermission((short) 0710)); // 파일 생성 후 권한 (0710) 설정
                ostream.writeUTF(resources);
            } finally {
                IOUtils.closeQuietly(ostream);
            }
        } else {
            // null 아니면 로컬 파일을 HDFS로 복사
            fs.copyFromLocalFile(new Path(fileSrcPath), dst);
        }

        // LocalResource → YARN 컨테이너가 접근할 수 있는 파일/아카이브 메타 정보
        FileStatus scFileStatus = fs.getFileStatus(dst);
        LocalResource scRsrc =
                LocalResource.newInstance(
                        URL.fromURI(dst.toUri()), // URL - HDFS 경로
                        LocalResourceType.FILE, // FILE, ARCHIVE, PATTERN 등
                        LocalResourceVisibility.APPLICATION, // APPLICATION, NODE, PUBLIC (접근 범위)
                        scFileStatus.getLen(), // 파일 크기
                        scFileStatus.getModificationTime()); // 마지막 수정 시간
        // 맵에 등록
        // fileDstPath를 키로 사용하여 컨테이너 런치 컨텍스트에 등록
        // AM 컨테이너가 시작될 때 이 리소스들을 자동으로 로컬 디렉토리에 복사하고 사용 가능
        localResources.put(fileDstPath, scRsrc);
    }

    // 반환값: Map<String, String> → 컨테이너에서 사용할 환경 변수(key=value)
    private Map<String, String> getAMEnvironment(Map<String, LocalResource> localResources, FileSystem fs)
            throws IOException {

        // AM 컨테이너에서 사용할 환경 변수 저장
        Map<String, String> env = new HashMap<String, String>();

        // Set ApplicationMaster jar file
        LocalResource appJarResource = localResources.get(Constants.AM_JAR_NAME);
        Path hdfsAppJarPath = new Path(fs.getHomeDirectory(), appJarResource.getResource().getFile());
        FileStatus hdfsAppJarStatus = fs.getFileStatus(hdfsAppJarPath);
        long hdfsAppJarLength = hdfsAppJarStatus.getLen();
        long hdfsAppJarTimestamp = hdfsAppJarStatus.getModificationTime();

        env.put(Constants.AM_JAR_PATH, hdfsAppJarPath.toString());
        env.put(Constants.AM_JAR_TIMESTAMP, Long.toString(hdfsAppJarTimestamp));
        env.put(Constants.AM_JAR_LENGTH, Long.toString(hdfsAppJarLength));


        // AM 컨테이너에서 Java가 실행될 때 필요한 클래스 경로 지정
        // Add AppMaster.jar location to classpath
        // At some point we should not be required to add the hadoop specific classpaths to the env.
        // It should be provided out of the box.
        // For now setting all required classpaths including the classpath to "." for the application jar
        StringBuilder classPathEnv = new StringBuilder(Environment.CLASSPATH.$$()) // Environment.CLASSPATH.$$() → 기본 Hadoop/Java 클래스 경로
                .append(ApplicationConstants.CLASS_PATH_SEPARATOR).append("./*"); //  "./*" → 현재 디렉토리(AM JAR 포함)

        for (
                String c : conf.getStrings(
                    YarnConfiguration.YARN_APPLICATION_CLASSPATH, // yarn-site.xml에서 지정된 YARN 관련 클래스 경로
                    YarnConfiguration.DEFAULT_YARN_CROSS_PLATFORM_APPLICATION_CLASSPATH // 기본 Hadoop/YARN 라이브러리 경로
                )
        ){
            classPathEnv.append(ApplicationConstants.CLASS_PATH_SEPARATOR);
            classPathEnv.append(c.trim());
        }

        // CLASSPATH 환경 변수에 등록 → AM JVM 실행 시 사용
        env.put("CLASSPATH", classPathEnv.toString());

        return env;
    }




    /**
     * Monitor the submitted application for completion.
     * Kill application if time expires.
     * @param appId Application ID of application to be monitored
     * @return true if application completed successfully
     * @throws org.apache.hadoop.yarn.exceptions.YarnException
     * @throws java.io.IOException
     */
    // monitorApplication() 메소드는 클라이언트가 YARN에 제출한 애플리케이션 상태를 실시간으로 모니터링하고,
    // 필요하면 타임아웃 시 강제 종료시킴.
    private boolean monitorApplication(ApplicationId appId)
            throws YarnException, IOException {
        // application 끝날 때 까지 무한 루프.
        while (true) {
            // Check app status every 1 second.
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.err.println("Thread sleep in monitoring loop interrupted");
            }

            // Get application report for the appId we are interested in

            /*
            Application 실행 요청 5 -  Application 상태 모니터링
            - 클라이언트는 ClientRMService 의 getApplicationReport로 ResourceManager에게 ApplicationReport 를 요청한다.
                ApplicationReport는 Yarn 클러스터에서 실행되는 어플리케이션의 통계정보를 담고 있다
            - yarnClient.getApplicationReport(appId) : 상태 모니터링
             */
            ApplicationReport report = yarnClient.getApplicationReport(appId); // ApplicationReport : app 현재 상태, 리소스 사용량, AM 상태 등 제공
            YarnApplicationState state = report.getYarnApplicationState(); // YARN 수준 상태 - 예: NEW, RUNNING, FINISHED, KILLED, FAILED
            FinalApplicationStatus dsStatus = report.getFinalApplicationStatus(); // AM 수준 최종 상태 - 예: SUCCEEDED, FAILED, KILLE
            if (YarnApplicationState.FINISHED == state) {
                if (FinalApplicationStatus.SUCCEEDED == dsStatus) {
                    System.out.println("Application has completed successfully. "
                            + " Breaking monitoring loop : ApplicationId:" + appId.getId());
                    return true;
                } else {
                    System.out.println("Application did finished unsuccessfully."
                            + " YarnState=" + state.toString() + ", DSFinalStatus=" + dsStatus.toString()
                            + ". Breaking monitoring loop : ApplicationId:" + appId.getId());
                    return false;
                }
            } else if (YarnApplicationState.KILLED == state
                    || YarnApplicationState.FAILED == state) {
                System.out.println("Application did not finish."
                        + " YarnState=" + state.toString() + ", DSFinalStatus=" + dsStatus.toString()
                        + ". Breaking monitoring loop : ApplicationId:" + appId.getId());
                return false;
            }
            // (시작된 시간 + 타임아웃된 시간)이 (현재시간)보다 크다면 타임아웃이 지난 것
            if (System.currentTimeMillis() > (clientStartTime + clientTimeout)) {
                System.out.println("Reached client specified timeout for application. Killing application"
                        + ". Breaking monitoring loop : ApplicationId:" + appId.getId());
                forceKillApplication(appId);
                return false;
            }
        }
    }

    /**
     * Kill a submitted application by sending a call to the ASM
     * @param appId Application ID to be killed.
     * @throws org.apache.hadoop.yarn.exceptions.YarnException
     * @throws java.io.IOException
     */
    private void forceKillApplication(ApplicationId appId)
            throws YarnException, IOException {
        yarnClient.killApplication(appId);

    }

    /**
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        boolean result = false;
        try {
            MyClient client = new MyClient();
            System.out.println("Initializing Client");
            try {
                boolean doRun = client.init(args);
                if (!doRun) {
                    System.exit(0);
                }
            } catch (IllegalArgumentException e) {
                System.err.println(e.getLocalizedMessage());
                client.printUsage();
                System.exit(-1);
            }
            result = client.run();
        } catch (Throwable t) {
            System.err.println("Error running Client\n" + t.getMessage() + Arrays.toString(t.getStackTrace()));
            System.exit(1);
        }
        if (result) {
            System.out.println("Application completed successfully");
            System.exit(0);
        }
        System.err.println("Application failed to complete successfully");
        System.exit(2);
    }
}