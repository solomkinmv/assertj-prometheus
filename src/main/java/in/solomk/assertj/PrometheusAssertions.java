package in.solomk.assertj;

public class PrometheusAssertions {

    private PrometheusAssertions() {
    }

    public static PrometheusAssert assertThat(String actual) {
        return new PrometheusAssert(actual);
    }
}
