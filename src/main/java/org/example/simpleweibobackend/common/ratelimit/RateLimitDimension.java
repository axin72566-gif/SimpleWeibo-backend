package org.example.simpleweibobackend.common.ratelimit;

/**
 * 限流维度,决定限流 key 中拼接的对象标识
 */
public enum RateLimitDimension {

    /**
     * 接口级:所有请求共享同一个配额,只按方法区分
     */
    INTERFACE,

    /**
     * 用户级:按 X-User-Id 请求头区分,每个用户独立配额;无登录态时退化为 IP
     */
    USER,

    /**
     * IP 级:按客户端 IP 区别,适用于注册/登录等匿名接口
     */
    IP
}
