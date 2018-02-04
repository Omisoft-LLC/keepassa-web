package com.omisoft.keepassa.common;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.graphite.PickledGraphite;
import com.omisoft.keepassa.configuration.FileConfigService;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

/**
 * Created by dido on 10.06.16.
 */
@Slf4j
public class MetricsService {

  private static final String GRAPHITE_SERVER = "graphite.omisoft.eu";

  public static void initMetrics(String appName) {
    log.info("INITING METRICS");
    if (!FileConfigService.getInstance().isDev()) {
      MetricRegistry registry =
          SharedMetricRegistries.getOrCreate("metricsRegistry");
      final JmxReporter jmxreporter = JmxReporter.forRegistry(registry).build();
      jmxreporter.start();

      final Slf4jReporter logreporter = Slf4jReporter.forRegistry(registry)
          .outputTo(LoggerFactory.getLogger("com.omisoft.metrics." + appName))
          .convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).build();
      logreporter.start(5, TimeUnit.MINUTES);
      try {
        final PickledGraphite pickledGraphite =
            new PickledGraphite(new InetSocketAddress(GRAPHITE_SERVER, 2004));
        final GraphiteReporter reporter =
            GraphiteReporter.forRegistry(registry).prefixedWith(appName + ".omibox.eu")
                .convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL).build(pickledGraphite);
        reporter.start(1, TimeUnit.MINUTES);
      } catch (Exception e) {
        e.printStackTrace();
        log.error("ERROR IN METRICS", e);
      }
    }
  }
}
