package top.crazy.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.common
 * @Author: crazyPang
 * @CreateTime: 2023-05-16  21:34
 * @Description: 节点类 存储节点的 host port httpclient 发起请求
 */

@Data
@AllArgsConstructor
public class HostNode {
    private String hostName;
    private String host;
    private String port;
    private LocalDateTime expireTime;
    private Integer weight;
}
