package top.crazy.common;

import lombok.Data;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy
 * @Author: crazyPang
 * @CreateTime: 2023-05-16  21:28
 * @Description: TODO
 */

@Data
public class RegisterMessage {
    public String message;
    private String serviceName;
    private String host;
    private String  port;
    private String hostName;
    private Integer registerMessageType;
    private Integer loadBalanceWeight;
}
