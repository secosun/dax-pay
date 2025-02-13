package cn.daxpay.single.param.payment.allocation;

import cn.daxpay.single.param.PaymentCommonParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

/**
 * 查询分账接收者参数
 * @author xxm
 * @since 2024/5/20
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Schema(title = "查询分账接收者参数")
public class QueryAllocReceiverParam extends PaymentCommonParam {

    /** 所属通道 */
    @Schema(description = "所属通道")
    @NotEmpty(message = "所属通道必填")
    private String channel;
}
