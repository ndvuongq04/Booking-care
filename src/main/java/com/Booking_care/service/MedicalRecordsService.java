package com.Booking_care.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.Booking_care.domain.Clinic;
import com.Booking_care.domain.Doctor;
import com.Booking_care.domain.MedicalRecord;
import com.Booking_care.domain.Patient;
import com.Booking_care.domain.Specialty;
import com.Booking_care.domain.dto.MedicalRecordDTO.MedicalRecordCriteriaDTO;
import com.Booking_care.domain.dto.MedicalRecordDTO.ReqMedicalRecordDTO;
import com.Booking_care.domain.dto.MedicalRecordDTO.ResMedicalRecordDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.MedicalRecordsRepository;
import com.Booking_care.service.specification.MedicalRecordSpecs;
import com.Booking_care.util.error.IdInvalidException;
import com.Booking_care.mapper.medicalrecord.MedicalRecordMapper;

@Service
public class MedicalRecordsService {
    private final MedicalRecordsRepository medicalRecordsRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final ClinicService clinicService;
    private final SpecialtyService specialtyService;

    public MedicalRecordsService(MedicalRecordsRepository medicalRecordsRepository,
            PatientService patientService,
            DoctorService doctorService,
            ClinicService clinicService,
            SpecialtyService specialtyService) {
        this.medicalRecordsRepository = medicalRecordsRepository;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.clinicService = clinicService;
        this.specialtyService = specialtyService;

    }

    public ResultPaginationDTO fetchAllMedicalRecords(Pageable pageable) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<MedicalRecord> page = this.medicalRecordsRepository.findAll(pageable);

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        // convert
        List<ResMedicalRecordDTO> listAcc = page.getContent().stream()
                .map(MedicalRecordMapper::toResMedicalRecordDTO)
                .collect(Collectors.toList());

        res.setResult(listAcc);
        res.setMeta(meta);

        return res;
    }


    public MedicalRecord handleCreateMedicalRecord(ReqMedicalRecordDTO record) throws IdInvalidException {
        // Validation: các fetch methods sẽ throw exception nếu không tìm thấy
        Patient patient = this.patientService.fetchPatientById(record.getPatientId());
        Doctor doctor = this.doctorService.fetchDoctorById(record.getDoctorId());
        Clinic clinic = this.clinicService.fetchClinicById(record.getClinicId());
        Specialty specialty = this.specialtyService.fetchSpecialtyById(record.getSpecialtyId());

        try {
            MedicalRecord mRecord = new MedicalRecord();
            mRecord.setDescription(record.getDescription());
            mRecord.setPatient(patient);
            mRecord.setDoctor(doctor);
            mRecord.setClinic(clinic);
            mRecord.setSpecialty(specialty);

            return this.medicalRecordsRepository.save(mRecord);
        } catch (Exception e) {
            throw new IdInvalidException("Không thể tạo medical record: " + e.getMessage());
        }
    }

    public MedicalRecord fetchMedicalRecordById(Long id) throws IdInvalidException {
        return this.medicalRecordsRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("MedicalRecord với id " + id + " không tồn tại"));
    }

    public MedicalRecord handleUpdateMedicalRecord(Long id, ReqMedicalRecordDTO record) throws IdInvalidException {
        // Validation: check medical record exists (will throw if not found)
        MedicalRecord existing = fetchMedicalRecordById(id);

        // Validation: các fetch methods sẽ throw exception nếu không tìm thấy
        Patient patient = this.patientService.fetchPatientById(record.getPatientId());
        Doctor doctor = this.doctorService.fetchDoctorById(record.getDoctorId());
        Clinic clinic = this.clinicService.fetchClinicById(record.getClinicId());
        Specialty specialty = this.specialtyService.fetchSpecialtyById(record.getSpecialtyId());

        try {
            existing.setDescription(record.getDescription());
            existing.setPatient(patient);
            existing.setDoctor(doctor);
            existing.setClinic(clinic);
            existing.setSpecialty(specialty);

            return medicalRecordsRepository.save(existing);
        } catch (Exception e) {
            throw new IdInvalidException("Không thể cập nhật medical record: " + e.getMessage());
        }
    }

    public ResultPaginationDTO fetchAllMedicalRecordsByDoctor(Pageable pageable, long doctorId) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<MedicalRecord> page = this.medicalRecordsRepository.findByDoctorId(pageable, doctorId);

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        // convert
        List<ResMedicalRecordDTO> listAcc = page.getContent().stream()
                .map(MedicalRecordMapper::toResMedicalRecordDTO)
                .collect(Collectors.toList());

        res.setResult(listAcc);
        res.setMeta(meta);

        return res;
    }

    public Page<MedicalRecord> getPatientOfDoctorWithSpecs(
            Pageable pageable, MedicalRecordCriteriaDTO dto) {

        Specification<MedicalRecord> spec = Specification
                .where(MedicalRecordSpecs.doctorIdJoinEqual(dto.getDoctorId()));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            spec = spec.and(MedicalRecordSpecs.nameJoinLikeIgnoreCase(dto.getName()));
        }
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isBlank()) {
            spec = spec.and(MedicalRecordSpecs.phoneNumberJoinLikeIgnoreCase(dto.getPhoneNumber()));
        }

        return this.medicalRecordsRepository.findAll(spec, pageable);
    }

    public ResultPaginationDTO fetchAllMedicalRecordsByDoctorSearch(Pageable pageable,
            MedicalRecordCriteriaDTO medicalRecordCriteriaDTO) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<MedicalRecord> page = this.getPatientOfDoctorWithSpecs(pageable, medicalRecordCriteriaDTO);

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        // convert
        List<ResMedicalRecordDTO> listMed = page.getContent().stream()
                .map(MedicalRecordMapper::toResMedicalRecordDTO)
                .collect(Collectors.toList());

        res.setResult(listMed);
        res.setMeta(meta);

        return res;
    }

}
