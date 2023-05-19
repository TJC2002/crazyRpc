package top.crazy.common;

import java.io.Serializable;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.common
 * @Author: crazyPang
 * @CreateTime: 2023-05-14  19:34
 * @Description: TODO
 */
public class RpcResult implements Serializable {
    private String ReqId;
    private Object result;
    private Integer code;
    private String message;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getReqId() {
        return ReqId;
    }

    public void setReqId(String reqId) {
        ReqId = reqId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
