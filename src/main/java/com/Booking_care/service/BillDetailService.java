package com.Booking_care.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.Booking_care.domain.Bill;
import com.Booking_care.domain.BillDetail;
import com.Booking_care.domain.Services;
import com.Booking_care.domain.dto.BillDTO.ReqBillDTO;
import com.Booking_care.domain.dto.BillDetailDTO.ReqBillDetailDTO;
import com.Booking_care.repository.BillDetailRepository;
import com.Booking_care.util.error.IdInvalidException;

@Service
public class BillDetailService {
    private final BillDetailRepository billDetailRepository;
    private final ServicesService servicesService;

    public BillDetailService(BillDetailRepository billDetailRepository,
            ServicesService servicesService) {
        this.billDetailRepository = billDetailRepository;
        this.servicesService = servicesService;
    }

    @Transactional
    public List<BillDetail> handleCreateBillDetail(ReqBillDTO reqBillDTO, Bill bill) throws IdInvalidException {
        List<BillDetail> details = new ArrayList<>();

        if (reqBillDTO.getServices() == null || reqBillDTO.getServices().isEmpty()) {
            throw new IdInvalidException("Hóa đơn phải có ít nhất một dịch vụ.");
        }

        for (ReqBillDetailDTO serviceItem : reqBillDTO.getServices()) {
            // Lấy service từ DB
            Services service = this.servicesService.fetchServicesById(serviceItem.getServiceId());
            if (service == null) {
                throw new IdInvalidException("Service với id " + serviceItem.getServiceId() + " không tồn tại");
            }

            // Kiểm tra quantity
            if (serviceItem.getQuantity() == null || serviceItem.getQuantity() <= 0) {
                throw new IdInvalidException("Quantity không hợp lệ cho serviceId: " + serviceItem.getServiceId());
            }

            // Tạo BillDetail
            BillDetail billDetail = new BillDetail();
            billDetail.setBill(bill);
            billDetail.setService(service);
            billDetail.setQuantity(serviceItem.getQuantity());
            billDetail.setServiceCost(service.getCost());

            // Tính totalService = serviceCost * quantity
            BigDecimal totalService = service.getCost()
                    .multiply(BigDecimal.valueOf(serviceItem.getQuantity()));
            billDetail.setTotalService(totalService);

            details.add(billDetail);
        }

        return this.billDetailRepository.saveAll(details);
    }

    public List<BillDetail> fetchBillDetailByBillId(long billId) {
        return this.billDetailRepository.findAllByBillId(billId);
    }

}
