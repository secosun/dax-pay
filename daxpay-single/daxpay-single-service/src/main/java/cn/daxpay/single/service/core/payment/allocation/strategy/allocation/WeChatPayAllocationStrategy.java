package cn.daxpay.single.service.core.payment.allocation.strategy.allocation;

import cn.daxpay.single.code.PayChannelEnum;
import cn.daxpay.single.exception.pay.PayFailureException;
import cn.daxpay.single.service.core.channel.wechat.entity.WeChatPayConfig;
import cn.daxpay.single.service.core.channel.wechat.service.WeChatPayAllocationService;
import cn.daxpay.single.service.core.channel.wechat.service.WeChatPayConfigService;
import cn.daxpay.single.service.core.payment.sync.result.AllocRemoteSyncResult;
import cn.daxpay.single.service.func.AbsAllocationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * 微信支付分账策略
 * @author xxm
 * @since 2024/4/1
 */
@Slf4j
@Service
@Scope(SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class WeChatPayAllocationStrategy extends AbsAllocationStrategy {

    private final WeChatPayAllocationService weChatPayAllocationService;

    private final WeChatPayConfigService weChatPayConfigService;

    private WeChatPayConfig weChatPayConfig;

    /**
     * 策略标识
     */
    @Override
    public PayChannelEnum getChannel() {
        return PayChannelEnum.WECHAT;
    }

    /**
     * 操作前处理, 校验和初始化支付配置
     */
    @Override
    public void doBeforeHandler() {
        weChatPayConfig = weChatPayConfigService.getAndCheckConfig();
        // 判断是否支持分账
        if (Objects.equals(weChatPayConfig.getAllocation(),false)){
            throw new PayFailureException("微信支付配置不支持分账");
        }
        // 如果分账金额为0, 不发起分账
        if (getAllocationOrder().getAmount() == 0){
            throw new PayFailureException("微信订单的分账比例不正确或订单金额太小, 无法进行分账");
        }
    }

    /**
     * 分账操作
     */
    @Override
    public void allocation() {
        weChatPayAllocationService.allocation(getAllocationOrder(), this.getAllocationOrderDetails(), weChatPayConfig);
    }

    /**
     * 分账完结
     */
    @Override
    public void finish() {
        weChatPayAllocationService.finish(getAllocationOrder(), weChatPayConfig);
    }

    /**
     * 同步状态
     */
    @Override
    public AllocRemoteSyncResult doSync() {
        return weChatPayAllocationService.sync(this.getAllocationOrder(),this.getAllocationOrderDetails(),weChatPayConfig);
    }

}
