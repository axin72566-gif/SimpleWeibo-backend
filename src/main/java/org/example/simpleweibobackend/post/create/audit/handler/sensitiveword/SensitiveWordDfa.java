package org.example.simpleweibobackend.post.create.audit.handler.sensitiveword;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** 敏感词 DFA:节点树匹配,文本命中任一敏感词即返回 true */
public class SensitiveWordDfa {

    private final DfaNode root;

    private SensitiveWordDfa(DfaNode root) {
        this.root = root;
    }

    public static SensitiveWordDfa of(Set<String> words) {
        DfaNode root = new DfaNode();
        for (String word : words) {
            if (word == null || word.isEmpty()) {
                continue;
            }
            DfaNode current = root;
            for (char c : word.toCharArray()) {
                current = current.children.computeIfAbsent(c, k -> new DfaNode());
            }
            current.end = true;
        }
        return new SensitiveWordDfa(root);
    }

    public boolean containsAny(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        for (int start = 0; start < text.length(); start++) {
            DfaNode current = root;
            for (int index = start; index < text.length(); index++) {
                DfaNode next = current.children.get(text.charAt(index));
                if (next == null) {
                    break;
                }
                current = next;
                if (current.end) {
                    return true;
                }
            }
        }
        return false;
    }

    private static final class DfaNode {

        private final Map<Character, DfaNode> children = new HashMap<>();

        private boolean end;
    }
}
