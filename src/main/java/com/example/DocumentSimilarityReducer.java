package com.example;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class DocumentSimilarityReducer extends Reducer<Text, Text, Text, Text> {
    
    private final Map<String, Set<String>> docWordMap = new HashMap<>();
    
    @Override
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        Set<String> wordSet = new HashSet<>();
        for (Text value : values) {
            wordSet.addAll(Arrays.asList(value.toString().split(",")));
        }

        docWordMap.put(key.toString(), wordSet);
        for (Map.Entry<String, Set<String>> entry : docWordMap.entrySet()) {
            String existingDoc = entry.getKey();
            Set<String> existingWordSet = entry.getValue();
            if (existingDoc.equals(key.toString())) {
                continue;
            }
            
            // Calculate Jaccard Similarity
            Set<String> intersection = new HashSet<>(existingWordSet);
            intersection.retainAll(wordSet);

            Set<String> union = new HashSet<>(existingWordSet);
            union.addAll(wordSet);

            double jacSimilarity = (double) intersection.size() / union.size();
            int similarityPercent = (int) Math.round(jacSimilarity * 100);
            
            if (similarityPercent >= 50) {
                String doc1 = existingDoc;
                String doc2 = key.toString();
                context.write(new Text("(" + doc2 + ", " + doc1 + ")"), new Text("-> " + similarityPercent + "%"));
            }
        }
    }
}