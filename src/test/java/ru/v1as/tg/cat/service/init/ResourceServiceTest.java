package ru.v1as.tg.cat.service.init;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.v1as.tg.cat.config.JpaConfiguration;
import ru.v1as.tg.cat.jpa.dao.ResourceDao;
import ru.v1as.tg.cat.jpa.entities.resource.ResourceEntity;
import ru.v1as.tg.cat.service.init.ResourceServiceTest.ResourceServiceTestConf;

@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
@SpringBootTest(classes = ResourceServiceTestConf.class)
public class ResourceServiceTest {

    @Autowired ResourceService resourceService;
    @Autowired ResourceDao resourceDao;

    @Test
    public void shouldSaveInitResources() {
        final List<ResourceEntity> resources = resourceDao.findAll();
        Assert.assertEquals(ResourceService.KNOWN_RESOURCES.size(), resources.size());
    }

    @Import(JpaConfiguration.class)
    public static class ResourceServiceTestConf {
        @Bean
        public ResourceService getResourceService(ResourceDao resourceDao) {
            return new ResourceService(resourceDao);
        }
    }
}
