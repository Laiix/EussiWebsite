package com.eussi.dao;

import com.eussi.domain.Topic;
import com.eussi.test.dataset.util.XlsDataSetBeanFactory;
import org.testng.annotations.Test;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.spring.annotation.SpringBean;

import java.util.List;

/**
 * topic 的DAO类
 *
 */
public class TopicDaoTest extends BaseDaoTest {

	@SpringBean("topicDao")
	private TopicDao topicDao;
	
	@Test
	@ExpectedDataSet("EussiWebsite.ExpectedTopics.xls")
	public void addTopic()throws Exception {
	    List<Topic> topics = XlsDataSetBeanFactory.createBeans(TopicDaoTest.class,"EussiWebsite.SaveTopics.xls", "t_topic", Topic.class);
	    for(Topic topic:topics){
	    	topicDao.save(topic);
	    }
	}
}
