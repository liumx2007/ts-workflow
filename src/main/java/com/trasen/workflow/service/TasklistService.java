package com.trasen.workflow.service;

import com.trasen.workflow.model.TaskInstDTO;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

@Component
public class TasklistService {

	public Properties getSqlSessionFactoryProperties(ProcessEngineConfigurationImpl conf) {
		Properties properties = new Properties();		
		ProcessEngineConfigurationImpl.initSqlSessionFactoryProperties(properties, conf.getDatabaseTablePrefix(), conf.getDatabaseType());
		return properties;
	}

	public SqlSessionFactory createMyBatisSqlSessionFactory(InputStream config) {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		ProcessEngineConfiguration processEngineConfiguration = processEngine.getProcessEngineConfiguration();
		DataSource dataSource = processEngineConfiguration.getDataSource();

		// use this transaction factory if you work in a non transactional
		// environment
		// TransactionFactory transactionFactory = new JdbcTransactionFactory();

		// use this transaction factory if you work in a transactional
		// environment (e.g. called within the engine or using JTA)
		TransactionFactory transactionFactory = new ManagedTransactionFactory();

		Environment environment = new Environment("customTasks", transactionFactory, dataSource);

		XMLConfigBuilder parser = new XMLConfigBuilder( //
				new InputStreamReader(config), //
				"", // set environment later via code
				getSqlSessionFactoryProperties((ProcessEngineConfigurationImpl) processEngineConfiguration));
		Configuration configuration = parser.getConfiguration();
		configuration.setEnvironment(environment);
		configuration = parser.parse();

		configuration.setDefaultStatementTimeout(processEngineConfiguration.getJdbcStatementTimeout());

		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
		return sqlSessionFactory;
	}

	public List<TaskInstDTO> getTaskInstList(final String processId) {
		InputStream config = this.getClass().getResourceAsStream("/mybatis-config.xml");
		SqlSessionFactory sqlSessionFactory = createMyBatisSqlSessionFactory(config);
		SqlSession session = sqlSessionFactory.openSession();
		try {
			List<TaskInstDTO> tasks = session.selectList("taskProcess.getTaskInstList", processId);
			return tasks;
		} finally {
			session.close();
		}
	}



}
