package com.github.missioncriticalcloud.cosmic.usagetestresources;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.searchbox.client.JestClient;
import io.searchbox.core.Bulk;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;
import io.searchbox.indices.Refresh;
import io.searchbox.indices.template.PutTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

public class EsTestUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void setupIndex(final JestClient client) throws IOException {
        final Resource resource = new ClassPathResource("/cosmic-metrics-template.json");
        final String template = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));

        client.execute(new Delete.Builder("cosmic-metrics-*").build());
        client.execute(new PutTemplate.Builder("cosmic-metrics-template", template).build());
    }

    public static void setupData(final JestClient client) throws IOException {
        final Resource resource = new ClassPathResource("/cosmic-metrics-es-data.json");
        final JsonNode jsonNode = objectMapper.readTree(resource.getInputStream());

        Bulk.Builder builder = new Bulk.Builder().defaultIndex("cosmic-metrics-2017.01.01").defaultType("metric");

        jsonNode.elements().forEachRemaining(metric -> builder.addAction(
                new Index.Builder(metric.toString()).build()
        ));

        client.execute(builder.build());

        client.execute(new Refresh.Builder().build());
    }
}
