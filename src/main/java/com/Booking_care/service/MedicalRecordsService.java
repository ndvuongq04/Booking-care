package com.Booking_care.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.Booking_care.domain.Account;
import com.Booking_care.domain.Clinic;
import com.Booking_care.domain.Doctor;
import com.Booking_care.domain.MedicalRecord;
import com.Booking_care.domain.Patient;
import com.Booking_care.domain.Specialty;
import com.Booking_care.domain.dto.AccountDTO.AccountCriteriaDTO;
import com.Booking_care.domain.dto.AccountDTO.ResAccountDTO;
import com.Booking_care.domain.dto.MedicalRecordDTO.ReqMedicalRecordDTO;
import com.Booking_care.domain.dto.MedicalRecordDTO.ResMedicalRecordDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.MedicalRecordsRepository;
import com.Booking_care.util.error.IdInvalidException;

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
                .map(item -> this.convertToMedicalRecordDTO(item))
                .collect(Collectors.toList());

        res.setResult(listAcc);
        res.setMeta(meta);

        return res;
    }

    public static ResMedicalRecordDTO convertToMedicalRecordDTO(MedicalRecord record) {
        if (record == null)
            return null;

        ResMedicalRecordDTO dto = new ResMedicalRecordDTO();
        dto.setId(record.getId());
        dto.setDescription(record.getDescription());
        dto.setCreateAt(record.getCreateAt());
        dto.setUpdateAt(record.getUpdateAt());

        // Patient
        if (record.getPatient() != null) {
            ResMedicalRecordDTO.PatientDTO pDto = new ResMedicalRecordDTO.PatientDTO();
            pDto.setId(record.getPatient().getId());
            pDto.setName(record.getPatient().getAccount().getName()); // giả sử Patient có Account chứa name
            dto.setPatient(pDto);
        }

        // Doctor
        if (record.getDoctor() != null) {
            ResMedicalRecordDTO.DoctorDTO dDto = new ResMedicalRecordDTO.DoctorDTO();
            dDto.setId(record.getDoctor().getId());
            dDto.setName(record.getDoctor().getAccount().getName()); // giả sử Doctor có Account chứa name
            dDto.setDegree(record.getDoctor().getDegree().name());
            dto.setDoctor(dDto);
        }

        // Clinic
        if (record.getClinic() != null) {
            ResMedicalRecordDTO.ClinicDTO cDto = new ResMedicalRecordDTO.ClinicDTO();
            cDto.setId(record.getClinic().getId());
            cDto.setName(record.getClinic().getName());
            dto.setClinic(cDto);
        }

        // Specialty
        if (record.getSpecialty() != null) {
            ResMedicalRecordDTO.SpecialtyDTO sDto = new ResMedicalRecordDTO.SpecialtyDTO();
            sDto.setId(record.getSpecialty().getId());
            sDto.setName(record.getSpecialty().getName());
            dto.setSpecialty(sDto);
        }

        return dto;
    }

    public MedicalRecord handleCreateMedicalRecord(ReqMedicalRecordDTO record) throws IdInvalidException {
        MedicalRecord mRecord = new MedicalRecord();
        mRecord.setDescription(record.getDescription());

        // fetch patient
        Patient patient = this.patientService.fetchPatientById(record.getPatientId());
        mRecord.setPatient(patient);

        // fetch doctor
        Doctor doctor = this.doctorService.fetchDoctorById(record.getDoctorId());
        mRecord.setDoctor(doctor);

        // fetch clinic
        Clinic clinic = this.clinicService.fetchClinicById(record.getClinicId());
        mRecord.setClinic(clinic);

        // fetch specialty
        Specialty specialty = this.specialtyService.fetchSpecialtyById(record.getSpecialtyId());
        mRecord.setSpecialty(specialty);

        return this.medicalRecordsRepository.save(mRecord);
    }

    public MedicalRecord fetchMedicalRecordById(Long id) throws IdInvalidException {
        return this.medicalRecordsRepository.findById(id).orElse(null);
    }

    public MedicalRecord handleUpdateMedicalRecord(Long id, ReqMedicalRecordDTO record) throws IdInvalidException {
        MedicalRecord existing = fetchMedicalRecordById(id);

        existing.setDescription(record.getDescription());

        // fetch patient
        Patient patient = this.patientService.fetchPatientById(record.getPatientId());
        existing.setPatient(patient);

        // fetch doctor
        Doctor doctor = this.doctorService.fetchDoctorById(record.getDoctorId());
        existing.setDoctor(doctor);

        // fetch clinic
        Clinic clinic = this.clinicService.fetchClinicById(record.getClinicId());
        existing.setClinic(clinic);

        // fetch specialty
        Specialty specialty = this.specialtyService.fetchSpecialtyById(record.getSpecialtyId());
        existing.setSpecialty(specialty);

        return medicalRecordsRepository.save(existing);
    }

}
