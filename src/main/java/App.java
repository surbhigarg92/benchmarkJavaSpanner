
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.TimestampBound;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * This sample demonstrates how to configure OpenTelemetry and inject via Spanner Options.
 */
public final class App {

  private static final String DB =
      "projects/projectid/instances/instanceid/databases/test-db";
  private static final String QUERY = "SELECT '1'";
  static DatabaseClient client;

  public static void main(String[] args) {
    client =
        SpannerOptions.newBuilder()
            .setNumChannels(1)
            .build()
            .getService()
            .getDatabaseClient(DatabaseId.of(DB));
    System.out.println("Started the warmup");
    for (int i = 0; i < 20000; i++) {
      // trigger JIT compilation
      select1Query();
    }

    benchmark( 40, 20000);
  }
  static void select1Query() {
    com.google.cloud.spanner.ResultSet rs =
        client
            .singleUse(TimestampBound.ofMaxStaleness(10000, TimeUnit.SECONDS))
            .executeQuery(Statement.newBuilder(QUERY).build());
    if (!rs.next() || rs.getString(0).isEmpty()) {
      throw new RuntimeException("Noooo");
    }
    rs.close();
  }

  private static void benchmark(int runs, int attempts) {
    for (int r = 0; r < runs; r++) {
      // for (int i = 0; i < 10000; i++) {
      //   // warmup
      //   select1Query();
      // }
      ArrayList<Integer> latencies = new ArrayList<>(attempts);
      for (int i = 0; i < attempts; i++) {
        long start = System.nanoTime();
        select1Query();
        int latency = (int) ((System.nanoTime() - start) / 1000);
        latencies.add(latency);
      }
      latencies.sort(Integer::compareTo);
      for (int p : new int[] {0, 25, 50, 75, 90, 99}) {
        System.out.printf("%d\t", latencies.get(p * latencies.size() / 100));
      }
      System.out.println();
    }
  }
}
