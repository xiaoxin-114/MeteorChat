package com.meteor.chat.sensitiveword.core.algorithm.DFA;

import com.meteor.chat.sensitiveword.core.SensitiveWordFilter;
import cn.hutool.core.util.StrUtil;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class DFAFilter implements SensitiveWordFilter {

    private Node root = new Node();
    private final static char replace = '*'; // 替代字符
    private final static String skipChars = " !*-+_=,，.@;:；：。、？?（）()【】[]《》<>“”\"‘’"; // 遇到这些字符就会跳过
    private final static Set<Character> skipSet = new HashSet<>(); // 遇到这些字符就会跳过
    private static DFAFilter instance = null;

    static {
        for(char c : skipChars.toCharArray()) {
            skipSet.add(c);
        }
    }

    public static DFAFilter getInstance() {
        synchronized (DFAFilter.class) {
            if (Objects.isNull(instance)) {
                instance = new DFAFilter();
                instance.loadWord(instance.getWords());
            }
        }
        return instance;
    }

    private List<String> getWords() {
        return Arrays.asList("abcd", "abcbba", "adabca");
    }

    @Override
    public void loadWord(List<String> words) {
        if (CollectionUtils.isEmpty(words)) {
            return;
        }
        words.forEach(str -> loadWord(str));
    }

    private void loadWord(String str) {
        if (StrUtil.isEmpty(str)) {
            return;
        }
        char[] chars = str.toLowerCase(Locale.ROOT).toCharArray();
        Node head = root;
        for (int i = 0; i < chars.length; i++) {
            char aChar = chars[i];
            if (skip(aChar)) {
                continue;
            }
            if (head.children.containsKey(aChar)) {
               head = head.children.get(aChar);
               continue;
            }
            Node node = new Node(aChar);
            head.children.put(aChar, node);
            head = node;
        }
        head.end = true;
    }

    @Override
    public boolean hasSensitiveWord(String content) {
        return Objects.equals(content, filter(content));
    }

    @Override
    public String filter(String content) {
        if (StrUtil.isEmpty(content)) {
            return content;
        }
        StringBuilder stringBuilder = new StringBuilder(content);
        char[] chars = content.toLowerCase(Locale.ROOT).toCharArray();
        for (int i = 0; i < chars.length; i++) {
            Node head = root;
            int index = i;
            StringBuilder replaceSb = new StringBuilder();
            while(index < chars.length) {
                char c = chars[index];
                if (skip(c)) {
                    continue;
                }
                if (!head.children.containsKey(c)) {
                    break;
                }
                head = head.children.get(c);
                replaceSb.append(replace);
                if (head.end) {
                    stringBuilder = stringBuilder.replace(i, index + 1, replaceSb.toString());
                    i = index;
                    break;
                }
                index++;
            }
        }
        return stringBuilder.toString();
    }
    /**
     * 判断是否需要跳过当前字符
     *
     * @param c 待检测字符
     * @return true: 需要跳过, false: 不需要跳过
     */
    private boolean skip(char c) {
        return skipSet.contains(c);
    }

    private static class Node{
        char value;
        Map<Character, Node> children;
        boolean end;
        public Node(){
            this.children = new HashMap<>();
        }
        public Node(char value) {
            this.value = value;
            this.children = new HashMap<>();
        }
    }

}
