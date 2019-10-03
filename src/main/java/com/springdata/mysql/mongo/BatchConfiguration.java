package com.springdata.mysql.mongo;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.springdata.models.Users;




@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	@Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    
    @Autowired
    EntityManagerFactory emf;
    
    @Autowired
    DataSource dataSource;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    private static final String QUERY_FIND_USERS =
            "SELECT FROM Users";
 
    @Bean
    public Job readCSVFile() {
      return jobBuilderFactory.get("readCSVFile").incrementer(new RunIdIncrementer()).start(step1())
          .build();
    }
    //org.springframework.jdbc.datasource.DriverManagerDataSource
    @Bean
    public Step step1() {
      try {
		return stepBuilderFactory.get("step1").<Users, Users>chunk(10).reader(productItemReader())
		      .writer(writer()).build();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
    }
    
  /* @Bean
	public
    ItemReader<Users> databaseXmlItemReader(DataSource dataSource) {
    	System.out.println("inside reader111....");
        JdbcCursorItemReader<Users> databaseReader = new JdbcCursorItemReader<Users>();
        System.out.println("inside reader....");
        databaseReader.setDataSource(dataSource);
        databaseReader.setSql(QUERY_FIND_USERS);
        databaseReader.setRowMapper(new BeanPropertyRowMapper<>(Users.class));
 
        return databaseReader;
    }*/
    
   /* @Bean
    public ItemReader<Users> productItemReader() throws Exception {
    	  System.out.println("inside reader....");
    	  JdbcPagingItemReader<Users> reader = new JdbcPagingItemReader<Users>();
       // reader.setEntityManagerFactory(emf);
        //reader.setQueryString("select :name from Users");
        reader.setPageSize(5);
        reader.afterPropertiesSet();
        System.out.println(reader);
        return reader;
    }*/
    @Bean
    public ItemReader<Users> productItemReader() throws Exception {
    try
	{
		JdbcPagingItemReader<Users> reader = new JdbcPagingItemReader<>();
		final SqlPagingQueryProviderFactoryBean sqlPagingQueryProviderFactoryBean = new SqlPagingQueryProviderFactoryBean();
		sqlPagingQueryProviderFactoryBean.setDataSource(dataSource);
		sqlPagingQueryProviderFactoryBean.setSelectClause("select id, name");
		sqlPagingQueryProviderFactoryBean.setFromClause("from Users");
		sqlPagingQueryProviderFactoryBean.setSortKey("name");
		reader.setQueryProvider(sqlPagingQueryProviderFactoryBean.getObject());
		reader.setDataSource(dataSource);
		reader.setPageSize(1);
		reader.setRowMapper(new BeanPropertyRowMapper<>(Users.class));
		reader.afterPropertiesSet();
		reader.setSaveState(true);
		//logger.info("Reading users anonymized in chunks of {}", USERS_CHUNK_SIZE);
		return reader;
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}
	return null;
    }
    /*@Bean
    public JpaItemWriter<Users> jpaItemWriter() {
    	System.out.println("inside writer....");
        JpaItemWriter<Users> writer = new JpaItemWriter();
        writer.setEntityManagerFactory(emf);
        return writer;
    }*/
   
  /* @Bean
   public Step step1() {
     try {
    	 System.out.println("inside step 1....");
		return stepBuilderFactory.get("step1").<Users, Users>chunk(10).reader(productItemReader())
		     .writer(writer()).build();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
   }*/
    @Bean
    public MongoItemWriter<Users> writer() {
      MongoItemWriter<Users> writer = new MongoItemWriter<Users>();
      writer.setTemplate(mongoTemplate);
      writer.setCollection("users1");
      return writer;
    }
 
 
  /*  @Bean
    public Job flowJob() {
        return jobBuilderFactory.get("flowJob")
                .incrementer(new RunIdIncrementer())
                .start(flatFileJpaWriterStep())
                .build();
    }*/
    
    
}
