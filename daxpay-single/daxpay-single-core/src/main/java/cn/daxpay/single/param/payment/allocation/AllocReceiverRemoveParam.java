package cn.daxpay.single.param.payment.allocation;

import cn.daxpay.single.param.PaymentCommonParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

/**
 * 分账接收者删除参数
 * @author xxm
 * @since 2024/5/20
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Schema(title = "分账接收者删除参数")
public class AllocReceiverRemoveParam extends PaymentCommonParam {

    @Schema(description = "接收者编号")
    @NotEmpty(message = "接收者编号必填")
    private String receiverNo;

}
