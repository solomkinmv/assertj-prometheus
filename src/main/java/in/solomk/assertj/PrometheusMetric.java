package in.solomk.assertj;

import java.util.Set;

public record PrometheusMetric(String name,
                               double value,
                               Set<PrometheusTag> tags) {
}
