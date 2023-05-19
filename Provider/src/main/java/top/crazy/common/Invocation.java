package top.crazy.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.common
 * @Author: crazyPang
 * @CreateTime: 2023-05-13  23:18
 * @Description: TODO
 */
@Data
public class Invocation implements Serializable{

    private String ReqId;

    private static final long serialVersionUID = 1L;

    private String interfaceName;

    private String methodName;

    private Class[] parameterTypes;

    private Object[]  parameters;

    public Invocation(String interfaceName, String methodName, Class[] parameterTypes, Object[] parameters) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.parameters = parameters;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }


    public String getReqId() {
        return ReqId;
    }

    public void setReqId(String reqId) {
        ReqId = reqId;
    }
}
