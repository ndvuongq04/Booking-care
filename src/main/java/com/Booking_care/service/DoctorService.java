package com.Booking_care.service;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.Booking_care.domain.Account;
import com.Booking_care.domain.Clinic;
import com.Booking_care.domain.Doctor;
import com.Booking_care.domain.Specialty;
import com.Booking_care.domain.dto.DoctorDTO.DoctorCriteriaDTO;
import com.Booking_care.domain.dto.DoctorDTO.ResDoctorDTO;
import com.Booking_care.domain.dto.DoctorDTO.UpdateDoctorDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.DoctorRepository;
import com.Booking_care.service.specification.DoctorSpecs;
import com.Booking_care.util.error.IdInvalidException;
import com.Booking_care.mapper.doctor.DoctorMapper;
import com.Booking_care.domain.enums.RoleName;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AccountService accountService;
    private final ClinicService clinicService;
    private final SpecialtyService specialtyService;
    private final AccountProfile accountProfile;

    public DoctorService(DoctorRepository doctorRepository,
            AccountService accountService,
            ClinicService clinicService,
            SpecialtyService specialtyService,
            AccountProfile accountProfile) {
        this.doctorRepository = doctorRepository;
        this.clinicService = clinicService;
        this.accountService = accountService;
        this.specialtyService = specialtyService;
        this.accountProfile = accountProfile;
    }

    public boolean isAccountExits(long id) {
        return this.doctorRepository.existsByAccountId(id);
    }

    public Account fetchAccountById(long id) {
        return this.accountService.fetchAccountById(id);
    }

    public Doctor handleCreateDoctor(Doctor doctor) {
        // Validation 1: Check account exists
        Account account = this.accountService.fetchAccountById(doctor.getAccount().getId());

        // Validation 2: Check account has DOCTOR role - QUAN TRỌNG!
        boolean hasRole = this.accountProfile.accountHasRole(account.getId(), RoleName.DOCTOR);
        if (!hasRole) {
            throw new IdInvalidException(
                    "Account id: " + account.getId() + " không có quyền " + RoleName.DOCTOR);
        }

        // Validation 3: Check account not already used for other profiles
        this.accountProfile.accountUsed(account.getId());

        // Validation 4: Check account not already used by another doctor
        if (this.doctorRepository.existsByAccountId(account.getId())) {
            throw new IdInvalidException(
                    "Account với id " + account.getId() + " đã được sử dụng cho một bác sĩ khác");
        }

        // Validation 5: Check clinic exists (will throw if not found)
        Clinic clinic = this.clinicService.fetchClinicById(doctor.getClinic().getId());

        // Validation 6: Check specialty exists (will throw if not found)
        Specialty specialty = this.specialtyService.fetchSpecialtyById(doctor.getSpecialty().getId());

        try {
            doctor.setAccount(account);
            doctor.setClinic(clinic);
            doctor.setSpecialty(specialty);
            return this.doctorRepository.save(doctor);
        } catch (Exception e) {
            throw new IdInvalidException("Không thể tạo doctor: " + e.getMessage());
        }
    }

    public Doctor fetchDoctorById(long id) {
        return this.doctorRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Doctor với id " + id + " không tồn tại"));
    }

    public Doctor handleUpdateDoctor(UpdateDoctorDTO doctor) {
        // Validation: check doctor exists (will throw if not found)
        Doctor currentDoctor = this.fetchDoctorById(doctor.getId());

        // Validation: check clinic exists (will throw if not found)
        Clinic clinic = this.clinicService.fetchClinicById(doctor.getClinic().getId());

        // Validation: check specialty exists (will throw if not found)
        Specialty specialty = this.specialtyService.fetchSpecialtyById(doctor.getSpecialty().getId());

        try {
            currentDoctor.setCost(doctor.getCost());
            currentDoctor.setDegree(doctor.getDegree());
            currentDoctor.setDescription(doctor.getDescription());
            currentDoctor.setIsActive(doctor.getIsActive());

            if (doctor.getClinic() != null) {
                currentDoctor.setClinic(clinic);
            }

            if (doctor.getSpecialty() != null) {
                currentDoctor.setSpecialty(specialty);
            }

            return this.doctorRepository.save(currentDoctor);
        } catch (Exception e) {
            throw new IdInvalidException("Không thể cập nhật doctor: " + e.getMessage());
        }
    }

    public void handleDeleteDoctor(long id) {
        // Validation: check doctor exists (will throw if not found)
        Doctor d = this.fetchDoctorById(id);

        try {
            d.setIsActive(false);
            this.doctorRepository.save(d);
        } catch (Exception e) {
            throw new IdInvalidException("Không thể xóa doctor: " + e.getMessage());
        }
    }

    public ResultPaginationDTO fetchAllDoctor(Pageable pageable) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Doctor> page = this.doctorRepository.findAll(pageable);

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        // convert
        List<ResDoctorDTO> listDoc = page.getContent().stream()
                .map(DoctorMapper::toResDoctorDTO)
                .collect(Collectors.toList());

        res.setResult(listDoc);
        res.setMeta(meta);

        return res;
    }

    public boolean existsByAccountAndIdNot(Account a, long id) {
        Account account = this.fetchAccountById(a.getId());
        return this.doctorRepository.existsByAccountAndIdNot(account, id);
    }

    public Page<Doctor> getAllWithSpec(DoctorCriteriaDTO doctorCriteriaDTO, Pageable pageable) {
        Specification<Doctor> combinedSpec = Specification.where(null);

        if (doctorCriteriaDTO.getDegree() != null && !doctorCriteriaDTO.getDegree().trim().isEmpty()) {
            Specification<Doctor> currentSpec = DoctorSpecs.degreeEqual(doctorCriteriaDTO.getDegree());
            combinedSpec = combinedSpec.and(currentSpec);
        }

        if (doctorCriteriaDTO.getMonthYear() != null) {
            YearMonth monthYear = doctorCriteriaDTO.getMonthYear();

            Instant from = monthYear.atDay(1)
                    .atStartOfDay(ZoneOffset.UTC) // mốc 00:00 ngày đầu tháng
                    .toInstant();

            Instant to = monthYear.plusMonths(1).atDay(1)
                    .atStartOfDay(ZoneOffset.UTC) // mốc 00:00 ngày đầu tháng kế tiếp
                    .toInstant();

            Specification<Doctor> currentSpec = DoctorSpecs.dateBetween(from, to);
            combinedSpec = combinedSpec.and(currentSpec);
        }

        if (doctorCriteriaDTO.getClinicId() != null) {
            Specification<Doctor> currentSpec = DoctorSpecs.clinicJointEqual(doctorCriteriaDTO.getClinicId());
            combinedSpec = combinedSpec.and(currentSpec);
        }

        if (doctorCriteriaDTO.getSpecialtyId() != null) {
            Specification<Doctor> currentSpec = DoctorSpecs.specialtyJointEqual(doctorCriteriaDTO.getSpecialtyId());
            combinedSpec = combinedSpec.and(currentSpec);
        }

        if (doctorCriteriaDTO.getName() != null && !doctorCriteriaDTO.getName().trim().isEmpty()) {
            Specification<Doctor> currentSpec = DoctorSpecs.nameJoinLikeIgnoreCase(doctorCriteriaDTO.getName());
            combinedSpec = combinedSpec.and(currentSpec);
        }

        if (doctorCriteriaDTO.getCost() != null) {
            Specification<Doctor> currentSpec = DoctorSpecs.costBetween(doctorCriteriaDTO.getCost().getMin(),
                    doctorCriteriaDTO.getCost().getMax());
            combinedSpec = combinedSpec.and(currentSpec);
        }

        if (doctorCriteriaDTO.getPhoneNumber() != null && !doctorCriteriaDTO.getPhoneNumber().trim().isEmpty()) {
            Specification<Doctor> currentSpec = DoctorSpecs.phoneNumberJointLike(doctorCriteriaDTO.getPhoneNumber());
            combinedSpec = combinedSpec.and(currentSpec);
        }

        return this.doctorRepository.findAll(combinedSpec, pageable);

    }

    public ResultPaginationDTO getDoctorSearch(DoctorCriteriaDTO doctorCriteriaDTO, Pageable pageable) {
        Page<Doctor> listPage = this.getAllWithSpec(doctorCriteriaDTO, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(listPage.getTotalPages());
        meta.setTotals(listPage.getTotalElements());

        // convert
        List<ResDoctorDTO> listDoc = listPage.getContent().stream()
                .map(DoctorMapper::toResDoctorDTO)
                .collect(Collectors.toList());

        res.setResult(listDoc);
        res.setMeta(meta);

        return res;
    }

}
