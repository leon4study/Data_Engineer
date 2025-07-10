package de.monitoring.prometheus;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class ApiController {
    private static final String template = "hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    static final Counter requests = Counter.build()
            .name("requests_total")
            .help("Total requests.")
            .labelNames("method", "api")
            .register();

    static final Histogram remoteSiteLatency = Histogram.build()
            .name("remote_website_latency_seconds")
            .help("Request latency in seconds.")
            .labelNames("site")
            .buckets(0.1, 0.5, 0.8, 0.9, 0.95, 0.99, 1)
            .register();


    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        requests.labels("get","/greeting").inc();
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    // 무한루푸 돌게 하는 것. 엄청 많이.
    @GetMapping("/cpurun")
    public ResponseEntity<String> runCpu(
            @RequestParam(value = "divider", defaultValue = "65536") long divier){
        requests.labels("get","/cpurun").inc();
        long counter = 0;
        do {
            counter ++;
        }while (counter < (Long.MAX_VALUE / divier));
        return ResponseEntity.ok("finished");
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<String> getUser(@PathVariable String userId) {
        requests.labels("get","/users/userId").inc();
        return ResponseEntity.ok("user_id: " + userId);
    }

    @GetMapping("/website")
    public ResponseEntity<String> getSite( //site 라는 쿼리스트링으로 들어오는 웹사이트를 get 으로 한 번 찔러 보는 것
            @RequestParam(value = "site", defaultValue = "http://www.example.com") String site){
        requests.labels("get","/website").inc();
        RestTemplate restTemplate = new RestTemplate();
        System.out.println("site: " + site);
        try (Histogram.Timer requestTimer = remoteSiteLatency.labels(site).startTimer()){
            String response = restTemplate.getForObject(site, String.class);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.unprocessableEntity().body(e.getMessage() + e.getStackTrace());
        }
    }
}
