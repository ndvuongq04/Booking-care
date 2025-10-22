package com.Booking_care.domain.dto.MedicalRecordDTO;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResMedicalRecordDTO {
    private Long id;
    private String description;
    private Instant createAt;
    private Instant updateAt;

    private PatientDTO patient;
    private DoctorDTO doctor;
    private ClinicDTO clinic;
    private SpecialtyDTO specialty;

    // private List<ServiceMedicalRecordDTO> serviceMedicalRecords;
    // private List<BillDTO> bills;

    // Inner DTOs chỉ chứa thông tin cơ bản để FE hiển thị
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatientDTO {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoctorDTO {
        private Long id;
        private String name;
        private String degree;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClinicDTO {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpecialtyDTO {
        private Long id;
        private String name;
    }

    // @Getter
    // @Setter
    // public static class ServiceMedicalRecordDTO {
    // private Long id;
    // private String serviceName;
    // private Double cost;
    // }

    // @Getter
    // @Setter
    // public static class BillDTO {
    // private Long id;
    // private Double totalAmount;
    // private Instant createdAt;
    // }

}
