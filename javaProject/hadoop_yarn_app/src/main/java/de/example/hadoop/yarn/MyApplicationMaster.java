package de.example.hadoop.yarn;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.ExitUtil;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.URL;
import org.apache.hadoop.yarn.client.api.AMRMClient;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.apache.hadoop.yarn.client.api.NMClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.util.Records;
import org.apache.log4j.LogManager;

public class MyApplicationMaster {

    // Application Attempt ID ( combination of attemptId and fail count )
    protected ApplicationAttemptId appAttemptID;

    // No. of containers to run shell command on
    private int numTotalContainers = 1;

    // Memory to request for the container on which the shell command will run
    private int containerMemory = 10;

    // VirtualCores to request for the container on which the shell command will run
    private int containerVirtualCores = 1;

    // Priority of the request
    private int requestPriority;

    // Location of shell script ( obtained from info set in env )
    // Shell script path in fs
    private String appJarPath = "";
    // Timestamp needed for creating a local resource
    private long appJarTimestamp = 0;
    // File length needed for local resource
    private long appJarPathLen = 0;

    // Configuration
    private Configuration conf;

    public MyApplicationMaster() {
        // Set up the configuration
        // YARN 관련 환경 변수, 디폴트 설정, 클러스터 정보 등을 읽을 수 있음
        // AM 내부에서 FileSystem, 리소스 요청, 컨테이너 환경 설정 등에 사용
        conf = new YarnConfiguration();
    }

    /**
     * Parse command line options
     *
     * @param args Command line args
     * @return Whether init successful and run should be invoked
     * @throws org.apache.commons.cli.ParseException
     * @throws java.io.IOException
     */
    public boolean init(String[] args) throws Exception {
        Options opts = new Options();
        opts.addOption("app_attempt_id", true,
                "App Attempt ID. Not to be used unless for testing purposes");
        opts.addOption("shell_env", true,
                "Environment for shell script. Specified as env_key=env_val pairs");
        opts.addOption("container_memory", true,
                "Amount of memory in MB to be requested to run the shell command");
        opts.addOption("container_vcores", true,
                "Amount of virtual cores to be requested to run the shell command");
        opts.addOption("num_containers", true,
                "No. of containers on which the shell command needs to be executed");
        opts.addOption("priority", true, "Application Priority. Default 0");
        opts.addOption("help", false, "Print usage");

        // 커맨드라인 파싱
        // 파라미터 문자열을 분석 → cliParser.getOptionValue()로 접근 가능
        CommandLine cliParser = new GnuParser().parse(opts, args);

        // 환경변수 읽어오기
        Map<String, String> envs = System.getenv();

        // YARN 컨테이너가 실행될 때 NodeManager가 전달한 환경 변수 확인
        // AM 실행 시 CONTAINER_ID 확인 + ApplicationAttemptId 생성하고
        // 없으면 테스트용 app_attempt_id 사용
        // AM이 어느 시도(attempt)인지 식별 → 이후 컨테이너 요청, 리포트 제출에 필요

        if (!envs.containsKey(ApplicationConstants.Environment.CONTAINER_ID.name())) {
            if (cliParser.hasOption("app_attempt_id")) {
                String appIdStr = cliParser.getOptionValue("app_attempt_id", "");
                appAttemptID = ApplicationAttemptId.fromString(appIdStr);
            } else {
                throw new IllegalArgumentException(
                        "Application Attempt Id not set in the environment");
            }
        } else {
            ContainerId containerId = ContainerId.fromString(
                    envs.get(ApplicationConstants.Environment.CONTAINER_ID.name()));
            appAttemptID = containerId.getApplicationAttemptId();
        }

        if (!envs.containsKey(ApplicationConstants.APP_SUBMIT_TIME_ENV)) {
            throw new RuntimeException(
                    ApplicationConstants.APP_SUBMIT_TIME_ENV + " not set in the environment");
        }
        if (!envs.containsKey(ApplicationConstants.Environment.NM_HOST.name())) {
            throw new RuntimeException(
                    ApplicationConstants.Environment.NM_HOST.name() + " not set in the environment");
        }
        if (!envs.containsKey(ApplicationConstants.Environment.NM_HTTP_PORT.name())) {
            throw new RuntimeException(
                    ApplicationConstants.Environment.NM_HTTP_PORT + " not set in the environment");
        }
        if (!envs.containsKey(ApplicationConstants.Environment.NM_PORT.name())) {
            throw new RuntimeException(
                    ApplicationConstants.Environment.NM_PORT.name() + " not set in the environment");
        }

        // AM JAR 파일 정보 확인
        // MyClient.java - getAMEnvironment()에서 전달한 환경 변수 참조
        // JAR 파일 경로, 길이, 타임스탬프를 읽어 컨테이너에서 AM JAR 실행 준비
        if (envs.containsKey(Constants.AM_JAR_PATH)) {
            appJarPath = envs.get(Constants.AM_JAR_PATH);

            if (envs.containsKey(Constants.AM_JAR_TIMESTAMP)) {
                appJarTimestamp = Long.valueOf(envs.get(Constants.AM_JAR_TIMESTAMP));
            }
            if (envs.containsKey(Constants.AM_JAR_LENGTH)) {
                appJarPathLen = Long.valueOf(envs.get(Constants.AM_JAR_LENGTH));
            }

            if (!appJarPath.isEmpty() && (appJarTimestamp <= 0 || appJarPathLen <= 0)) {
                System.err.println("Illegal values in env for shell script path" + ", path="
                        + appJarPath + ", len=" + appJarPathLen + ", timestamp=" + appJarTimestamp);
                throw new IllegalArgumentException(
                        "Illegal values in env for shell script path");
            }
        }

        // 초기 정보 출력
        // 디버깅 및 로그용, 현재 실행 중인 AM의 ID/시도 정보 출력
        System.out.println("Application master for app" + ", appId="
                + appAttemptID.getApplicationId().getId() + ", clusterTimestamp="
                + appAttemptID.getApplicationId().getClusterTimestamp()
                + ", attemptId=" + appAttemptID.getAttemptId());

        // 커맨드라인 옵션 적용
        containerMemory = Integer.parseInt(cliParser.getOptionValue("container_memory", "10"));
        containerVirtualCores = Integer.parseInt(cliParser.getOptionValue("container_vcores", "1"));
        numTotalContainers = Integer.parseInt(cliParser.getOptionValue("num_containers", "1"));
        if (numTotalContainers == 0) {
            throw new IllegalArgumentException("Cannot run MyAppliCationMaster with no containers");
        }
        requestPriority = Integer.parseInt(cliParser.getOptionValue("priority", "0"));

        return true;
    }


    /**
     * Main run function for the application master
     *
     * @throws org.apache.hadoop.yarn.exceptions.YarnException
     * @throws java.io.IOException
     */
    @SuppressWarnings({ "unchecked" })
    public void run() throws Exception {
        System.out.println("Running MyApplicationMaster");

        // Initialize clients to ResourceManager and NodeManagers
        // AMRMClient → AM이 ResourceManager(RM)와 통신할 수 있게 해주는 클라이언트
        AMRMClient<ContainerRequest> amRMClient = AMRMClient.createAMRMClient();
        amRMClient.init(conf);
        // 초기화 후 start() 호출 → RM과 연결 준비 완료
        amRMClient.start();

        // Register with ResourceManager
        // AM 자신을 RM에 등록
        // 인자로 전달되는 값: 호스트, RPC 포트, 추적 URL - 지금 예제에서는 모두 빈 문자열/0으로 설정 → 기본 등록
        amRMClient.registerApplicationMaster("", 0, "");

        // Set up resource type requirements for Container
        // 각 컨테이너에서 요구되는 메모리, CPU 코어 설정
        Resource capability = Records.newRecord(Resource.class);
        capability.setMemorySize(containerMemory);
        capability.setVirtualCores(containerVirtualCores);

        // Priority for worker containers - priorities are intra-application
        // 동일 애플리케이션 내 컨테이너 우선순위 지정
        Priority priority = Records.newRecord(Priority.class);
        priority.setPriority(requestPriority);

        // Make container requests to ResourceManager
        // RM에게 컨테이너 요청을 등록 - 요청된 수만큼 반복하여 numTotalContainers 생성
        for (int i = 0; i < numTotalContainers; ++i) {
            ContainerRequest containerAsk = new ContainerRequest(capability, null, null, priority);
            amRMClient.addContainerRequest(containerAsk);
        }

        // 컨테이너를 실제 노드에서 실행시키는 NodeManager와 통신
        // AM → NM : 컨테이너 런칭 명령 전송
        NMClient nmClient = NMClient.createNMClient();
        nmClient.init(conf);
        nmClient.start();

        // Setup CLASSPATH for Container
        // 각 컨테이너 실행 환경 변수 설정  - CLASSPATH 지정 → 애플리케이션 클래스, 의존 라이브러리 포함
        Map<String, String> containerEnv = new HashMap<String, String>();
        containerEnv.put("CLASSPATH", "./*");

        // Setup ApplicationMaster jar file for Container
        // AM JAR 파일을 LocalResource로 등록 → 컨테이너에서 실행 가능하게 함
        LocalResource appMasterJar = createAppMasterJar();



        // Obtain allocated containers and launch
        int allocatedContainers = 0;
        // We need to start counting completed containers while still allocating
        // them since initial ones may complete while we're allocating subsequent
        // containers and if we miss those notifications, we'll never see them again
        // and this ApplicationMaster will hang indefinitely.
        int completedContainers = 0;


        // 컨테이너를 다 할당 받을 때까지 반복을 함
        while (allocatedContainers < numTotalContainers) {
            AllocateResponse response = amRMClient.allocate(0);
            // RM으로부터 컨테이너 할당을 반복적으로 요청
            for (Container container : response.getAllocatedContainers()) {
                allocatedContainers++;

                // createContainerLaunchContext() → 컨테이너 실행 환경과 명령 설정
                ContainerLaunchContext appContainer = createContainerLaunchContext(appMasterJar, containerEnv);
                System.out.println("Launching container " + container.getId().toString());
                System.out.println("Allocated containers " + allocatedContainers);

                // 실제 컨테이너 실행
                nmClient.startContainer(container, appContainer);
            }
            // 완료된 컨테이너 상태 체크
            for (ContainerStatus status : response.getCompletedContainersStatuses()) {
                ++completedContainers;
                System.out.println("ContainerID:" + status.getContainerId() + ", state:" + status.getState().name());
            }
            // 무한루프다 보니 어플리케이션 마스터가 리소스 매니저나 노드매니저한테 엄청 요청을 많이 보냄. 그러면 시스템에 부하가 오니까 sleep 넣어줌.
            Thread.sleep(100);
        }

        // 다 끝날 때까지 반복.
        // Now wait for the remaining containers to complete
        // 할당 완료 후, 남은 컨테이너 종료까지 대기.
        // AM이 모든 작업 완료 후 종료될 수 있도록 함
        while (completedContainers < numTotalContainers) {
            AllocateResponse response = amRMClient.allocate(completedContainers / numTotalContainers);
            for (ContainerStatus status : response.getCompletedContainersStatuses()) {
                ++completedContainers;
                System.out.println("ContainerID:" + status.getContainerId() + ", state:" + status.getState().name());
            }
            Thread.sleep(100);
        }

        System.out.println("Completed containers:" + completedContainers);

        // Un-register with ResourceManager
        // 모든 컨테이너 작업이 끝나면 AM이 RM에 작업 종료 상태 전달
        amRMClient.unregisterApplicationMaster(
                FinalApplicationStatus.SUCCEEDED, "", "");
        System.out.println("Finished MyApplicationMaster");
    }

    private LocalResource createAppMasterJar() throws IOException {
        // YARN에서는 컨테이너에서 접근할 수 있는 파일/리소스를 LocalResource 객체로 표현
        // AM을 실행할 JAR 파일을 LocalResource로 지정
        LocalResource appMasterJar = Records.newRecord(LocalResource.class);

        // 비어있으면 빈 LocalResource 객체 반환
        if (!appJarPath.isEmpty()) {
            appMasterJar.setType(LocalResourceType.FILE); // FILE → 단일 파일 (JAR)
            Path jarPath = new Path(appJarPath);
            // JAR 파일 경로를 HDFS 또는 로컬 FS 기준으로 절대 경로화
            jarPath = FileSystem.get(conf).makeQualified(jarPath);
            appMasterJar.setResource(URL.fromPath(jarPath)); // setResource() → 컨테이너가 접근할 URL 지정

            //YARN이 파일의 변경 여부 확인과 데이터 무결성 검증에 사용하는 정보- 파일 수정 시간(timestamp)과 크기(size) 지정
            appMasterJar.setTimestamp(appJarTimestamp);
            appMasterJar.setSize(appJarPathLen);

            // 컨테이너 간 리소스 공유 범위 설정 - PUBLIC → 클러스터 모든 노드에서 접근 가능
            appMasterJar.setVisibility(LocalResourceVisibility.PUBLIC);
        }
        // 이 LocalResource 객체를 컨테이너 런치 컨텍스트(ContainerLaunchContext)에 등록하면,
        // AM 컨테이너가 실행될 때 해당 JAR 파일을 사용할 수 있음
        return appMasterJar;
    }



    /**
     * Launch container by create ContainerLaunchContext
     *
     * @param appMasterJar
     * @param containerEnv
     * @return
     */

    private ContainerLaunchContext createContainerLaunchContext(LocalResource appMasterJar,
                                                                Map<String, String> containerEnv){

        // YARN 컨테이너 런치 컨텍스트 객체 생성
        ContainerLaunchContext appContainer = Records.newRecord(ContainerLaunchContext.class);

        // 컨테이너 안에서 접근할 LocalResource 등록
        appContainer.setLocalResources(Collections.singletonMap(Constants.AM_JAR_NAME, appMasterJar));

        // 컨테이너 실행 환경 변수 설정 (CLASSPATH, 필요 시 JAVA_HOME 등)
        appContainer.setEnvironment(containerEnv);

        // 컨테이너 안에서 실행될 어플리케이션도 하나의 어플리케이션이니까 자바 어플리케이션을 실행할 옵션을 커멘드에다가 세팅
        // 최대 메모리 128M, HelloYarn 클래스가 메인이다
        appContainer.setCommands(
                Collections.singletonList(
                        "$JAVA_HOME/bin/java" +
                                " -Xmx128M" +
                                " de.example.hadoop.yarn.HelloYarn" +
                                " 1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout" +
                                " 2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr"
                )
        );

        return appContainer;
    }

    // 여기에 있는 main이 컨테이너에 이 친구가 할당 받아서 Jar의 main class 까지 찾았을 때, 실행되는 함수.
    public static void main(String[] args) throws Exception {
        try {
            MyApplicationMaster appMaster = new MyApplicationMaster();
            System.out.println("Initializing MyApplicationMaster");
            boolean doRun = appMaster.init(args);
            if (!doRun) {
                System.exit(0);
            }
            appMaster.run();
        } catch (Throwable t) {
            System.err.println("Error running MyApplicationMaster\n" + t.getMessage() + Arrays.toString(t.getStackTrace()));
            LogManager.shutdown();
            ExitUtil.terminate(1, t);
        }
    }
}
