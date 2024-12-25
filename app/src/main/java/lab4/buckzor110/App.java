package lab4.buckzor110;

import lab4.buckzor110.calc.SaleInfo;
import lab4.buckzor110.calc.SaleMapper;
import lab4.buckzor110.calc.SaleReducer;
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

        String inputDir = "/Users/e.y.ershov/lab4-Buckzor110/app/input_dir/7.csv";
        String outputDir = "/Users/e.y.ershov/lab4-Buckzor110/app/result";

        int reducersCount = 1;
        int datablockSizeMb = 1 * ((int) Math.pow(2, 10));
        String intermediateResultDir = outputDir + "-intermediate";

        long startTime = System.currentTimeMillis();
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path intermediateOutput = new Path(intermediateResultDir);
        Path finalOutput = new Path(outputDir);

        if (fs.exists(intermediateOutput)) {
            System.out.printf("Удаление существующей промежуточной директории: %s%n", intermediateResultDir);
            fs.delete(intermediateOutput, true);
        }

        if (fs.exists(finalOutput)) {
            System.out.printf("Удаление существующей итоговой директории: %s%n", outputDir);
            fs.delete(finalOutput, true);
        }

        conf.set("mapreduce.input.fileinputformat.split.maxsize", Integer.toString(datablockSizeMb));

        Job salesAnalysisJob = Job.getInstance(conf, "map sales");
        salesAnalysisJob.setNumReduceTasks(reducersCount);
        salesAnalysisJob.setJarByClass(App.class);
        salesAnalysisJob.setMapperClass(SaleMapper.class);
        salesAnalysisJob.setReducerClass(SaleReducer.class);
        salesAnalysisJob.setMapOutputKeyClass(Text.class);
        salesAnalysisJob.setMapOutputValueClass(SaleInfo.class);
        salesAnalysisJob.setOutputKeyClass(Text.class);
        salesAnalysisJob.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(salesAnalysisJob, new Path(inputDir));
        intermediateOutput = new Path(intermediateResultDir);
        FileOutputFormat.setOutputPath(salesAnalysisJob, intermediateOutput);

        boolean success = salesAnalysisJob.waitForCompletion(false);

        if (!success) {
            System.exit(1);
        }

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
        System.out.printf("Jobs completed in %n milliseconds%n", endTime - startTime);

        System.exit(sortByValueJob.waitForCompletion(false) ? 0 : 1);
    }
}