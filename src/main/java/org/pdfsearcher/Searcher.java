package org.pdfsearcher;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.nio.file.Paths;


public class Searcher {
    private IndexSearcher indexSearcher;
    private QueryParser queryParser;
    private StandardAnalyzer standardAnalyzer;
    private IndexReader reader;

    public Searcher(String indexDir) throws IOException {
        this.reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDir)));
        indexSearcher = new IndexSearcher(reader);
        standardAnalyzer = new StandardAnalyzer();

        queryParser = new QueryParser(IndexItem.CONTENT, standardAnalyzer);
    }

    public void findByContent(String queryString, int numOfResults) throws IOException, ParseException {
        Query query = queryParser.parse(queryString);

        ScoreDoc[] scoreDocs = indexSearcher.search(query, numOfResults).scoreDocs;

        for (int i = 0; i < scoreDocs.length; i++) {
            int docID = scoreDocs[i].doc;
            Terms terms = reader.getTermVector(docID, IndexItem.CONTENT);
            String max = terms.getMax().utf8ToString();
            String min = terms.getMin().utf8ToString();
            long sumDocFreq = terms.getSumDocFreq();

            System.out.println("Term name : " + IndexItem.CONTENT + " Total Terms : " + sumDocFreq + " Min term : " + min + " Max term : " + max);

            TermsEnum iterator = terms.iterator();
            BytesRef term;
            while ((term = iterator.next()) != null) {
                String termText = term.utf8ToString();
                if (queryString.equals(termText)) {
                    long termFreq = iterator.totalTermFreq();
                    long docCount = iterator.docFreq();

                    System.out.println("term: " + termText + ", termFreq = " + termFreq + ", docCount = " + docCount);
                    break;
                }
            }
        }

    }

    public void close() throws IOException {
        standardAnalyzer.close();
    }
}
