package com.example;
import java.io.IOException;
import java.util.HashSet;
import java.util.StringTokenizer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class DocumentSimilarityMapper extends Mapper<Object, Text, Text, Text> {
    
    public void map(Object Key, Text value, Context context) throws IOException, InterruptedException{
        
        String fileName = ((FileSplit) context.getInputSplit()).getPath().getName();
        HashSet<String> wordSet = new HashSet<>();
        StringTokenizer strTokenizer = new StringTokenizer(value.toString());

        while (strTokenizer.hasMoreTokens()) {
            wordSet.add(strTokenizer.nextToken().toLowerCase());
        }
        context.write(new Text(fileName), new Text(String.join(",", wordSet)));

    }
}
