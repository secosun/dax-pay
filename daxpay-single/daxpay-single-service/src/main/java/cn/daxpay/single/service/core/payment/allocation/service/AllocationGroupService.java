package cn.daxpay.single.service.core.payment.allocation.service;

import cn.bootx.platform.common.core.exception.BizException;
import cn.bootx.platform.common.core.exception.DataNotExistException;
import cn.bootx.platform.common.core.function.CollectorsFunction;
import cn.bootx.platform.common.core.rest.PageResult;
import cn.bootx.platform.common.core.rest.param.PageParam;
import cn.bootx.platform.common.mybatisplus.util.MpUtil;
import cn.daxpay.single.code.PayChannelEnum;
import cn.daxpay.single.service.core.payment.allocation.convert.AllocationGroupConvert;
import cn.daxpay.single.service.core.payment.allocation.dao.AllocationGroupManager;
import cn.daxpay.single.service.core.payment.allocation.dao.AllocationGroupReceiverManager;
import cn.daxpay.single.service.core.payment.allocation.dao.AllocationReceiverManager;
import cn.daxpay.single.service.core.payment.allocation.entity.AllocationGroup;
import cn.daxpay.single.service.core.payment.allocation.entity.AllocationGroupReceiver;
import cn.daxpay.single.service.core.payment.allocation.entity.AllocationReceiver;
import cn.daxpay.single.service.dto.allocation.AllocationGroupDto;
import cn.daxpay.single.service.dto.allocation.AllocationGroupReceiverResult;
import cn.daxpay.single.service.param.allocation.group.AllocationGroupBindParam;
import cn.daxpay.single.service.param.allocation.group.AllocationGroupParam;
import cn.daxpay.single.service.param.allocation.group.AllocationGroupReceiverParam;
import cn.daxpay.single.service.param.allocation.group.AllocationGroupUnbindParam;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分账组服务
 * @author xxm
 * @since 2024/4/1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AllocationGroupService {
    private final AllocationGroupManager groupManager;
    private final AllocationGroupReceiverManager groupReceiverManager;
    private final AllocationReceiverManager receiverManager;

    /**
     * 分页
     */
    public PageResult<AllocationGroupDto> page(PageParam pageParam, AllocationGroupParam query){
        return MpUtil.convert2DtoPageResult(groupManager.page(pageParam,query));
    }

    /**
     * 查询详情
     */
    public AllocationGroupDto findById(Long id){
        return groupManager.findById(id).map(AllocationGroup::toDto).orElseThrow(()->new DataNotExistException("分账组不存在"));
    }

    /**
     * 查询分账接收方
     */
    public List<AllocationGroupReceiverResult> findReceiversByGroups(Long groupId){
        List<AllocationGroupReceiver> groupReceivers = groupReceiverManager.findByGroupId(groupId);
        List<Long> receiverIds = groupReceivers.stream()
                .map(AllocationGroupReceiver::getReceiverId)
                .collect(Collectors.toList());
        // 查询关联接收方信息
        Map<Long, AllocationReceiver> receiverMap = receiverManager.findAllByIds(receiverIds)
                .stream()
                .collect(Collectors.toMap(AllocationReceiver::getId, Function.identity(), CollectorsFunction::retainLatest));
        // 组装信息
        return groupReceivers.stream()
                .map(o -> {
                    AllocationReceiver receiver = receiverMap.get(o.getReceiverId());
                    return new AllocationGroupReceiverResult()
                            .setId(o.getId())
                            .setReceiverId(receiver.getId())
                            .setReceiverNo(receiver.getReceiverNo())
                            .setReceiverAccount(receiver.getReceiverAccount())
                            .setReceiverName(receiver.getReceiverName())
                            .setRate(o.getRate())
                            .setReceiverType(receiver.getReceiverType())
                            .setRelationName(receiver.getRelationName())
                            .setRelationType(receiver.getRelationType());
                })
                .collect(Collectors.toList());
    }

    /**
     * 创建分账组
     */
    public void create(AllocationGroupParam param){
        PayChannelEnum.findByCode(param.getChannel());
        AllocationGroup group = AllocationGroupConvert.CONVERT.convert(param);
        group.setTotalRate(0);
        groupManager.save(group);
    }

    /**
     * 设置默认分账组
     */
    @Transactional(rollbackFor = Exception.class)
    public void setUpDefault(Long id){
        // 分账组
        AllocationGroup group = groupManager.findById(id)
                .orElseThrow(() -> new DataNotExistException("未找到分账组"));
        groupManager.clearDefault(group.getChannel());
        group.setDefaultGroup(true);
        groupManager.updateById(group);
    }

    /**
     * 清除默认分账组
     */
    @Transactional(rollbackFor = Exception.class)
    public void clearDefault(Long id){
        AllocationGroup group = groupManager.findById(id)
                .orElseThrow(() -> new DataNotExistException("未找到分账组"));
        group.setDefaultGroup(false);
        groupManager.updateById(group);
    }

    /**
     * 更新分账组
     */
    public void update(AllocationGroupParam param){
        AllocationGroup group = groupManager.findById(param.getId())
                .orElseThrow(() -> new DataNotExistException("未找到分账组"));
        BeanUtil.copyProperties(param,group, CopyOptions.create().ignoreNullValue());
        group.setTotalRate(null);
        groupManager.updateById(group);
    }

    /**
     * 删除分账组
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id){
        groupManager.deleteById(id);
        groupReceiverManager.deleteByGroupId(id);
    }

    /**
     * 绑定分账接收方
     */
    @Transactional(rollbackFor = Exception.class)
    public void bindReceivers(AllocationGroupBindParam param) {
        // 分账组
        AllocationGroup group = groupManager.findById(param.getGroupId())
                .orElseThrow(() -> new DataNotExistException("未找到分账组"));
        // 查询接收方
        List<AllocationGroupReceiverParam> receiverParams = param.getReceivers();
        List<Long> receiverIds =  receiverParams.stream()
                .map(AllocationGroupReceiverParam::getReceiverId)
                .collect(Collectors.toList());
        List<AllocationReceiver> receivers = receiverManager.findAllByIds(receiverIds);
        if (receivers.size() != receiverIds.size()){
            throw new DataNotExistException("传入的分账接收房数量与查询到的不一致");
        }
        // 接收方只能为一个支付通道
        long count = receivers.stream()
                .map(AllocationReceiver::getChannel)
                .distinct()
                .count();
        if (count > 1){
            throw new BizException("接收方只能为一个支付通道");
        }
        // 检查接收方和传入的通道是否是不一致
        if (!Objects.equals(group.getChannel(), receivers.get(0).getChannel())){
            throw new BizException("接收方和传入的通道不一致");
        }

        // 保存分账接收者
        List<AllocationGroupReceiver> groupReceivers = receivers.stream()
                .map(receiver -> new AllocationGroupReceiver().setGroupId(group.getId())
                        .setReceiverId(receiver.getId())
                        .setRate(receiverParams.get(receivers.indexOf(receiver))
                                .getRate()))
                .collect(Collectors.toList());
        groupReceiverManager.saveAll(groupReceivers);
        // 计算分账比例
        int sum = receiverParams.stream()
                .mapToInt(AllocationGroupReceiverParam::getRate)
                .sum();
        group.setTotalRate(group.getTotalRate() + sum);
        groupManager.updateById(group);
    }

    /**
     * 批量删除分账接收方
     */
    @Transactional(rollbackFor = Exception.class)
    public void unbindReceivers(AllocationGroupUnbindParam param){
        // 分账组
        AllocationGroup group = groupManager.findById(param.getGroupId())
                .orElseThrow(() -> new DataNotExistException("未找到分账组"));
        // 删除接收方
        List<AllocationGroupReceiver> receivers = groupReceiverManager.findAllByIds(param.getReceiverIds());
        if (receivers.size() != param.getReceiverIds().size()){
            throw new DataNotExistException("传入的分账接收房数量与查询到的不一致");
        }
        groupReceiverManager.deleteByIds(param.getReceiverIds());
        // 计算分账比例
        int sum = receivers.stream()
                .mapToInt(AllocationGroupReceiver::getRate)
                .sum();
        group.setTotalRate(group.getTotalRate() - sum);
        groupManager.updateById(group);
    }

    /**
     * 删除单个分账接收方
     */
    @Transactional
    public void unbindReceiver(Long receiverId){
        AllocationGroupReceiver groupReceiver = groupReceiverManager.findById(receiverId)
                .orElseThrow(() -> new DataNotExistException("未找到分账接收方"));
        AllocationGroup group = groupManager.findById(groupReceiver.getGroupId())
                .orElseThrow(() -> new DataNotExistException("未找到分账组"));
        // 更新分账比例
        group.setTotalRate(group.getTotalRate() - groupReceiver.getRate());
        // 更新接收比例
        groupReceiverManager.deleteById(receiverId);
        groupManager.updateById(group);
    }

    /**
     * 修改分账比例
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateRate(Long receiverId, Integer rate){
        AllocationGroupReceiver groupReceiver = groupReceiverManager.findById(receiverId)
                .orElseThrow(() -> new DataNotExistException("未找到分账接收方"));
        AllocationGroup group = groupManager.findById(groupReceiver.getGroupId())
                .orElseThrow(() -> new DataNotExistException("未找到分账组"));
        // 更新分账比例
        group.setTotalRate(group.getTotalRate() - groupReceiver.getRate() + rate);
        // 更新接收比例
        groupReceiver.setRate(rate);
        groupReceiverManager.updateById(groupReceiver);
        groupManager.updateById(group);
    }

    /**
     * 判断分账组编号是否存在
     */
    public boolean existsByGroupNo(String groupNo) {
        return groupManager.existedByGroupNo(groupNo);
    }
}
