package org.example.simpleweibobackend.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 全局错误码
 *
 * <p>业务码分段规则: 前三位对应 HTTP 状态, 后两位为段内序号
 * <ul>
 *   <li>400xx - 请求参数错误</li>
 *   <li>401xx - 认证失败(未登录/账号密码错误)</li>
 *   <li>404xx - 资源不存在</li>
 *   <li>409xx - 资源冲突(已存在/重复操作)</li>
 *   <li>422xx - 语义校验失败(如内容审核拒绝)</li>
 *   <li>500xx - 服务端内部错误</li>
 * </ul>
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    PARAM_ERROR(40000, 400, "请求参数错误"),
    CANNOT_FOLLOW_SELF(40001, 400, "不能关注自己"),
    UNAUTHORIZED(40100, 401, "未登录或登录已过期"),
    LOGIN_FAILED(40101, 401, "用户名或密码错误"),
    USER_NOT_FOUND(40400, 404, "用户不存在"),
    POST_NOT_FOUND(40401, 404, "帖子不存在"),
    USERNAME_EXISTS(40900, 409, "用户名已存在"),
    REPEAT_LIKE(40901, 409, "请勿重复点赞"),
    REPEAT_FOLLOW(40902, 409, "请勿重复关注"),
    AUDIT_REJECTED(42200, 422, "内容未通过审核"),
    INTERNAL_ERROR(50000, 500, "服务器内部错误"),
    LOGOUT_FAILED(50001, 500, "退出登录失败");

    private final Integer code;

    private final Integer httpStatus;

    private final String message;
}
