package com.Booking_care.domain.dto.BillDetailDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReqBillDetailDTO {
    private Long serviceId;
    private Integer quantity;
}
