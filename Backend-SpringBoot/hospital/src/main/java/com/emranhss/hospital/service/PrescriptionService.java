package com.emranhss.hospital.service;


import com.emranhss.hospital.dto.*;
import com.emranhss.hospital.entity.*;
import com.emranhss.hospital.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrescriptionService {

    @Autowired
    private IPrescriptionRepo prescriptionRepo;

    @Autowired
    private IDoctorRepo doctorRepo;

    @Autowired
    private IAppoinmentRepo appointmentRepo;

    @Autowired
    private IMedicineRepo medicineRepo;

    @Autowired
    private ITestRepo testRepo;



    @Transactional
    public Prescription savePrescription(PrescriptionDTO dto) {
        Prescription prescription = new Prescription();
        prescription.setNote(dto.getNote());
        prescription.setAdvice(dto.getAdvice());
        prescription.setHeight(dto.getHeight());
        prescription.setWeight(dto.getWeight());
        prescription.setBp(dto.getBp());
        prescription.setDate(dto.getDate());
        prescription.setApplyWay(dto.getApplyWay());

        // Set doctor if present
        if (dto.getDoctor() != null && dto.getDoctor().getId() != null) {
            doctorRepo.findById(dto.getDoctor().getId())
                    .ifPresent(prescription::setDoctor);
        }

        // Set appointment if present
        if (dto.getAppointment() != null && dto.getAppointment().getId() != null) {
            appointmentRepo.findById(dto.getAppointment().getId())
                    .ifPresent(prescription::setAppointment);
        }

        // Set medicines
        if (dto.getMedicines() != null) {
            List<Medicine> medicines = dto.getMedicines().stream()
                    .map(mdto -> {
                        Medicine m = new Medicine();
                        m.setMedicineName(mdto.getMedicineName());
                        m.setDuration(mdto.getDuration());
                        m.setApplyWay(mdto.getApplyWay());
                        m.setPrescription(prescription);
                        return m;
                    })
                    .collect(Collectors.toList());
            prescription.setMedicines(medicines);
        }

        // Set tests
        if (dto.getTests() != null) {
            List<Tests> tests = dto.getTests().stream()
                    .map(t -> {
                        if (t.getId() != null) {
                            return testRepo.findById(t.getId()).orElse(null);
                        }
                        return null;
                    })
                    .filter(t -> t != null)
                    .collect(Collectors.toList());

            tests.forEach(t -> t.setPrescription(prescription));
            prescription.setTests(tests);
        }

        return prescriptionRepo.save(prescription);
    }





    public void deletePrescription(Long id) throws Exception {
        Prescription p = prescriptionRepo.findById(id)
                .orElseThrow(() -> new Exception("Prescription not found"));
        prescriptionRepo.delete(p);
    }



    // Private: map Entity → DTO
    public PrescriptionDTO mapToResponseDTO(Prescription prescription) {
        PrescriptionDTO dto = new PrescriptionDTO();
        dto.setId(prescription.getId());
        dto.setNote(prescription.getNote());
        dto.setAdvice(prescription.getAdvice());
        dto.setHeight(prescription.getHeight());
        dto.setWeight(prescription.getWeight());
        dto.setBp(prescription.getBp());
        dto.setDate(prescription.getDate());
        dto.setApplyWay(prescription.getApplyWay());

        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setId(prescription.getDoctor().getId());
        doctorDTO.setName(prescription.getDoctor().getName());
        dto.setDoctor(doctorDTO);

        AppoinmentDTO appointmentDTO = new AppoinmentDTO();
        appointmentDTO.setId(prescription.getAppointment().getId());
        appointmentDTO.setSlotDate(prescription.getAppointment().getScheduleSlot().getDate());
        dto.setAppointment(appointmentDTO);

        dto.setMedicines(
                prescription.getMedicines().stream().map(m -> {
                    MedicineDTO md = new MedicineDTO();
                    md.setId(m.getId());
                    md.setMedicineName(m.getMedicineName());
                    md.setDuration(m.getDuration());
                    md.setApplyWay(m.getApplyWay());
                    return md;
                }).toList()
        );


        dto.setTests(
                prescription.getTests().stream().map(t -> {
                    TestDTO td = new TestDTO();
                    td.setId(t.getId());
                    td.setTestName(t.getTestName());
                    return td;
                }).toList()
        );

        return dto;
    }

}