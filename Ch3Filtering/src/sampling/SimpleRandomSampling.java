package sampling;

import java.io.IOException;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class SimpleRandomSampling extends Configured implements Tool {
	public static final String FILTER_PERCENTAGE_KEY = "com.eftimoff.mapreduce.filtering.sampling.filter_percentage";

	public static class SimpleRandomSamplingMapper extends
			Mapper<Object, Text, NullWritable, Text> {
		private float filterPercentage;
		private Random rands = new Random();

		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			filterPercentage = context.getConfiguration().getFloat(
					FILTER_PERCENTAGE_KEY, 0.0f);
		}

		@Override
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			if (rands.nextFloat() < filterPercentage) {
				context.write(NullWritable.get(), value);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(),
				new SimpleRandomSampling(), args);
		System.exit(res);
	}

	@Override
	public int run(String[] args) throws Exception {
		Configuration conf = new Configuration();
		GenericOptionsParser parser = new GenericOptionsParser(conf, args);
		String[] otherArgs = parser.getRemainingArgs();
		if (otherArgs.length != 3) {
			printUsage();
		}
		Float filterPercentage = 0.0f;
		try {
			filterPercentage = Float.parseFloat(otherArgs[0]) / 100.0f;
		} catch (NumberFormatException nfe) {
			printUsage();
		}

		Job job = new Job(conf, "Simple Random Sampling");
		job.setJarByClass(SimpleRandomSampling.class);
		job.setMapperClass(SimpleRandomSamplingMapper.class);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Text.class);
		job.setNumReduceTasks(1); // prevent lots of small files
		job.getConfiguration()
				.setFloat(FILTER_PERCENTAGE_KEY, filterPercentage);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		boolean success = job.waitForCompletion(true);

		return success ? 0 : 1;
	}

	private void printUsage() {
		System.err
				.println("Usage: SimpleRandomSampling <percentage> <in> <out>");
		ToolRunner.printGenericCommandUsage(System.err);
		System.exit(2);
	}
}