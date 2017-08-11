package org.pdfsearcher;


import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;


import java.io.IOException;
import java.nio.file.Paths;


public class Indexer {
    private IndexWriter indexWriter;
    StandardAnalyzer analyzer;

    public Indexer(String indexDir) throws IOException {
        if (indexWriter == null) {
            analyzer = new StandardAnalyzer();
            indexWriter = new IndexWriter(FSDirectory.open(Paths.get(indexDir)),
                    new IndexWriterConfig(analyzer));
        }
    }

    public void index(IndexItem indexItem) throws IOException {
        indexWriter.deleteDocuments(new Term(IndexItem.ID, indexItem.getId().toString()));

        Document document = new Document();
        FieldType type = new FieldType();
        type.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        type.setStored(true);
        type.setStoreTermVectors(true);
        document.add(new Field(IndexItem.ID, indexItem.getId().toString(), type));
        document.add(new Field(IndexItem.TITLE, indexItem.getTitle(), type));
        document.add(new Field(IndexItem.CONTENT, indexItem.getContent(), type));

        indexWriter.addDocument(document);

    }

    public void close() throws IOException {
        indexWriter.close();
        analyzer.close();
    }
}
