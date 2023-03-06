package in.solomk.assertj;

import org.junit.jupiter.api.Test;

import java.util.List;

import static in.solomk.assertj.PrometheusAssertions.assertThat;

public class PrometheusAssertUsabilityTest {

    private static final String PROMETHEUS_RESPONSE = """
            # HELP http_requests comment section
            http_requests_total{method="GET",endpoint="/api/users",status="200"} 13
            http_requests_total{method="POST",endpoint="/api/users",status="200"} 4
            http_response_time_bucket{method="GET",endpoint="/api/users",status="200",le="0.1"} 5
            http_response_time_bucket{method="GET",endpoint="/api/users",status="200",le="0.5"} 10
            http_response_time_bucket{method="GET",endpoint="/api/users",status="200",le="1"} 12
            http_response_time_bucket{method="GET",endpoint="/api/users",status="200",le="+Inf"} 13
            http_response_time_sum{method="GET",endpoint="/api/users",status="200"} 1.23456789E9
            http_response_time_count{method="GET",endpoint="/api/users",status="200"} 13
            cpu_usage{cpu="cpu0"} 0.2
            cpu_usage{cpu="cpu1"} 0.3
            # TYPE kafka_producer_record_send_total counter
            kafka_producer_record_send_total{kafka_version="3.1.2",operation="PATCH /path/{param:.+}",label_with_dots="some.value-1",} 5.0
            """;

    @Test
    void exampleTest() {
        assertThat(PROMETHEUS_RESPONSE)
                .hasMetric("http_response_time_bucket",
                           List.of(new PrometheusTag("method", "GET")))
                .hasMetric("http_response_time_bucket",
                           List.of(new PrometheusTag("method", "GET"),
                                   new PrometheusTag("endpoint", "/api/users")),
                           value -> value == 10)
                .hasMetric("kafka_producer_record_send_total",
                           List.of(),
                           value -> value > 4.5); // todo: filter by name and print output like "expected metric with name 'kafka_producer_record_send_total' to have value > 9, but was 5"
    }
}
