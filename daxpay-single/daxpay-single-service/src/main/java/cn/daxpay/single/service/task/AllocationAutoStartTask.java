package cn.daxpay.single.service.task;

import cn.daxpay.single.param.payment.allocation.AllocationParam;
import cn.daxpay.single.service.core.order.pay.dao.PayOrderManager;
import cn.daxpay.single.service.core.order.pay.entity.PayOrder;
import cn.daxpay.single.service.core.payment.allocation.service.AllocationService;
import cn.daxpay.single.util.OrderNoGenerateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

/**
 * 自动分账定时任务, 10s一次
 * @author xxm
 * @since 2024/5/6
 */
@Slf4j
@Component
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@RequiredArgsConstructor
public class AllocationAutoStartTask implements Job {
    private final PayOrderManager payOrderManager;
    private final AllocationService allocationService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        for (PayOrder payOrder : payOrderManager.findAutoAllocation()) {
            AllocationParam param = new AllocationParam();
            param.setBizAllocationNo(OrderNoGenerateUtil.allocation());
            try {
                allocationService.allocation(param, payOrder);
            } catch (Exception e) {
                log.warn("自动分账失败, 支付订单号: {}", payOrder.getOrderNo());
                log.warn("自动分账失败:{}", e.getMessage());
            }
        }
    }
}
