package top.crazy.loadBalance;

import lombok.AllArgsConstructor;
import lombok.Data;
import top.crazy.common.RegisterMessageType;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.loadBalance
 * @Author: crazyPang
 * @CreateTime: 2023-05-18  23:11
 * @Description: TODO
 */

@AllArgsConstructor
public enum LoadBalanceType {

    RANDOM(1),
    WEIGHT(2),
    ERROR(99);
    private Integer type;

    public static LoadBalanceType match(Integer type){
        for(LoadBalanceType value:LoadBalanceType.values()){
            if(value.getType().equals(type)){
                return value;
            }
        }
        return ERROR;
    }

    public Integer getType() {
        return type;
    }
}
