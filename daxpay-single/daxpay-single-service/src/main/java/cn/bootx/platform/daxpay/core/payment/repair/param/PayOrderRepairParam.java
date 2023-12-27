package cn.bootx.platform.daxpay.core.payment.repair.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 支付订单修复参数
 * @author xxm
 * @since 2023/12/27
 */
@Data
@Accessors(chain = true)
public class PayOrderRepairParam {

    @Schema(description = "支付ID")
    private Long paymentId;


}
