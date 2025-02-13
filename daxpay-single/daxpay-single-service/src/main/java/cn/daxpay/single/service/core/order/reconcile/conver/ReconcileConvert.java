package cn.daxpay.single.service.core.order.reconcile.conver;

import cn.daxpay.single.service.core.order.reconcile.entity.ReconcileTradeDetail;
import cn.daxpay.single.service.core.order.reconcile.entity.ReconcileDiff;
import cn.daxpay.single.service.core.order.reconcile.entity.ReconcileOrder;
import cn.daxpay.single.service.dto.order.reconcile.ReconcileTradeDetailDto;
import cn.daxpay.single.service.dto.order.reconcile.ReconcileDiffDto;
import cn.daxpay.single.service.dto.order.reconcile.ReconcileOrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 *
 * @author xxm
 * @since 2024/1/22
 */
@Mapper
public interface ReconcileConvert {
    ReconcileConvert CONVERT = Mappers.getMapper(ReconcileConvert.class);

    ReconcileTradeDetailDto convert(ReconcileTradeDetail in);

    ReconcileDiffDto convert(ReconcileDiff in);

    ReconcileOrderDto convert(ReconcileOrder in);
}
