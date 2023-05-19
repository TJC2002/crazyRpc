package top.crazy.loadBalance;

import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

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
    ROUND_ROBIN(2),
    WEIGHT_ROUND_ROBIN(3),
    WEIGHT_RANDOM(4),
    SOURCE_HASH(5),
    ERROR(99);
    private Integer type;


    public static Map<String,LoadBalanceType> STRING_LOAD_BALANCE = new HashMap<>();
    static {
        STRING_LOAD_BALANCE.put("random",RANDOM);
        STRING_LOAD_BALANCE.put("round_robin",ROUND_ROBIN);
        STRING_LOAD_BALANCE.put("weight_round_robin",WEIGHT_ROUND_ROBIN);
        STRING_LOAD_BALANCE.put("weight_random",WEIGHT_RANDOM);
        STRING_LOAD_BALANCE.put("source_hash",SOURCE_HASH);
    }
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
