package cn.daxpay.single.service.core.payment.sync.strategy.pay;


import cn.daxpay.single.code.PayChannelEnum;
import cn.daxpay.single.service.core.channel.alipay.entity.AliPayConfig;
import cn.daxpay.single.service.core.channel.alipay.service.AliPayConfigService;
import cn.daxpay.single.service.core.channel.alipay.service.AliPaySyncService;
import cn.daxpay.single.service.func.AbsPaySyncStrategy;
import cn.daxpay.single.service.core.payment.sync.result.PayRemoteSyncResult;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * 支付宝支付同步
 * @author xxm
 * @since 2023/7/14
 */
@Scope(SCOPE_PROTOTYPE)
@Component
@RequiredArgsConstructor
public class AliPaySyncStrategy extends AbsPaySyncStrategy {

    private final AliPayConfigService alipayConfigService;

    private final AliPaySyncService alipaySyncService;

    /**
     * 策略标识
     */
    @Override
    public PayChannelEnum getChannel() {
        return PayChannelEnum.ALI;
    }

    /**
     * 异步支付单与支付网关进行状态比对
     */
    @Override
    public PayRemoteSyncResult doSyncStatus() {
        this.initAlipayConfig();
        return alipaySyncService.syncPayStatus(this.getOrder());
    }

    /**
     * 初始化支付宝配置信息
     */
    private void initAlipayConfig() {
        // 检查并获取支付宝支付配置
        AliPayConfig config = alipayConfigService.getConfig();
        alipayConfigService.initConfig(config);
    }
}
