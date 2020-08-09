package com.ftn.plagiator.elasticsearch.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.ftn.plagiator.elasticsearch.model.PaperElastic;

@Repository
public interface PaperElasticRepository extends ElasticsearchRepository<PaperElastic, Long>{

    @Query("{\n" +
            "    \"bool\":{\n" +
            "        \"must\": [\n" +
            "            {\n" +
            "                \"bool\": {\n" +
            "                    \"should\": [\n" +
            "                        {\n" +
            "                            \"wildcard\": {\n" +
            "                                \"title\": \"*?0*\"\n" +
            "                            }\n" +
            "                        }\n" +
            "                    ]\n" +
            "                }\n" +
            "            }\n" +
            "        ]\n" +
            "    }\n" +
            "}")
    Page<PaperElastic> findByFilter(String filter, Pageable pageable);
}
