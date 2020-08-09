package com.ftn.plagiator.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.ftn.plagiator.validation.StaticData;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.env.Environment;
import org.elasticsearch.node.InternalSettingsPreparer;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.transport.Netty4Plugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import com.ftn.plagiator.elasticsearch.plugin.SerbianPlugin;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.ftn.plagiator.elasticsearch.repository")
public class ElasticSearchConfiguration {

	@SuppressWarnings("resource")
	public Client nodeClient() {

		File tmpDir = null;
		try {
			tmpDir = new File(StaticData.PUTANJA_DO_ELASTICSEARCHDATA);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		XContentBuilder settingsBuilder = null;
		
		try {
			
			settingsBuilder = XContentFactory.jsonBuilder()
			        .startObject()
			            .startObject("analysis")
			                .startObject("filter")
			                    .startObject("filter_shingle") //my_shingle_filter
			                        .field("type","shingle")
			                        .field("min_shingle_size", 4)
			                        .field("max_shingle_size", 6)
			                        .field("output_unigrams", false)
			                    .endObject()
			                .endObject()
//			                .startObject("tokenizer")
//			                    .startObject("my_ngram_tokenizer")
//			                        .field("type","nGram")
//			                        .field("min_gram",1)
//			                        .field("max_gram",1)
//			                    .endObject()
//			                .endObject()
			                .startObject("analyzer")
			                    .startObject("ShingleAnalyzer") //my_shingle_analyzer
//			                        .field("tokenizer","my_ngram_tokenizer")
			                    	.field("type","custom")
			                    	.field("tokenizer","standard")
			                        .array("filter", "lowercase", "filter_shingle")
			                    .endObject()
			                .endObject()
			            .endObject()
			        .endObject();
			
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Settings settings = Settings.builder().put("http.enabled", true)
				.put("cluster.name", "elasticsearch")
				.put("transport.type", "netty4").put("client.transport.sniff", true)
//                .put("index.query.bool.max_clause_count", 100000)
				.put("indices.query.bool.max_clause_count", 100000)
				.put("path.data", new File(tmpDir, "data").getAbsolutePath())
				.put("path.logs", new File(tmpDir, "logs").getAbsolutePath())
				.put("path.home", tmpDir.getAbsolutePath())
                .build();

		Collection<Class<? extends Plugin>> plugins = new ArrayList<>();
		plugins.add(SerbianPlugin.class);
		plugins.add(Netty4Plugin.class);
		

		Node node;
		Client client = null;
			try {
				node = new NodeWithPlugins(InternalSettingsPreparer.prepareEnvironment(settings, null), plugins).start();
				client = node.client();
				
				String[] indexesForAnalyzer = new String[] { 
						"paper"
					};

					for (String indexName : indexesForAnalyzer) {
						boolean indexExists = client.admin().indices()
						.prepareExists(indexName)
						.execute().actionGet().isExists();
						if(!indexExists){
						client.admin().indices().prepareCreate(indexName)
						    .setSettings(settingsBuilder)
						    .execute().actionGet();
							}
						}

				
				return client;
				
			} catch (NodeValidationException e) {
				e.printStackTrace();
			}
			
		return null;
	}

	@Bean
	public ElasticsearchTemplate elasticsearchTemplate() {
		return new ElasticsearchTemplate(nodeClient());
	}

	private class NodeWithPlugins extends Node {
		protected NodeWithPlugins(Environment environment, Collection<Class<? extends Plugin>> list) {
			super(environment, list);
		}
	}

}
