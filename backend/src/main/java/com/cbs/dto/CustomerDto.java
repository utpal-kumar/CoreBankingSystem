package com.cbs.dto;

import com.cbs.model.Customer;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String type;
    private String riskProfile;
    private String address;
    private String kycStatus;

    public static CustomerDto from(Customer c) {
        return CustomerDto.builder()
                .id(c.getId())
                .name(c.getName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .type(c.getType() != null ? c.getType().name() : null)
                .riskProfile(c.getRiskProfile() != null ? c.getRiskProfile().name() : null)
                .address(c.getAddress())
                .kycStatus(c.getKycStatus() != null ? c.getKycStatus().name() : null)
                .build();
    }
}
