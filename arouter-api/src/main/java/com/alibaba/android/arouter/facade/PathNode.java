package com.alibaba.android.arouter.facade;

import com.alibaba.android.arouter.facade.model.RouteMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author bozhao
 * date: 2021/2/4
 */
public class PathNode {
    private static final String REGEX = "^<:[a-zA-Z0-9_]+>$";
    private String name;
    private RouteMeta routeMeta;
    private String paramName;
    private Map<String, PathNode> childNodes;
    private PathNode wildcardNode;

    public PathNode(String name) {
        this.name = name;
    }

    public void putPathNode(String childPath, PathNode childNode) {
        if (childPath == null || childPath.isEmpty() || childNode == null) {
            return;
        }
        if (matchWildcardNode(childPath)) {
            wildcardNode = childNode;
            return;
        }
        if (childNodes == null) {
            childNodes = new HashMap<>();
        }
        childNode.putPathNode(childPath, childNode);
    }

    public PathNode getChildPathNode(String pathNodeString) {
        PathNode pathNode = childNodes == null ? null : childNodes.get(pathNodeString);
        if (pathNode != null) {
            return pathNode;
        }
        return wildcardNode != null && pathNodeString.equals(name) ? wildcardNode : null;
    }

    public PathNode matchPathNode(String pathNodeString) {
        if (pathNodeString == null) {
            return null;
        }
        if (matchWildcardNode(pathNodeString)) {
            return wildcardNode;
        }
        return childNodes == null ? null : childNodes.get(pathNodeString);
    }

    public boolean isWildcardNode() {
        return matchWildcardNode(name);
    }

    public String getParamName() {
        if (paramName != null) {
            return paramName;
        }
        if (isWildcardNode()) {
            //remove wildcard part.
            paramName = name.substring(2, name.length() - 1);
            return paramName;
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public RouteMeta getRouteMeta() {
        return routeMeta;
    }

    private boolean matchWildcardNode(String path) {
        if (path == null) {
            return false;
        }
        return Pattern.matches(REGEX, path);
    }

}
