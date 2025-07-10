package de.monitoring.prometheus;

import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class PrometheusMonitoringAppApplication {
    public static void main(String[] args) throws IOException {
        DefaultExports.initialize(); //핫스팟에 있는 디폴트익스포트 실행하면
        //내부적으로 collectRegistry.defaultRegistry 불러와서 매트릭 남긴 걸 어디에 저장을 해야되는데 그 저장소가 Registry 객체라고 보면 됨.
        //한 줄만 넣어주면 디폴트 레지스트리에 hotspot 에서 JVM에서 수집할 수 있는 정보들을 다 넣어준다고 보면 됨.

        HTTPServer server = new HTTPServer.Builder().withPort(1234).build();
        // 위에서 수집한 정보를 웹에서 스크랩 할 수 있게 만든 건데, HTTPServer 가 프로메테우스 httpserver다.
        // build() 하면 안에 start() 함수 있어서 알아서 실행됨

        SpringApplication.run(PrometheusMonitoringAppApplication.class, args);
    }
}
