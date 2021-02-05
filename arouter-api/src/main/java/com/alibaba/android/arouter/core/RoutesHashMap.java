package com.alibaba.android.arouter.core;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.alibaba.android.arouter.facade.PathNode;
import com.alibaba.android.arouter.facade.model.RouteMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bozhao
 * date: 2021/2/5
 */
public class RoutesHashMap extends HashMap<String, RouteMeta> {
    private final PathNode rootWildcard = new PathNode("root");

    @Nullable
    @Override
    public RouteMeta put(String key, RouteMeta value) {
        if (PathNode.isWildcardPath(key)) {
            PathNode.addPathToRoot(rootWildcard, key, value);
            return value;
        }
        return super.put(key, value);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public RouteMeta putIfAbsent(String key, RouteMeta value) {
        if (PathNode.isWildcardPath(key)) {
            RouteMeta oldValue = rootWildcard.matchPathNodeData(key);
            if (oldValue == null) {
                PathNode.addPathToRoot(rootWildcard, key, value);
                return value;
            }
            return oldValue;
        }
        return super.putIfAbsent(key, value);
    }

    @Override
    public void putAll(@NonNull Map<? extends String, ? extends RouteMeta> m) {
        //super.putAll(m);
        for (Map.Entry<? extends String, ? extends RouteMeta> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Nullable
    @Override
    public RouteMeta get(@Nullable Object key) {
        //hash result
        RouteMeta result = super.get(key);
        if (result == null && key instanceof String) {
            result = rootWildcard.matchPathNodeData((String) key);
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public RouteMeta getOrDefault(@Nullable Object key, @Nullable RouteMeta defaultValue) {
        RouteMeta result = get(key);
        return result == null ? defaultValue : result;
    }

    @Override
    public boolean containsKey(@Nullable Object key) {
        return super.containsKey(key);
    }

    private boolean wildcardContainsKey(@Nullable Object key) {
        if (!(key instanceof String)) {
            return false;
        }
        return rootWildcard.matchPathNodeData((String) key) != null;
    }
}
