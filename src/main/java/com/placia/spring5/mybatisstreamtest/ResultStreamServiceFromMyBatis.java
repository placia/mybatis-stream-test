package com.placia.spring5.mybatisstreamtest;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResultStreamServiceFromMyBatis {
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    public void publish(String statement, ResultHandlerWithSubscriber resultHandlerWithSubscriber) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            sqlSession.select(statement, resultHandlerWithSubscriber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
