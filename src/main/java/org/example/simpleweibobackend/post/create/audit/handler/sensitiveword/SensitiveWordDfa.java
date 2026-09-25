package org.example.simpleweibobackend.post.create.audit.handler.sensitiveword;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 敏感词 DFA:词集合按字符逐层注册到节点树,节点即状态、字符即迁移边、词尾节点即接受态;
 * 扫描文本时从每个下标出发沿迁移边走,走到接受态即命中,
 * 匹配复杂度为 文本长度*最长词长,低于逐词 contains 的 词数*文本长度
 */
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
