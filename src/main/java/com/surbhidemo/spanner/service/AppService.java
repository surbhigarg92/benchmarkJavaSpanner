package com.surbhidemo.spanner.service;

import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.TimestampBound;
import com.surbhidemo.spanner.components.DbDetails;
import com.surbhidemo.spanner.restInterfaces.AppServiceRequestBoundary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;


@Service
public final class AppService implements AppServiceRequestBoundary {

    private final DbDetails dbDetails;
    DatabaseClient client;

    public AppService(DbDetails dbDetails) {
        this.dbDetails = dbDetails;
    }

    @Override
    public void run(int parallelCycles, int iterations) {
        System.out.println("\n--------- Details of Run-------------\n");
        System.out.println(dbDetails.getDbName());
        System.out.println(dbDetails.getProjectId());
        System.out.println(dbDetails.getQuery());
        System.out.println("\n-------------------------------------\n");
        for (int i = 0; i < parallelCycles; i++) {
            CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
                runLive(iterations);
            });
            System.out.println("New Iteration Started and Is Done: ");
        }
    }

    public void runLive(int iterations) {
        client =
                SpannerOptions.newBuilder()
                        .setNumChannels(1)
                        .setProjectId(dbDetails.getProjectId())
                        .build()
                        .getService()
                        .getDatabaseClient(DatabaseId.of(dbDetails.getDbName()));
        System.out.println("Started the warmup for client ID" + client.hashCode());
        for (int i = 0; i < 20000; i++) {
            // trigger JIT compilation
            if (i % 100 == 0) {
                System.out.println("Attempt: " + i);
            }
            select1Query();
        }

        benchmark(iterations, 20000);
    }

    void select1Query() {
        try {
            com.google.cloud.spanner.ResultSet rs =
                    client.singleUse(TimestampBound.ofMaxStaleness(10000, TimeUnit.SECONDS))
                            .executeQuery(Statement.newBuilder(dbDetails.getQuery()).build());
            if (!rs.next() || rs.getString(0).isEmpty()) {
                throw new RuntimeException("Noooo");
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.out.println(Arrays.toString(ex.getStackTrace()));
            throw ex;
        }
    }

    private void benchmark(int runs, int attempts) {
        for (int r = 0; r < runs; r++) {
            ArrayList<Integer> latencies = new ArrayList<>(attempts);
            for (int i = 0; i < attempts; i++) {
                long start = System.nanoTime();
                select1Query();
                int latency = (int) ((System.nanoTime() - start) / 1000);
                latencies.add(latency);
            }
            latencies.sort(Integer::compareTo);
            for (int p : new int[]{0, 25, 50, 75, 90, 99}) {
                System.out.printf("%d\t", latencies.get(p * latencies.size() / 100));
            }
            System.out.println();
        }
    }
}
