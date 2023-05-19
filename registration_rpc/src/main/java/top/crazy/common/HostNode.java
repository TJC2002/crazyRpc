package top.crazy.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.common
 * @Author: crazyPang
 * @CreateTime: 2023-05-16  21:34
 * @Description: TODO
 */

@Data
@AllArgsConstructor
public class HostNode {
    private String hostName;
    private String host;
    private String port;
    private Integer weight;
}
