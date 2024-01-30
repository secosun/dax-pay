package cn.bootx.platform.daxpay.service.func;

import cn.bootx.platform.daxpay.code.PayRefundStatusEnum;
import cn.bootx.platform.daxpay.code.PayStatusEnum;
import cn.bootx.platform.daxpay.param.pay.RefundChannelParam;
import cn.bootx.platform.daxpay.param.pay.RefundParam;
import cn.bootx.platform.daxpay.service.core.order.pay.entity.PayChannelOrder;
import cn.bootx.platform.daxpay.service.core.order.pay.entity.PayOrder;
import cn.bootx.platform.daxpay.service.core.order.refund.entity.PayRefundChannelOrder;
import cn.bootx.platform.daxpay.service.core.order.refund.entity.PayRefundOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 抽象支付退款策略基类
 *
 * @author xxm
 * @since 2020/12/11
 */
@Getter
@Setter
public abstract class AbsRefundStrategy implements PayStrategy{

    /** 支付订单 */
    private PayOrder payOrder = null;

    /** 退款订单 已经持久化, 后续需要更新 */
    private PayRefundOrder refundOrder = null;

    /** 当前通道的订单 */
    private PayChannelOrder payChannelOrder = null;

    /** 退款参数 */
    private RefundParam refundParam = null;

    /** 当前通道的退款参数 退款参数中的与这个不一致, 以这个为准 */
    private RefundChannelParam refundChannelParam = null;

    /** 当前通道的退款订单 未持久化, 需要后续更新 */
    private PayRefundChannelOrder refundChannelOrder;

    /**
     * 初始化参数
     */
    public void initRefundParam(PayOrder payOrder, RefundParam refundParam, PayChannelOrder payChannelOrder) {
        this.payOrder = payOrder;
        this.payChannelOrder = payChannelOrder;
        this.refundParam = refundParam;
    }


    /**
     * 退款前预扣通道和支付订单的金额
     */
    public void doPreDeductOrderHandler(){
        PayChannelOrder payChannelOrder = this.getPayChannelOrder();
        int refundableBalance = payChannelOrder.getRefundableBalance() - this.getRefundChannelParam().getAmount();
        payChannelOrder.setRefundableBalance(refundableBalance)
                .setStatus(PayStatusEnum.REFUNDING.getCode());
    }

    /**
     * 退款前对处理
     */
    public void doBeforeRefundHandler() {}

    /**
     * 退款操作
     */
    public abstract void doRefundHandler();

    /**
     * 退款发起成功操作, 异步支付通道需要进行重写
     */
    public void doSuccessHandler() {
        // 更新退款订单数据状态
        this.refundChannelOrder.setStatus(PayRefundStatusEnum.SUCCESS.getCode())
                .setRefundTime(LocalDateTime.now());

        // 支付通道订单可退余额
        int refundableBalance = this.getPayChannelOrder().getRefundableBalance() - this.refundChannelOrder.getAmount();
        // 支付通道订单状态
        PayStatusEnum status = refundableBalance == 0 ? PayStatusEnum.REFUNDED : PayStatusEnum.PARTIAL_REFUND;
        this.payChannelOrder.setRefundableBalance(refundableBalance)
                .setStatus(status.getCode());
    }

    /**
     * 生成通道退款订单对象
     */
    public void generateChannelOrder() {
        this.refundChannelOrder = new PayRefundChannelOrder()
                .setPayChannelId(this.getPayChannelOrder().getId())
                .setAsync(this.getPayChannelOrder().isAsync())
                .setChannel(this.getPayChannelOrder().getChannel())
                .setOrderAmount(this.getPayChannelOrder().getAmount())
                .setAmount(this.getRefundChannelParam().getAmount());
    }

}
