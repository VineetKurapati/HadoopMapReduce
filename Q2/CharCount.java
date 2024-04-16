import java.io.IOException; 
import java.util.Iterator; 
import org.apache.hadoop.fs.Path; 
import org.apache.hadoop.io.IntWritable; 
import org.apache.hadoop.io.LongWritable; 
import org.apache.hadoop.io.Text; 
import org.apache.hadoop.mapred.FileInputFormat; 
import org.apache.hadoop.mapred.FileOutputFormat; 
import org.apache.hadoop.mapred.JobClient; 
import org.apache.hadoop.mapred.JobConf; 
import org.apache.hadoop.mapred.MapReduceBase; 
import org.apache.hadoop.mapred.Mapper; 
import org.apache.hadoop.mapred.OutputCollector; 
import org.apache.hadoop.mapred.Reducer; 
import org.apache.hadoop.mapred.Reporter; 
import org.apache.hadoop.mapred.TextInputFormat; 
import org.apache.hadoop.mapred.TextOutputFormat; 

public class CharCount { 
    public static class CharCountMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {     
        private final static IntWritable one = new IntWritable(1); 
        private Text charKey = new Text(); 

        public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {     
            String line = value.toString();     
            for (int i = 0; i < line.length(); i++) {
                char character = line.charAt(i);
                charKey.set(String.valueOf(character));
                output.collect(charKey, one);
            }
        }     
    } 
    
    public static class CharCountReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> { 
        public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException { 
            int sum = 0; 
            while (values.hasNext()) { 
                sum += values.next().get(); 
            } 
            output.collect(key, new IntWritable(sum)); 
        } 
    }
    
    public static void main(String[] args) throws IOException { 
        if (args.length != 2) {
            System.err.println("Usage: CharCount <input path> <output path>");
            System.exit(-1);
        }

        JobConf conf = new JobConf(CharCount.class); 
        conf.setJobName("CharCount"); 
        conf.setOutputKeyClass(Text.class); 
        conf.setOutputValueClass(IntWritable.class); 
        conf.setMapperClass(CharCountMapper.class); 
        conf.setCombinerClass(CharCountReducer.class); 
        conf.setReducerClass(CharCountReducer.class); 
        conf.setInputFormat(TextInputFormat.class); 
        conf.setOutputFormat(TextOutputFormat.class); 
        FileInputFormat.setInputPaths(conf, new Path(args[0])); 
        FileOutputFormat.setOutputPath(conf, new Path(args[1])); 
        JobClient.runJob(conf); 
    } 
}
