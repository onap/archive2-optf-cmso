/*
 * Copyright © 2017-2018 AT&T Intellectual Property.
 * Modifications Copyright © 2018 IBM.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * 
 * Unless otherwise specified, all documentation contained herein is licensed
 * under the Creative Commons License, Attribution 4.0 Intl. (the "License");
 * you may not use this documentation except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         https://creativecommons.org/licenses/by/4.0/
 * 
 * Unless required by applicable law or agreed to in writing, documentation
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.onap.optf.cmso;

import org.onap.optf.cmso.eventq.CmQuartzJob;
import org.onap.optf.cmso.optimizer.OptimizerQuartzJob;
import org.onap.optf.cmso.sostatus.ScheduleStatusJob;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan({"org.onap.optf.cmso"})
@EnableTransactionManagement
public class CMSQuartzConfiguration {

    @Autowired
    Environment env;

    @Bean
    public SimpleTriggerFactoryBean eventqTriggerFactoryBean() {

        Integer interval = env.getProperty("cmso.cm.polling.job.interval.ms", Integer.class, 60000);
        SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(eventqDetailFactoryBean().getObject());
        stFactory.setStartDelay(3000);
        stFactory.setRepeatInterval(interval);
        // Indefinitely
        return stFactory;
    }

    @Bean
    public JobDetailFactoryBean eventqDetailFactoryBean() {
        JobDetailFactoryBean factory = new JobDetailFactoryBean();
        factory.setJobClass(CmQuartzJob.class);
        return factory;
    }

    @Bean
    public SimpleTriggerFactoryBean statusTriggerFactoryBean() {

        Integer interval = env.getProperty("cmso.status.job.interval.ms", Integer.class, 60000);
        SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(statusDetailFactoryBean().getObject());
        stFactory.setStartDelay(3000);
        stFactory.setRepeatInterval(interval);
        // Indefinitely
        return stFactory;
    }

    @Bean
    public JobDetailFactoryBean statusDetailFactoryBean() {
        JobDetailFactoryBean factory = new JobDetailFactoryBean();
        factory.setJobClass(ScheduleStatusJob.class);
        return factory;
    }

    @Bean
    public SimpleTriggerFactoryBean optimizerTriggerFactoryBean() {

        Integer interval = env.getProperty("cmso.optimizer.job.interval.ms", Integer.class, 60000);
        SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(optimizerDetailFactoryBean().getObject());
        stFactory.setStartDelay(3000);
        stFactory.setRepeatInterval(interval);
        // Indefinitely
        return stFactory;
    }

    @Bean
    public JobDetailFactoryBean optimizerDetailFactoryBean() {
        JobDetailFactoryBean factory = new JobDetailFactoryBean();
        factory.setJobClass(OptimizerQuartzJob.class);
        return factory;
    }

    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean cmsoFactoryBean(JobFactory jobFactory) {
        SchedulerFactoryBean cmso = new SchedulerFactoryBean();
        cmso.setJobFactory(jobFactory);
        cmso.setTriggers(eventqTriggerFactoryBean().getObject(), optimizerTriggerFactoryBean().getObject(),
                statusTriggerFactoryBean().getObject());
        return cmso;
    }
}
