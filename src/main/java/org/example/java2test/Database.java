package org.example.java2test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Database {
    private final String url = "http://www.bsi.si/_data/tecajnice/dtecbs-l.xml";
    private final List<Element> tecajnicaElements;

    public Database() {
        tecajnicaElements = fetchXMLData(url);
    }

    private List<Element> fetchXMLData(String url) {
        List<Element> elements = new ArrayList<>();

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() == 200) {
                InputStream inputStream = response.body();
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(true); // Add this line to handle namespaces
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(inputStream);
                NodeList nodeList = document.getElementsByTagNameNS("http://www.bsi.si", "tecajnica"); // Use getElementsByTagNameNS to handle namespaces

                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        elements.add((Element) node);
                    }
                }

                inputStream.close();
            }
        } catch (IOException | InterruptedException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }

        return elements;
    }


    public LocalDate getMinDate() {
        return tecajnicaElements.stream()
                .map(element -> LocalDate.parse(element.getAttribute("datum")))
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());
    }

    public LocalDate getMaxDate() {
        return tecajnicaElements.stream()
                .map(element -> LocalDate.parse(element.getAttribute("datum")))
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());
    }

    public List<String> getCurrencies(LocalDate startDate, LocalDate endDate) {
        Set<String> startCurrencies = new HashSet<>();
        Set<String> endCurrencies = new HashSet<>();

        for (org.w3c.dom.Element element : tecajnicaElements) {
            LocalDate elementDate = LocalDate.parse(element.getAttribute("datum"));

            if (elementDate.equals(startDate) || elementDate.equals(endDate)) {
                NodeList tecajElements = element.getElementsByTagName("tecaj");

                for (int i = 0; i < tecajElements.getLength(); i++) {
                    org.w3c.dom.Element tecajElement = (org.w3c.dom.Element) tecajElements.item(i);
                    String currency = tecajElement.getAttribute("oznaka");

                    if (elementDate.equals(startDate)) {
                        startCurrencies.add(currency);
                    } else {
                        endCurrencies.add(currency);
                    }
                }
            }

            // Exit the loop early if both start and end currencies have been found
            if (!startCurrencies.isEmpty() && !endCurrencies.isEmpty()) {
                break;
            }
        }

        // Find the intersection of the two sets
        startCurrencies.retainAll(endCurrencies);

        return new ArrayList<>(startCurrencies);
    }

    public List<DataEntry> getDataEntries(String currency1, String currency2) {
        List<DataEntry> dataEntries = new ArrayList<>();

        for (org.w3c.dom.Element element : tecajnicaElements) {
            LocalDate date = LocalDate.parse(element.getAttribute("datum"));
            NodeList tecajElements = element.getElementsByTagName("tecaj");

            Double currency1Value = null;
            Double currency2Value = null;
            for (int i = 0; i < tecajElements.getLength(); i++) {
                org.w3c.dom.Element tecajElement = (org.w3c.dom.Element) tecajElements.item(i);
                String currency = tecajElement.getAttribute("oznaka");
                if (currency.equals(currency1)) {
                    currency1Value = Double.parseDouble(tecajElement.getTextContent());
                } else if (currency.equals(currency2)) {
                    currency2Value = Double.parseDouble(tecajElement.getTextContent());
                }

                if (currency1Value != null && currency2Value != null) {
                    break;
                }
            }

            if (currency1Value != null && currency2Value != null) {
                DataEntry dataEntry = new DataEntry(date.toString(), currency1Value, currency2Value);
                dataEntries.add(dataEntry);
            }
        }

        return dataEntries;
    }

}

