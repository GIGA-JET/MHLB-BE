package com.gigajet.mhlb.domain.workspace.service;

import com.gigajet.mhlb.global.common.dto.SendMessageDto;
import com.gigajet.mhlb.global.common.util.S3Handler;
import com.gigajet.mhlb.global.common.util.SuccessCode;
import com.gigajet.mhlb.domain.alarm.entity.Alarm;
import com.gigajet.mhlb.domain.alarm.repository.AlarmRepository;
import com.gigajet.mhlb.domain.status.entity.Status;
import com.gigajet.mhlb.domain.status.repository.StatusRepository;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.workspace.dto.WorkspaceRequestDto;
import com.gigajet.mhlb.domain.workspace.dto.WorkspaceResponseDto;
import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import com.gigajet.mhlb.domain.workspace.repository.WorkspaceRepository;
import com.gigajet.mhlb.domain.workspace.entity.WorkspaceOrder;
import com.gigajet.mhlb.domain.workspace.entity.WorkspaceUser;
import com.gigajet.mhlb.domain.workspace.repository.WorkspaceOrderRepository;
import com.gigajet.mhlb.domain.workspace.repository.WorkspaceUserRepository;
import com.gigajet.mhlb.global.exception.CustomException;
import com.gigajet.mhlb.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.gigajet.mhlb.domain.workspace.entity.WorkspaceUserRole.ADMIN;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final WorkspaceOrderRepository workspaceOrderRepository;
    private final StatusRepository statusRepository;
    private final AlarmRepository alarmRepository;

    private final S3Handler s3Handler;

    @Value("${workspace.default.image}")
    private String defaultImage;

    @Transactional//(readOnly = true)
    public List<WorkspaceResponseDto.AllList> findWorkspaceList(User user) {
        List<WorkspaceResponseDto.AllList> orderLists = new ArrayList<>();
        List<WorkspaceOrder> workspaceOrderList = workspaceOrderRepository.findByWorkspaceUser_UserAndIsShowOrderByOrders(user, true);

        for (WorkspaceOrder workspaceOrder : workspaceOrderList) {
            List<Alarm> alarmList = alarmRepository.findAllByUserAndWorkspaceIdAndUnreadMessage(user, workspaceOrder.getWorkspaceUser().getWorkspace().getId(), true);
            if (alarmList.size() == 0) {
                orderLists.add(new WorkspaceResponseDto.AllList(workspaceOrder.getWorkspaceUser().getWorkspace(), false));
            } else {
                orderLists.add(new WorkspaceResponseDto.AllList(workspaceOrder.getWorkspaceUser().getWorkspace(), true));
            }
        }

        return orderLists;
    }

    @Transactional
    public WorkspaceResponseDto.Response createWorkspace(User user, MultipartFile image, WorkspaceRequestDto.Create workspaceDto) throws IOException {
        String imageUrl;
        if (image == null) {
            imageUrl = defaultImage;
        } else {
            imageUrl = s3Handler.upload(image);
        }

        Workspace workspace = new Workspace(workspaceDto, imageUrl);

        Long count = workspaceUserRepository.countByUserAndIsShow(user, true);

        WorkspaceUser workspaceUser = new WorkspaceUser(user, workspace, ADMIN);
        WorkspaceOrder workspaceOrder = new WorkspaceOrder(count, workspaceUser);

        workspaceRepository.save(workspace);
        workspaceUserRepository.save(workspaceUser);
        workspaceOrderRepository.save(workspaceOrder);

        return new WorkspaceResponseDto.Response(workspace);
    }

    @Transactional(readOnly = true)
    public WorkspaceResponseDto.InfoAndRoll findWorkspaceInfoAndRoll(User user, Long workspaceId) {
        Workspace workspace = validateWorkspace(workspaceId);
        WorkspaceUser workspaceUser = workspaceUserRepository.findByUserAndWorkspaceAndIsShowTrue(user, workspace).orElseThrow(() -> new CustomException(ErrorCode.ACCESS_DENIED));
        return new WorkspaceResponseDto.InfoAndRoll(workspaceUser.getWorkspace(), workspaceUser.getRole());
    }

    @Transactional
    public ResponseEntity<SendMessageDto> changeOrder(User user, WorkspaceRequestDto.Orders orders) {
        List<WorkspaceOrder> orderList = workspaceOrderRepository.findByWorkspaceUser_UserAndIsShowOrderByOrders(user, true);

        HashMap<Long, Long> orderMap = new HashMap<>();//워크스페이스 id , 순서
        for (WorkspaceRequestDto.Order order : orders.getOrders()) {
            orderMap.put(order.getWorkspaceId(), order.getOrderNum());
        }

        for (WorkspaceOrder workspaceOrder : orderList) {
            workspaceOrderRepository.orderUpdate(orderMap.get(workspaceOrder.getWorkspaceUser().getWorkspace().getId()), workspaceOrder.getWorkspaceUser().getId());
        }

        return SendMessageDto.toResponseEntity(SuccessCode.ORDER_CHANGE_SUCCESS);
    }

    @Transactional//(readOnly = true)
    public List<WorkspaceResponseDto.OrderList> findOrder(User user) {
        List<WorkspaceResponseDto.OrderList> orderLists = new ArrayList<>();
        List<WorkspaceOrder> workspaceOrderList = workspaceOrderRepository.findByWorkspaceUser_UserAndIsShowOrderByOrders(user, true);

        for (WorkspaceOrder workspaceOrder : workspaceOrderList) {
            orderLists.add(new WorkspaceResponseDto.OrderList(workspaceOrder.getWorkspaceUser().getWorkspace(), workspaceOrder.getOrders()));
        }

        return orderLists;
    }

    @Transactional(readOnly = true)
    public List<WorkspaceResponseDto.People> findPeople(User user, Long workspaceId) {
        Workspace workspace = validateWorkspace(workspaceId);
        workspaceUserRepository.findByUserAndWorkspaceAndIsShowTrue(user, workspace).orElseThrow(() -> new CustomException(ErrorCode.ACCESS_DENIED));

        List<WorkspaceResponseDto.People> peopleList = new ArrayList<>();
        List<WorkspaceUser> workspaceUserList = workspaceUserRepository.findByWorkspaceAndIsShow(workspace, true);

        for (WorkspaceUser workspaceUser : workspaceUserList) {
            if (Objects.equals(workspaceUser.getUser().getId(), user.getId())) {
                continue;
            }
            Status status = statusRepository.findTopByUserOrderByUpdateDayDescUpdateTimeDesc(workspaceUser.getUser());
            peopleList.add(new WorkspaceResponseDto.People(status));
        }

        peopleList.sort((o1, o2) -> {
            if (Objects.equals(o1.getColor(), o2.getColor())) {
                return o1.getUserName().toLowerCase().compareTo(o2.getUserName().toLowerCase());
            }
            return Integer.compare(o1.getColor(), o2.getColor());
        });

        peopleList.add(0, new WorkspaceResponseDto.People(statusRepository.findTopByUserOrderByUpdateDayDescUpdateTimeDesc(user)));//본인이 가장 먼저 나오게 해야함

        return peopleList;
    }

    private Workspace validateWorkspace(Long workspaceId) {
        return workspaceRepository.findByIdAndIsShowTrue(workspaceId).orElseThrow(() -> new CustomException(ErrorCode.WRONG_WORKSPACE_ID));
    }
}
