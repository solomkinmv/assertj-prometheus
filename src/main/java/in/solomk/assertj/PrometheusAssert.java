package in.solomk.assertj;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.internal.Booleans;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PrometheusAssert extends AbstractAssert<PrometheusAssert, String> {

    private final List<PrometheusMetric> metrics;
    private final Booleans booleans = Booleans.instance();

    public PrometheusAssert(String actual) {
        super(actual, PrometheusAssert.class);
        metrics = parsePrometheusMetrics(actual);
    }

    public PrometheusAssert hasMetric(String metricName,
                                      List<PrometheusTag> expectedTags) {
        hasMetric(metricName, expectedTags, actualValue -> true);
        return this;
    }

    public PrometheusAssert hasMetric(String metricName,
                                      List<PrometheusTag> expectedTags,
                                      Predicate<Double> expectedValuePredicate) {
        var matched = metrics.stream()
                             .anyMatch(metric -> metricMatches(metric, metricName, expectedTags, expectedValuePredicate));
        booleans.assertEqual(info, matched, true);
        return this;
    }

    private boolean metricMatches(PrometheusMetric actualMetric,
                                  String metricName,
                                  List<PrometheusTag> expectedTags,
                                  Predicate<Double> expectedValuePredicate) {
        return metricMatches(actualMetric, metricName, expectedTags) && expectedValuePredicate.test(actualMetric.value());
    }

    private boolean metricMatches(PrometheusMetric actualMetric, String metricName, List<PrometheusTag> expectedTags) {
        if (!actualMetric.name().equals(metricName)) return false;

        return actualMetric.tags()
                           .containsAll(expectedTags);
    }

    private static List<PrometheusMetric> parsePrometheusMetrics(String metrics) {
        return Arrays.stream(metrics.split("\n"))
                     .map(PrometheusAssert::parseMetricLine)
                     .filter(Optional::isPresent)
                     .map(Optional::get)
                     .collect(Collectors.toList());
    }

    private static Optional<PrometheusMetric> parseMetricLine(String line) {
        if (line.charAt(0) == '#') return Optional.empty();
        int labelsStart = line.indexOf('{');
        int labelsEnd = line.lastIndexOf("}");

        double value = Double.parseDouble(line.substring(labelsEnd + 2));
        String metricName = line.substring(0, labelsStart);
        String tagsLine = line.substring(labelsStart + 1, labelsEnd);
        Set<PrometheusTag> tags = Stream.of(tagsLine.split(","))
                                        .map(PrometheusAssert::parseTag)
                                        .collect(Collectors.toSet());
        return Optional.of(new PrometheusMetric(metricName, value, tags));
    }

    private static PrometheusTag parseTag(String tagLine) {
        String tagName = tagLine.substring(0, tagLine.indexOf('='));
        String tagValue = tagLine.substring(tagName.length() + 2, tagLine.length() - 1);
        return new PrometheusTag(tagName, tagValue);
    }

}
