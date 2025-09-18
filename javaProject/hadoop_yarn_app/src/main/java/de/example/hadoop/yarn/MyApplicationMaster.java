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

        CommandLine cliParser = new GnuParser().parse(opts, args);

        Map<String, String> envs = System.getenv();

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

        System.out.println("Application master for app" + ", appId="
                + appAttemptID.getApplicationId().getId() + ", clusterTimestamp="
                + appAttemptID.getApplicationId().getClusterTimestamp()
                + ", attemptId=" + appAttemptID.getAttemptId());

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
        AMRMClient<ContainerRequest> amRMClient = AMRMClient.createAMRMClient();
        amRMClient.init(conf);
        amRMClient.start();

        // Register with ResourceManager
        amRMClient.registerApplicationMaster("", 0, "");

        // Set up resource type requirements for Container
        Resource capability = Records.newRecord(Resource.class);
        capability.setMemorySize(containerMemory);
        capability.setVirtualCores(containerVirtualCores);

        // Priority for worker containers - priorities are intra-application
        Priority priority = Records.newRecord(Priority.class);
        priority.setPriority(requestPriority);

        // Make container requests to ResourceManager
        for (int i = 0; i < numTotalContainers; ++i) {
            ContainerRequest containerAsk = new ContainerRequest(capability, null, null, priority);
            amRMClient.addContainerRequest(containerAsk);
        }

        NMClient nmClient = NMClient.createNMClient();
        nmClient.init(conf);
        nmClient.start();

        // Setup CLASSPATH for Container
        Map<String, String> containerEnv = new HashMap<String, String>();
        containerEnv.put("CLASSPATH", "./*");

        // Setup ApplicationMaster jar file for Container
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
            for (Container container : response.getAllocatedContainers()) {
                allocatedContainers++;

                ContainerLaunchContext appContainer = createContainerLaunchContext(appMasterJar, containerEnv);
                System.out.println("Launching container " + container.getId().toString());
                System.out.println("Allocated containers " + allocatedContainers);

                nmClient.startContainer(container, appContainer);
            }
            for (ContainerStatus status : response.getCompletedContainersStatuses()) {
                ++completedContainers;
                System.out.println("ContainerID:" + status.getContainerId() + ", state:" + status.getState().name());
            }
            // 무한루프다 보니 어플리케이션 마스터가 리소스 매니저나 노드매니저한테 엄청 요청을 많이 보냄. 그러면 시스템에 부하가 오니까 sleep 넣어줌.
            Thread.sleep(100);
        }

        // 다 끝날 때까지 반복.
        // Now wait for the remaining containers to complete
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
        amRMClient.unregisterApplicationMaster(
                FinalApplicationStatus.SUCCEEDED, "", "");
        System.out.println("Finished MyApplicationMaster");
    }

    private LocalResource createAppMasterJar() throws IOException {
        LocalResource appMasterJar = Records.newRecord(LocalResource.class);
        if (!appJarPath.isEmpty()) {
            appMasterJar.setType(LocalResourceType.FILE);
            Path jarPath = new Path(appJarPath);
            jarPath = FileSystem.get(conf).makeQualified(jarPath);
            appMasterJar.setResource(URL.fromPath(jarPath));
            appMasterJar.setTimestamp(appJarTimestamp);
            appMasterJar.setSize(appJarPathLen);
            appMasterJar.setVisibility(LocalResourceVisibility.PUBLIC);
        }
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
                                                                Map<String, String> containerEnv) {
        ContainerLaunchContext appContainer =
                Records.newRecord(ContainerLaunchContext.class);
        appContainer.setLocalResources(
                Collections.singletonMap(Constants.AM_JAR_NAME, appMasterJar));
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
