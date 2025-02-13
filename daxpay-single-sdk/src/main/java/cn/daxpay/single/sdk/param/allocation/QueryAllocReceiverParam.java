package cn.daxpay.single.sdk.param.allocation;

import cn.daxpay.single.sdk.model.allocation.AllocReceiversModel;
import cn.daxpay.single.sdk.net.DaxPayRequest;
import cn.daxpay.single.sdk.response.DaxPayResult;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 查询分账接收者参数
 * @author xxm
 * @since 2024/5/20
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class QueryAllocReceiverParam extends DaxPayRequest<AllocReceiversModel> {

    /** 所属通道 */
    private String channel;

    @Override
    public String path() {
        return "/unipay/query/allocReceiver";
    }

    @Override
    public DaxPayResult<AllocReceiversModel> toModel(String json) {
        return JSONUtil.toBean(json, new TypeReference<DaxPayResult<AllocReceiversModel>>() {}, false);
    }
}
