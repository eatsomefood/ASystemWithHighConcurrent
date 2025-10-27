package com.star.highconcurrent.util;

import cn.hutool.bloomfilter.BitMapBloomFilter;
import cn.hutool.bloomfilter.BloomFilterUtil;
import com.star.highconcurrent.mapper.BlogMapper;
import com.star.highconcurrent.mapper.CommentMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class CommentBloomFilter implements CommandLineRunner {

    private Long dataSize = 0L;

    @Value("${com.star.bloom-filter.comment.miss-precent:0.1}")
    private double missPrecent;

    private BitMapBloomFilter filter;

    @Resource
    private SqlSessionFactory factory;

    // 初始化布隆过滤器
    public void initFilter() {
        SqlSession sqlSession = factory.openSession(false);
        Cursor<Long> cursor = null;// 从数据库中查询出所有的数据，然后把这些数据存入布隆过滤器中
        try {
            // 分批次获取，防止数据过大
            // 初始化列表，避免反复扩容
            List<Long> list = new ArrayList<>(1000);
            CommentMapper mapper = sqlSession.getMapper(CommentMapper.class);
            int count = mapper.getCount();
            filter = new BitMapBloomFilter(count);
            cursor = mapper.getAllBlogIdByCursor();
            for (Long id : cursor) {
                list.add(id);
                if (list.size() >= 1000) {
                    addDataToFilter(list);
                    dataSize += list.size();
                    list.clear();
                }
            }
            if (!list.isEmpty()) {
                dataSize += list.size();
                addDataToFilter(list);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            log.info("当前数据库评论总数为:{}",dataSize);
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            sqlSession.close();
        }
    }

    public void addDataToFilter(List<Long> list) {
        for (Long l : list) {
            filter.add(String.valueOf(l));
        }
    }

    public void addData(long id){
        filter.add(String.valueOf(id));
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("初始化布隆过滤器");
        initFilter();
    }

    public boolean isValid(Long id){
        return filter.contains(String.valueOf(id));
    }

    public long dataSize(){
        return dataSize;
    }

}
