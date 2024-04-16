import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class KMeansClustering {

    // Mapper class for assigning points to nearest centroids
    public static class KMeansMapper extends Mapper<Object, Text, FloatWritable, FloatWritable> {

        private List<Float> centroids = new ArrayList<>();

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            // Load centroids from the configuration
            Configuration conf = context.getConfiguration();
            String centroidsStr = conf.get("centroids");
            String[] centroidsArr = centroidsStr.split(",");
            for (String centroid : centroidsArr) {
                centroids.add(Float.parseFloat(centroid));
            }
        }

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            Float point = Float.parseFloat(value.toString());
            Float minDistance = Float.MAX_VALUE;
            Float nearestCentroid = null;
            // Find the nearest centroid for the current point
            for (Float centroid : centroids) {
                Float distance = Math.abs(centroid - point);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestCentroid = centroid;
                }
            }
            // Emit the nearest centroid and the point
            context.write(new FloatWritable(nearestCentroid), new FloatWritable(point));
        }
    }

    // Reducer class for assigning each data point to its final centroid
    public static class KMeansReducer extends Reducer<FloatWritable, FloatWritable, FloatWritable, FloatWritable> {

        @Override
        public void reduce(FloatWritable key, Iterable<FloatWritable> values, Context context)
                throws IOException, InterruptedException {
            // Emit each data point along with its corresponding centroid
            for (FloatWritable value : values) {
                context.write(value, key);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.err.println("Usage: KMeansClustering <input> <output> <k>");
            System.exit(2);
        }
        // Initialize centroids randomly
        int k = Integer.parseInt(args[2]);
        StringBuilder initialCentroids = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < k; i++) {
            float centroid = random.nextFloat() * 100; // Assuming range of input data is [0, 100]
            initialCentroids.append(centroid);
            if (i < k - 1) {
                initialCentroids.append(",");
            }
        }

        Configuration conf = new Configuration();
        // Set centroids in configuration
        conf.set("centroids", initialCentroids.toString());

        Job job = Job.getInstance(conf, "KMeans Clustering");
        job.setJarByClass(KMeansClustering.class);
        job.setMapperClass(KMeansMapper.class);
        job.setReducerClass(KMeansReducer.class);
        job.setOutputKeyClass(FloatWritable.class);
        job.setOutputValueClass(FloatWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
