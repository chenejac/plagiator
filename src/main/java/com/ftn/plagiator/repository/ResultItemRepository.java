package com.ftn.plagiator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ftn.plagiator.model.ResultItem;

@Repository
public interface ResultItemRepository extends JpaRepository<ResultItem, Long>{

}
