package top.crazy.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.common
 * @Author: crazyPang
 * @CreateTime: 2023-05-16  23:07
 * @Description: TODO
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRespMessage {

    private Object result;

    private String message;

    private Integer type;
}
