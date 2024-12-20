package com.web.mapper.elasticserach;

import com.web.entity.elasticsearch.StandardTableIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface StandardTableRepository extends ElasticsearchRepository<StandardTableIndex, String> {
}
