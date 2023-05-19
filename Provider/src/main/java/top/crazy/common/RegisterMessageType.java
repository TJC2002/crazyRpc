package top.crazy.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RegisterMessageType {

    REGISTER_HOST_NODE(1),
    LOSS_CONNECT(2),
    GET_HOST_NODE(3),
    ERROR(99);
    private Integer type;

    public static RegisterMessageType match(Integer type){
        for(RegisterMessageType value:RegisterMessageType.values()){
            if(value.getType().equals(type)){
                return value;
            }
        }
        return ERROR;
    }
}
