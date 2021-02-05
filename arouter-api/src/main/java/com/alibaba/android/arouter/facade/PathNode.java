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

    public static void addPathToRoot(PathNode root, String path, RouteMeta routeMeta) {
        if (root == null || path == null) {
            return;
        }
        String[] nodes = buildPathArray(path);
        if (nodes == null) {
            return;
        }
        PathNode pathNode = root;
        for (int i = 0; i < nodes.length; i++) {
            String nodePath = nodes[i];
            PathNode curPath = pathNode.getChildPathNode(nodePath);
            if (curPath == null) {
                curPath = new PathNode(nodePath);
                pathNode.putPathNode(nodePath, curPath);
            }
            pathNode = curPath;
        }
        pathNode.setRouteMeta(routeMeta);
    }

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

    public RouteMeta matchPathNodeData(String path) {
        if (path == null) {
            return null;
        }
        PathNode dest = this;
        String[] destPath = buildPathArray(path);
        Map<String, String> paramsMap = new HashMap<>();
        for (String node : destPath) {
            PathNode cur = dest.matchPathNode(node);
            if (cur == null) {
                break;
            }
            if (cur.isWildcardNode() && cur.getParamName() != null) {
                paramsMap.put(cur.getParamName(), node);
            }
            dest = cur;
        }
        RouteMeta routeMeta = dest.getRouteMeta();
        return routeMeta;
    }

    private PathNode matchPathNode(String pathNodeString) {
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

    public void setRouteMeta(RouteMeta routeMeta) {
        this.routeMeta = routeMeta;
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

    private static String[] buildPathArray(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.lastIndexOf("/"));
        }
        return path.split("/");
    }

}
