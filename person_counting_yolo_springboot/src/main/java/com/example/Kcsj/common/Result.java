package com.example.Kcsj.common;

/**
 * 通用响应结果类
 * <p>
 * 用于封装API接口的返回结果，提供统一的响应格式。
 * 包含状态码(code)、消息(msg)和数据(data)三个核心字段。
 * </p>
 *
 * <p>
 * 使用场景：
 * <ul>
 *     <li>成功响应：通过success()方法创建，包含数据</li>
 *     <li>错误响应：通过error()方法创建，包含错误码和消息</li>
 * </ul>
 * </p>
 *
 * <p>
 * 设计特点：
 * <ul>
 *     <li>泛型支持：data字段支持任意类型(T)</li>
 *     <li>静态工厂方法：提供success()和error()快速创建实例</li>
 *     <li>默认成功码："0"表示成功</li>
 * </ul>
 * </p>
 *
 * @param <T> 数据类型参数，表示返回的数据内容
 * @author raymond
 * @version 1.0
 * @since 2024-12-07
 */


public class Result<T> {
    /**
     * 状态码
     * <p>
     * 表示请求处理的结果状态，通常：
     * <ul>
     *     <li>"0" - 成功</li>
     *     <li>其他值 - 错误码</li>
     * </ul>
     * </p>
     */
    private String code;

    /**
     * 消息提示
     * <p>
     * 对状态码的补充说明，提供更详细的错误信息或成功提示
     * </p>
     */
    private String msg;

    /**
     * 响应数据
     * <p>
     * 实际返回的业务数据，类型由泛型T决定
     * </p>
     */
    private T data;


    /**
     * 状态码
     * @return 状态码字符串
     */
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 消息提示
     * @return 消息提示字符串
     */
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 获取响应数据
     * @return 响应数据对象
     */
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 无参构造方法
     * 创建一个空的Result对象
     */
    public Result() {
    }
    /**
     * 带数据参数的构造方法
     * @param data 要封装的数据
     */
    public Result(T data) {
        this.data = data;
    }

    /**
     * 创建成功响应（无数据）
     * @return 成功的Result对象，data为null
     */
    public static Result success() {
        Result result = new Result<>();
        result.setCode("0");
        result.setMsg("成功");
        return result;
    }

    /**
     * 创建成功响应（带数据）
     * @param data 要返回的数据
     * @param <T> 数据类型
     * @return 成功的Result对象，包含指定数据
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>(data);
        result.setCode("0");
        result.setMsg("成功");
        return result;
    }

    /**
     * 创建错误响应
     * @param code 错误码
     * @param msg 错误消息
     * @return 错误的Result对象
     */
    public static Result error(String code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
