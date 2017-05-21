package WordCount; /**
 * Created by lyj on 17-5-1.
 */
import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCount {
    static class MyMapper extends Mapper<Object, Text, Text, IntWritable> {

        private final static IntWritable one = new IntWritable(1);

        @Override
        protected void map(Object key, Text value, Mapper<Object, Text, Text, IntWritable>.Context context)
                throws IOException, InterruptedException {
            context.write(value, one);
        }

    }

    static class MyReduce extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values,
                              Reducer<Text, IntWritable, Text, IntWritable>.Context context)
                throws IOException, InterruptedException {

            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }

    public static boolean run(Map<String,Object> args, InputFormat inputFormat) throws Exception{
        //配置信息
        Configuration conf = new Configuration();

        //job名称
        Job job = Job.getInstance(conf, (String)args.get("name"));

        job.setJarByClass(WordCount.class);
        job.setMapperClass(MyMapper.class);
        // job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(MyReduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        //输入、输出path
        FileInputFormat.addInputPath(job, new Path((String)args.get("input")));
        job.setInputFormatClass(inputFormat.getClass());
        FileOutputFormat.setOutputPath(job, new Path((String)args.get("output")));

        //结束
        return job.waitForCompletion(true);
    }
}
