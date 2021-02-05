package com.zypw.zypwcommon.entity.responseEntity;

import java.util.HashMap;

/**
 * 自定义：全局AXIOS请求响应实体
 *
 * */
public class AxiosResult extends HashMap<String,Object> {

    private static final long serialVersionUID = 1L;

    private static final String CODE_TAG = "code";
    private static final String MSG_TAG = "msg";
    private static final String DATA_TAG = "data";

    public AxiosResult(){};

    public AxiosResult(ResponseResult responseResult) {
        super.put(CODE_TAG,responseResult.getResponseCode());
        super.put(MSG_TAG,responseResult.getResponseMsg());
    };

    public AxiosResult(Integer code,String msg){
        super.put(CODE_TAG,code);
        super.put(MSG_TAG,msg);
    }

    public AxiosResult(Integer code,String msg,Object data){
        super.put(CODE_TAG,code);
        super.put(MSG_TAG,msg);
        super.put(DATA_TAG,data);
    }



    /**
     * 只返回成功消息
     * @return 响应实体
     */
    public static AxiosResult success(){
        return AxiosResult.success(ResponseResult.SUCCESS.getResponseMsg());
    }


    /**
     * 返回数据
     *
     * @param data 数据对象
     * @return 响应实体
     */
    public static AxiosResult success(Object data){
        return AxiosResult.success(ResponseResult.SUCCESS.getResponseMsg(),data);
    }

    /** 自定义返回消息
     *
     * @param msg 返回内容
     * @return 响应实体
     */
    public static AxiosResult success(String msg){
        return  AxiosResult.success(msg,null);
    }

    /**
     * 返回自定义消息和数据
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 响应实体
     */
    public static AxiosResult success(String msg,Object data){
        return new AxiosResult(ResponseResult.SUCCESS.getResponseCode(),msg,data);
    }

    /**
     * 返回默认消息
     *
     * @return 响应实体
     */
    public static AxiosResult error(){
        return AxiosResult.error(ResponseResult.FAIL.getResponseMsg());
    }

    /**
     * 自定义返回消息
     *
     * @param msg 返回内容
     * @return 响应实体
     */
    public static AxiosResult error(String msg){
        return AxiosResult.error(msg,null);
    }

    /**
     * 返回自定义消息和数据
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 响应实体
     */
    public static AxiosResult error(String msg,Object data){
        return new AxiosResult(ResponseResult.FAIL.getResponseCode(),msg,data);
    }
}
