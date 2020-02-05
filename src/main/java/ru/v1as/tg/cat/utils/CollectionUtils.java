package ru.v1as.tg.cat.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Iterator;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CollectionUtils {

    public static <T> T first(Collection<T> values) {
        checkNotNull(values, "Can't get first element from null");
        final Iterator<T> iterator = values.iterator();
        checkArgument(iterator.hasNext(), "Can't get first element from empty collection");
        return iterator.next();
    }

}
