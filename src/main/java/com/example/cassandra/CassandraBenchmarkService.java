package com.example.cassandra;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.example.cassandra.CassandraService.ID_NAME;

@Service
public class CassandraBenchmarkService {
    @Autowired
    private CassandraService cassandraService;
    @Autowired
    private CassandraDao cassandraDao;

    private static final String BENCHMARK_TABLE = "benchmark";
    private static final AtomicLong ID = new AtomicLong();

    public void createBenchmarkMapTable(){
        cassandraService.createTableOfMap(BENCHMARK_TABLE);
    }

    public long writeBenchmarkMapTable(Map<String, String> operationData){
        // TODO: 17/07/2017 main table
        String strId = String.valueOf(ID.incrementAndGet());

        cassandraDao.insertMapIntoMap(BENCHMARK_TABLE, strId, operationData);
        return ID.get();
    }

    public void dropBenchmarkMapTable(){
        cassandraService.dropTable(BENCHMARK_TABLE);
    }

    public Map<String, String> readBenchmarkMapTable(Long operationId) {
        Select.Where select = QueryBuilder.select().from(BENCHMARK_TABLE).where(eq(ID_NAME, operationId.toString()));
        return cassandraService.execute(select).one().getMap(BENCHMARK_TABLE,String.class, String.class);
    }
}
