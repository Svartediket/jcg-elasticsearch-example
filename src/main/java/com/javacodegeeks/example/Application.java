package com.javacodegeeks.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Application {

    //The config parameters for the connection
    private static final String HOST = "localhost";
    private static final int PORT_ONE = 9200;
    private static final int PORT_TWO = 9201;
    private static final String SCHEME = "http";

    private static RestHighLevelClient restHighLevelClient;
    private static ObjectMapper objectMapper = new ObjectMapper();

    private static final String INDEX = "testl";
    private static final String TYPE = "test";

    /**
     * Implemented Singleton pattern here
     * so that there is just one connection at a time.
     * @return RestHighLevelClient
     */
    private static synchronized RestHighLevelClient makeConnection() {

        if(restHighLevelClient == null) {
            restHighLevelClient = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(HOST, PORT_ONE, SCHEME),
                            new HttpHost(HOST, PORT_TWO, SCHEME)));
        }

        return restHighLevelClient;
    }

    private static synchronized void closeConnection() throws IOException {
        restHighLevelClient.close();
        restHighLevelClient = null;
    }

    private static void insertPerson(List<Article> articles){
        int id = 0;
        for(Article a : articles) {
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("href", a.getHref());
            dataMap.put("title", a.getTitle());
            dataMap.put("text", a.getText());
            dataMap.put("event", a.getEvent());
            dataMap.put("date", a.getDate());
            IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, Integer.toString(id))
                    .source(dataMap);
            try {
                IndexResponse response = restHighLevelClient.index(indexRequest);
            } catch(ElasticsearchException e) {
                e.getDetailedMessage();
            } catch (java.io.IOException ex){
                ex.getLocalizedMessage();
            }
            id++;
        }

    }

    public static void main(String[] args) throws IOException {

        makeConnection();

        GetArticles articles = new GetArticles();
        List<Article> listArticles = articles.getArticles();
        insertPerson(listArticles);

        closeConnection();
    }
}
