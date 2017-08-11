package org.pdfsearcher;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class Main {

    private static final String INDEX_DIR = "src/main/resources/index";
    private static final int DEFAULT_RESULT_SIZE = 100;

    public static void main(String[] args) throws IOException, ParseException {
        searchText(args[0], "account");
        clearIndexDirectory();
    }

    private static void searchText(String filePath, String key) throws IOException, ParseException {
        File file = new File(filePath);
        IndexItem pdfIndexItem = index(file);

        Indexer indexer = new Indexer(INDEX_DIR);
        indexer.index(pdfIndexItem);
        indexer.close();

        Searcher searcher = new Searcher(INDEX_DIR);
        searcher.findByContent(key, DEFAULT_RESULT_SIZE);
        searcher.close();
    }

    private static void clearIndexDirectory() throws IOException {
        Path rootPath = Paths.get(INDEX_DIR);
        Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .peek(file -> System.out.println("Deleting file : " + file.getName()))
                .forEach(File::delete);

    }

    public static IndexItem index(File file) throws IOException {
        PDDocument pdDocument = PDDocument.load(file);
        String content = new PDFTextStripper().getText(pdDocument);
        pdDocument.close();
        return new IndexItem((long) file.getName().hashCode(), file.getName(), content);
    }
}
