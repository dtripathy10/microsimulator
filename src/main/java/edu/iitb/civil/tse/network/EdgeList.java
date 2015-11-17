package edu.iitb.civil.tse.network;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Debabrata Tripathy
 */
class EdgeList {

    private Map<Node, Link> inEdgeList;
    private Map<Node, Link> outEdgeList;

    public EdgeList() {
        inEdgeList = new HashMap<>();
        outEdgeList = new HashMap<>();
    }

    public void addInEdgeList(Node node, Link link) {
        inEdgeList.put(node, link);
    }

    public void addOutEdgeList(Node node, Link link) {
        outEdgeList.put(node, link);
    }

    public Map<Node, Link> getInEdgeList() {
        return inEdgeList;
    }

    public Map<Node, Link> getOutEdgeList() {
        return outEdgeList;
    }
}
