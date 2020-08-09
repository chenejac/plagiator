package com.ftn.plagiator.elasticsearch.resultMappers;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;

import com.ftn.plagiator.elasticsearch.model.PaperElastic;

public class ContentResultMapper implements SearchResultMapper{
	
	public ContentResultMapper() {
		
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
		
		List<PaperElastic> chunk = new ArrayList<PaperElastic>();
        for (SearchHit searchHit : response.getHits()) {
            if (response.getHits().getHits().length <= 0) {
                return null;
            }
          
            Map<String, Object> source = searchHit.getSourceAsMap();
            //System.out.println(searchHit.getScore());
            PaperElastic paper = new PaperElastic();
            paper.setId(Long.parseLong(searchHit.getId()));
            paper.setText((String) source.get("text"));
            paper.setTitle((String) source.get("title"));
            
            paper.setSearchHits(searchHit.getScore());
 
            chunk.add(paper);
        }
        if (chunk.size() > 0) {
            return new AggregatedPageImpl(chunk);
        }
       
        return null;
	}

}
