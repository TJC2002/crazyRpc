package top.crazy.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.common
 * @Author: crazyPang
 * @CreateTime: 2023-05-16  23:14
 * @Description: TODO
 */

@AllArgsConstructor
@Getter
public enum RegisterRespMessageType {
    SUCCESS(200),
    WITHOUT_HOST_NODE(400),
    UN_KNOW_SERVICE_NAME(300),
    UN_KNOW_WRONG(500);
    private Integer type;

    public static RegisterRespMessageType match(Integer type){
        for(RegisterRespMessageType value:RegisterRespMessageType.values()){
            if(value.getType().equals(type)){
                return value;
            }
        }
        return UN_KNOW_WRONG;
    }
}
