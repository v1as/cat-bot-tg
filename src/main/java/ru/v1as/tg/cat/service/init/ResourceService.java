package ru.v1as.tg.cat.service.init;

import static ru.v1as.tg.cat.EmojiConst.MONEY_BAG;
import static ru.v1as.tg.cat.jpa.entities.resource.ResourceType.COUNTABLE;

import com.google.common.collect.ImmutableList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.jpa.dao.ResourceDao;
import ru.v1as.tg.cat.jpa.entities.resource.ResourceEntity;

@Component
@RequiredArgsConstructor
public class ResourceService {

    public static final ResourceEntity MONEY =
            new ResourceEntity(1L, "Деньги", COUNTABLE, MONEY_BAG);
    public static final List<ResourceEntity> KNOWN_RESOURCES =
            ImmutableList.<ResourceEntity>builder().add(MONEY).build();
    private final ResourceDao resourceDao;

    @PostConstruct
    public void init() {
        final Set<ResourceEntity> resources = new HashSet<>(resourceDao.findAll());
        final List<ResourceEntity> toSave =
                KNOWN_RESOURCES.stream()
                        .filter(r -> !resources.contains(r))
                        .collect(Collectors.toList());
        resourceDao.saveAll(toSave);
    }
}
