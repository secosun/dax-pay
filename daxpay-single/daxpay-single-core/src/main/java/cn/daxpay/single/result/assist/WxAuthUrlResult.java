package cn.daxpay.single.result.assist;

import cn.daxpay.single.result.PaymentCommonResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 微信oauth2授权的url连接
 * @author xxm
 * @since 2024/2/10
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Schema(title = "微信oauth2授权的url")
public class WxAuthUrlResult extends PaymentCommonResult {

    @Schema(description = "微信oauth2授权的url")
    private String url;
}
