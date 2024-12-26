package lab4.buckzor110;

import lab4.buckzor110.calc.SalesData;
import lab4.buckzor110.calc.SalesMapper;
import lab4.buckzor110.calc.SalesReducer;
import lab4.buckzor110.sort.ShuffleData;
import lab4.buckzor110.sort.ShuffleMapper;
import lab4.buckzor110.sort.ShuffleReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class App {

    public static void main(String[] args) throws Exception {

        String inputDir = "./src/main/resources/input_dir";
        String outputDir = "./src/main/resources/output_dir";

        int reducersCount = 40;
        int datablockSizeMb = 1_000_000;
        String intermediateResultDir = outputDir + "-processing";

        long startTime = System.currentTimeMillis();
        Configuration conf = new Configuration();

        FileSystem fs = FileSystem.get(conf);
        Path intermediateOutput = new Path(intermediateResultDir);
        Path finalOutput = new Path(outputDir);

        if (fs.exists(intermediateOutput)) {
            fs.delete(intermediateOutput, true);
        }

        if (fs.exists(finalOutput)) {
            fs.delete(finalOutput, true);
        }


        conf.set("mapreduce.input.fileinputformat.split.maxsize", Integer.toString(datablockSizeMb));
        conf.set("mapreduce.map.output.compress", "true");
        conf.set("mapreduce.map.output.compress.codec", "org.apache.hadoop.io.compress.SnappyCodec");
        conf.set("mapreduce.map.speculative", "true");
        conf.set("mapreduce.reduce.speculative", "true");

        Job salesAnalysisJob = Job.getInstance(conf, "map sales");
        salesAnalysisJob.setNumReduceTasks(reducersCount);
        salesAnalysisJob.setJarByClass(App.class);
        salesAnalysisJob.setMapperClass(SalesMapper.class);
        salesAnalysisJob.setReducerClass(SalesReducer.class);
        salesAnalysisJob.setMapOutputKeyClass(Text.class);
        salesAnalysisJob.setMapOutputValueClass(SalesData.class);
        salesAnalysisJob.setOutputKeyClass(Text.class);
        salesAnalysisJob.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(salesAnalysisJob, new Path(inputDir));
        intermediateOutput = new Path(intermediateResultDir);
        FileOutputFormat.setOutputPath(salesAnalysisJob, intermediateOutput);

        salesAnalysisJob.waitForCompletion(true);

        Job sortByValueJob = Job.getInstance(conf, "sorting by prize");
        sortByValueJob.setJarByClass(App.class);
        sortByValueJob.setMapperClass(ShuffleMapper.class);
        sortByValueJob.setReducerClass(ShuffleReducer.class);
        sortByValueJob.setMapOutputKeyClass(DoubleWritable.class);
        sortByValueJob.setMapOutputValueClass(ShuffleData.class);
        sortByValueJob.setOutputKeyClass(ShuffleData.class);
        sortByValueJob.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(sortByValueJob, intermediateOutput);
        FileOutputFormat.setOutputPath(sortByValueJob, new Path(outputDir));

        long endTime = System.currentTimeMillis();
        System.out.printf("Jobs completed in %s milliseconds", endTime - startTime);

        System.exit(sortByValueJob.waitForCompletion(true) ? 0 : 1);
    }
}