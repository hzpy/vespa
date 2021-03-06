// Copyright 2017 Yahoo Holdings. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.
package com.yahoo.vespa.hosted.dockerapi.metrics;

import com.yahoo.metrics.simple.MetricReceiver;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author freva
 */
public class MetricReceiverWrapperTest {
    private static final Dimensions hostDimension = new Dimensions.Builder().add("host", "abc.yahoo.com").build();
    private static final String applicationDocker = MetricReceiverWrapper.APPLICATION_DOCKER;

    @Test
    public void testDefaultValue() {
        MetricReceiverWrapper metricReceiver = new MetricReceiverWrapper(MetricReceiver.nullImplementation);

        metricReceiver.declareCounter(applicationDocker, hostDimension, "some.name");

        assertEquals(metricReceiver.getMetricsForDimension(applicationDocker, hostDimension).get("some.name"), 0L);
    }

    @Test
    public void testSimpleIncrementMetric() {
        MetricReceiverWrapper metricReceiver = new MetricReceiverWrapper(MetricReceiver.nullImplementation);
        CounterWrapper counter = metricReceiver.declareCounter(applicationDocker, hostDimension, "a_counter.value");

        counter.add(5);
        counter.add(8);

        Map<String, Number> latestMetrics = metricReceiver.getMetricsForDimension(applicationDocker, hostDimension);
        assertEquals("Expected only 1 metric value to be set", 1, latestMetrics.size());
        assertEquals(latestMetrics.get("a_counter.value"), 13L); // 5 + 8
    }

    @Test
    public void testSimpleGauge() {
        MetricReceiverWrapper metricReceiver = new MetricReceiverWrapper(MetricReceiver.nullImplementation);
        GaugeWrapper gauge = metricReceiver.declareGauge(applicationDocker, hostDimension, "test.gauge");

        gauge.sample(42);
        gauge.sample(-342.23);

        Map<String, Number> latestMetrics = metricReceiver.getMetricsForDimension(applicationDocker, hostDimension);
        assertEquals("Expected only 1 metric value to be set", 1, latestMetrics.size());
        assertEquals(latestMetrics.get("test.gauge"), -342.23);
    }

    @Test
    public void testRedeclaringSameGauge() {
        MetricReceiverWrapper metricReceiver = new MetricReceiverWrapper(MetricReceiver.nullImplementation);
        GaugeWrapper gauge = metricReceiver.declareGauge(applicationDocker, hostDimension, "test.gauge");
        gauge.sample(42);

        // Same as hostDimension, but new instance.
        Dimensions newDimension = new Dimensions.Builder().add("host", "abc.yahoo.com").build();
        GaugeWrapper newGauge = metricReceiver.declareGauge(applicationDocker, newDimension, "test.gauge");
        newGauge.sample(56);

        assertEquals(metricReceiver.getMetricsForDimension(applicationDocker, hostDimension).get("test.gauge"), 56.);
    }

    @Test
    public void testSameMetricNameButDifferentDimensions() {
        MetricReceiverWrapper metricReceiver = new MetricReceiverWrapper(MetricReceiver.nullImplementation);
        GaugeWrapper gauge = metricReceiver.declareGauge(applicationDocker, hostDimension, "test.gauge");
        gauge.sample(42);

        // Not the same as hostDimension.
        Dimensions newDimension = new Dimensions.Builder().add("host", "abcd.yahoo.com").build();
        GaugeWrapper newGauge = metricReceiver.declareGauge(applicationDocker, newDimension, "test.gauge");
        newGauge.sample(56);

        assertEquals(metricReceiver.getMetricsForDimension(applicationDocker, hostDimension).get("test.gauge"), 42.);
        assertEquals(metricReceiver.getMetricsForDimension(applicationDocker, newDimension).get("test.gauge"), 56.);
    }

    @Test
    public void testDeletingMetric() {
        MetricReceiverWrapper metricReceiver = new MetricReceiverWrapper(MetricReceiver.nullImplementation);
        metricReceiver.declareGauge(applicationDocker, hostDimension, "test.gauge");

        Dimensions differentDimension = new Dimensions.Builder().add("host", "abcd.yahoo.com").build();
        metricReceiver.declareGauge(applicationDocker, differentDimension, "test.gauge");

        assertEquals(2, metricReceiver.getDefaultMetricsRaw().size());
        metricReceiver.deleteMetricByDimension(applicationDocker, differentDimension, MetricReceiverWrapper.DimensionType.DEFAULT);
        assertEquals(1, metricReceiver.getDefaultMetricsRaw().size());
        assertEquals(metricReceiver.getMetricsForDimension(applicationDocker, hostDimension).size(), 1);
        assertEquals(metricReceiver.getMetricsForDimension(applicationDocker, differentDimension).size(), 0);
    }
}
