package com.meteor.chat.common.sensitiveword.algorithm.AC;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
@Setter
@Getter
public class ACTrieNode {

    private Map<Character, ACTrieNode> children = new HashMap<>();
    private int depth;
    private boolean isLeafNode;
    private ACTrieNode rollbackNode = null;

    public ACTrieNode childrenOf(Character character) {
        return children.get(character);
    }

    public boolean hasChildren(Character character) {
        return children.containsKey(character);
    }

    public void addChildrenIfAbsent(Character character) {
        children.computeIfAbsent(character, key -> new ACTrieNode());
    }

}
