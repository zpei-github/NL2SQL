package com.web.mapper.elasticserach;

import com.web.entity.elasticsearch.StandardColumnIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface StandardColumnRepository extends ElasticsearchRepository<StandardColumnIndex, String> {
}
