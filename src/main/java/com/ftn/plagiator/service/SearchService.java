package com.ftn.plagiator.service;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.ftn.plagiator.elasticsearch.model.PaperElastic;
import com.ftn.plagiator.elasticsearch.resultMappers.ContentResultMapper;


@Service
public class SearchService {

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	public Page<PaperElastic> listaRadova(String text) {
			  
		@SuppressWarnings({ "unused" })
		Analyzer analyzer = new Analyzer() {
			
			@Override
			protected TokenStreamComponents createComponents(String fieldName) {
				
				Tokenizer source = new StandardTokenizer();
				TokenStream src = new LowerCaseFilter(source);
			  
				ShingleFilter filter = new ShingleFilter(src, 2, 3);
				filter.setOutputUnigrams(false);
			  
				TokenStreamComponents tsc = new TokenStreamComponents(source, filter);
				return tsc;
			}
		};
		
		MatchQueryBuilder queryParams = QueryBuilders
				.matchQuery("text", text);//.minimumShouldMatch("30%"); //u kom procentu zelim poklapanja
				//.analyzer(analyzer)
				//.build();
		
		SearchQuery theQuery = new NativeSearchQueryBuilder()
				.withQuery(queryParams)
				//.withMinScore(3)  //koliko zelim da bude poklapanje
				.build();

		return elasticsearchTemplate.queryForPage(theQuery, PaperElastic.class, new ContentResultMapper());
	}
	

	

}
