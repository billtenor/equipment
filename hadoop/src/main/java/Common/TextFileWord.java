package Common;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.util.LineReader;

import java.io.IOException;
import java.util.StringTokenizer;

class TextFileWordReader extends RecordReader<LongWritable,Text>{
    final private String splitChar;
    private LineReader lineReader;
    private StringTokenizer lintStr=null;
    private LongWritable key;
    private Text value;
    private long start;
    private long pos;
    private long offset;
    private long end;

    public TextFileWordReader(String splitChar){
        this.splitChar=splitChar;
    }

    @Override
    public void initialize(InputSplit inputSplit, TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        Configuration conf = taskAttemptContext.getConfiguration();
        this.key=new LongWritable();
        this.value=new Text();
        try{
            FileSplit split=(FileSplit)inputSplit;
            FileSystem hdfs = FileSystem.get(conf);
            FSDataInputStream inputStream = hdfs.open(split.getPath());
            this.lineReader = new LineReader(inputStream);
            this.start=split.getStart();
            this.end=this.start+split.getLength();
            this.pos=this.start;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void close() throws IOException {
        this.lineReader.close();
    }

    @Override
    public LongWritable getCurrentKey() throws IOException, InterruptedException {
        return this.key;
    }

    @Override
    public Text getCurrentValue() throws IOException, InterruptedException {
        return this.value;
    }

    private StringTokenizer createStringTokenizer(String str){
        if(this.splitChar==null){
            return new StringTokenizer(str);
        }
        else{
            return new StringTokenizer(str,splitChar);
        }
    }

    private void setKey(){
        this.key.set(this.pos);
    }
    private void setValue(){
        String tmp = this.lintStr.nextToken();
        this.offset-=tmp.length();
        this.value.set(tmp);
    }
    @Override
    public synchronized boolean nextKeyValue() throws IOException {
        int newSize=-1;
        while(lintStr==null||!lintStr.hasMoreTokens()){
            Text text=new Text();
            newSize = lineReader.readLine(text);
            if(newSize == 0) {
                this.key = null;
                this.value = null;
                return false;
            }
            else{
                this.lintStr=createStringTokenizer(text.toString());
            }
        }
        if(newSize!=-1){
            setKey();
            this.pos+=(long)newSize;
            this.offset=(long)newSize;
        }
        setValue();
        return true;
    }

    @Override
    public float getProgress() throws IOException, InterruptedException {
        return this.start == this.end?0.0F:Math.min(1.0F, (float)(this.pos-this.offset - this.start) / (float)(this.end - this.start));
    }
}
/**
 * Created by lyj on 17-5-2.
 */
public class TextFileWord extends FileInputFormat<LongWritable, Text> {
    @Override
    public RecordReader<LongWritable, Text> createRecordReader(InputSplit inputSplit, TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        return new TextFileWordReader(null);
    }
}