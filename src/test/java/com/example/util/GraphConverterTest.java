package com.example.util;

import com.example.model.MyGraph;
import com.example.model.MyVertex;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class GraphConverterTest {
    @Test
    public void basicExportImportTest() throws IOException, ParserConfigurationException, SAXException {
        final String filename = "test.xml";

        MyGraph<Integer, Integer> graph = new MyGraph<>();
        MyVertex<Integer> v1 = new MyVertex<>(1);
        MyVertex<Integer> v2 = new MyVertex<>(3);
        MyVertex<Integer> v3 = new MyVertex<>(5);

        graph.insertVertex(v1);
        graph.insertVertex(v2);
        graph.insertVertex(v3);

        graph.insertEdge(v1, v2, 1);
        graph.insertEdge(v1, v3, 1);

        GraphConverter.saveGraphML(new File(filename), graph);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(filename);

        assert (doc.getElementsByTagName("node").getLength() == 3);
        assert (doc.getElementsByTagName("edge").getLength() == 2);
    }
}
