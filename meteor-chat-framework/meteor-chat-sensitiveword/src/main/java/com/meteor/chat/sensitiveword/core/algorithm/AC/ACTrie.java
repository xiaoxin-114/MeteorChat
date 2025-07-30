package com.meteor.chat.sensitiveword.core.algorithm.AC;

import cn.hutool.core.util.StrUtil;

import java.util.*;
import java.util.stream.Collectors;

public class ACTrie {

    private ACTrieNode root;
    private final static String skipChars = " !*-+_=,，.@;:；：。、？?（）()【】[]《》<>“”\"‘’"; // 遇到这些字符就会跳过
    private final static Set<Character> skipSet = new HashSet<>(); // 遇到这些字符就会跳过
    static {
        for (char c : skipChars.toCharArray()) {
            skipSet.add(c);
        }
    }

    public ACTrie(List<String> words) {
        words = words.stream().distinct().collect(Collectors.toList()); // 去重
        root = new ACTrieNode();
        for (String word : words) {
            addWord(word);
        }
        initFailover();
    }

    /**
     * 加载敏感词，构建树
     * @param word 敏感词
     */
    public void addWord(String word) {
        // 先转小写
        word = word.toLowerCase(Locale.ROOT);
        ACTrieNode head = root;
        for(int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            head.addChildrenIfAbsent(c);
            head = head.childrenOf(c);
            head.setDepth(i + 1);
        }
        head.setLeafNode(true);
    }

    /**
     * 为各个节点构建失败回退节点
     */
    private void initFailover() {
        Queue<ACTrieNode> queue = new ArrayDeque<>();
        queue.offer(root);
        // 借用queue结构进行树的层级遍历
        while(!queue.isEmpty()) {
            ACTrieNode parentNode = queue.poll();
            parentNode.getChildren().forEach((key, node) -> {
                // 所有节点的回退节点默认是父节点的回退节点
                ACTrieNode rollbackNode = parentNode.getRollbackNode();
                // 如果一直匹配不上，则按照父节点的回退节点不断回退，直到回退到根节点或者匹配上
                while(!Objects.isNull(rollbackNode) && !rollbackNode.hasChildren(key)) {
                    rollbackNode = rollbackNode.getRollbackNode();
                }
                // 一直没匹配上，那么回退节点就是根节点
                if (Objects.isNull(rollbackNode)) {
                    node.setRollbackNode(root);
                } else {
                    // 匹配上了，那么回退节点就是匹配上的那个节点
                    node.setRollbackNode(rollbackNode.childrenOf(key));
                }
                queue.offer(node);
            });
        }
    }

    public List<MatchResult> filter(String content) {
        content = content.toLowerCase(Locale.ROOT);
        ArrayList<MatchResult> result = new ArrayList<>();
        if (StrUtil.isEmpty(content)) {
            return result;
        }
        ACTrieNode head = root;
        int[] skipCountArr = new int[content.length()];
        int skipCount = 0;
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            // 如果包含其余字符则跳过
            if (skip(c)) {
                skipCountArr[i] = ++skipCount;
                continue;
            }
            skipCountArr[i] = skipCount;
            // 如果匹配不上当前字符，就按照回退节点回退
            while (Objects.nonNull(head.getRollbackNode()) && !head.hasChildren(c)) {
                head = head.getRollbackNode();
            }
            // 如果匹配上了
            if (head.hasChildren(c)) {
                // 向下个节点继续匹配
                head = head.childrenOf(c);
                // 如果当前匹配上的节点是叶子节点，需要根据深度记录匹配的结果
                if (head.isLeafNode()) {
                    int depth = head.getDepth();
                    // 计算这敏感词匹配之间有多少个跳过的字符，扩大屏蔽的范围，保证包敏感词内容都屏蔽
                    int start = i + 1 - depth - skipCountArr[i];
                    result.add(new MatchResult(start, i + 1));
                }
            }
        }
        return result;
    }

    private boolean skip(Character c) {
        return skipSet.contains(c);
    }
}
