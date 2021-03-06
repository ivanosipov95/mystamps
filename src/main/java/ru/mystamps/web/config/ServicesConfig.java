/*
 * Copyright (C) 2009-2015 Slava Semushin <slava.semushin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package ru.mystamps.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableAsync;

// CheckStyle: ignore AvoidStarImportCheck for next 2 lines
import ru.mystamps.web.dao.*; // NOPMD: UnusedImports
import ru.mystamps.web.service.*; // NOPMD: UnusedImports
import ru.mystamps.web.support.spring.security.SecurityConfig;

@Configuration
@EnableAsync
public class ServicesConfig {
	
	@Autowired
	private DaoConfig daoConfig;
	
	@Autowired
	private SecurityConfig securityConfig;
	
	@Autowired
	private SeriesDao seriesDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private UsersActivationDao usersActivationDao;
	
	@Autowired
	private StrategiesConfig strategiesConfig;
	
	@Autowired
	private ImageDao imageDao;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private Environment env;
	
	@Autowired
	private MessageSource messageSource;
	
	@Bean
	public CountryService getCountryService() {
		return new CountryServiceImpl(daoConfig.getJdbcCountryDao());
	}
	
	@Bean
	public CategoryService getCategoryService() {
		return new CategoryServiceImpl(daoConfig.getJdbcCategoryDao());
	}
	
	@Bean
	public CollectionService getCollectionService() {
		return new CollectionServiceImpl(daoConfig.getJdbcCollectionDao());
	}
	
	@Bean
	public CronService getCronService() {
		return new CronServiceImpl(usersActivationDao, getUsersActivationService());
	}
	
	@Bean
	public ImageService getImageService() {
		return new ImageServiceImpl(strategiesConfig.getImagePersistenceStrategy(), imageDao);
	}
	
	@Bean
	public MailService getMailService() {
		boolean isProductionEnvironment = env.acceptsProfiles("prod");
		boolean enableTestMode = !isProductionEnvironment;
		
		return new MailServiceImpl(
			mailSender,
			messageSource,
			env.getRequiredProperty("app.mail.robot.email"),
			enableTestMode);
	}
	
	@Bean
	public UsersActivationService getUsersActivationService() {
		return new UsersActivationServiceImpl(
			daoConfig.getJdbcUsersActivationDao(),
			getMailService()
		);
	}
	
	@Bean
	public SeriesService getSeriesService() {
		return new SeriesServiceImpl(seriesDao, daoConfig.getJdbcSeriesDao(), getImageService());
	}
	
	@Bean
	public SiteService getSiteService() {
		return new SiteServiceImpl(daoConfig.getSuspiciousActivityDao());
	}
	
	@Bean
	public UserService getUserService() {
		return new UserServiceImpl(
			userDao,
			daoConfig.getJdbcUserDao(),
			getUsersActivationService(),
			getCollectionService(),
			securityConfig.getPasswordEncoder()
		);
	}
	
}
