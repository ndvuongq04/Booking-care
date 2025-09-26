package com.Booking_care.config;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Booking_care.domain.Account;
import com.Booking_care.domain.Address;
import com.Booking_care.domain.Role;
import com.Booking_care.domain.Time;
import com.Booking_care.domain.enums.GenderEnum;
import com.Booking_care.repository.AccountRepository;
import com.Booking_care.repository.AddressRepository;
import com.Booking_care.repository.RoleRepository;
import com.Booking_care.repository.TimeRepository;

@Service
public class DatabaseInitialize implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final TimeRepository timeRepository;
    private final AddressRepository addressRepository;

    public DatabaseInitialize(RoleRepository roleRepository,
            AccountRepository accountRepository,
            PasswordEncoder passwordEncoder,
            TimeRepository timeRepository,
            AddressRepository addressRepository) {
        this.roleRepository = roleRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.timeRepository = timeRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> START INIT DATABASE");
        long countRole = this.roleRepository.count();
        long countAcc = this.accountRepository.count();

        if (countRole == 0) {
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setDescription("role admin được khởi tạo khi dự án chạy");
            this.roleRepository.save(adminRole);

            Role doctorRole = new Role();
            doctorRole.setName("DOCTOR");
            doctorRole.setDescription("role doctor được khởi tạo khi dự án chạy");
            this.roleRepository.save(doctorRole);

            Role supportRole = new Role();
            supportRole.setName("SUPPORT");
            supportRole.setDescription("role support được khởi tạo khi dự án chạy");
            this.roleRepository.save(supportRole);

            Role clientRole = new Role();
            clientRole.setName("CLIENT");
            clientRole.setDescription("role client được khởi tạo khi dự án chạy");
            this.roleRepository.save(clientRole);
        }

        if (countAcc == 0) {
            Account adminAcc = new Account();
            adminAcc.setEmail("superAdmin01@gmail.com");
            adminAcc.setPassword(this.passwordEncoder.encode("123456"));
            adminAcc.setName("I'm super admin");
            adminAcc.setGender(GenderEnum.MALE);

            Optional<Role> adminRole = this.roleRepository.findByName("ADMIN");
            if (adminRole.isPresent()) {
                adminAcc.setRole(adminRole.get());
            }

            this.accountRepository.save(adminAcc);

        }

        // seed times
        long countTime = this.timeRepository.count();
        if (countTime == 0) {
            List<Time> slots = List.of(
                    new Time(0, "08:00", "08:30", null, null, null),
                    new Time(0, "08:30", "09:00", null, null, null),
                    new Time(0, "09:00", "09:30", null, null, null),
                    new Time(0, "09:30", "10:00", null, null, null),
                    new Time(0, "10:00", "10:30", null, null, null),
                    new Time(0, "10:30", "11:00", null, null, null),
                    new Time(0, "13:30", "14:00", null, null, null),
                    new Time(0, "14:00", "14:30", null, null, null),
                    new Time(0, "14:30", "15:00", null, null, null),
                    new Time(0, "15:00", "15:30", null, null, null),
                    new Time(0, "15:30", "16:00", null, null, null),
                    new Time(0, "16:00", "16:30", null, null, null));
            this.timeRepository.saveAll(slots);
        }

        //
        long countAddress = this.addressRepository.count();
        if (countAddress == 0) {
            List<Address> cities = List.of(
                    new Address(0, "Hà Nội", true, null, null, null),
                    new Address(0, "TP. Hồ Chí Minh", true, null, null, null),
                    new Address(0, "Đà Nẵng", true, null, null, null),
                    new Address(0, "Hải Phòng", true, null, null, null),
                    new Address(0, "Cần Thơ", true, null, null, null),
                    new Address(0, "Bắc Ninh", true, null, null, null),
                    new Address(0, "Quảng Ninh", true, null, null, null),
                    new Address(0, "Nghệ An", true, null, null, null),
                    new Address(0, "Thừa Thiên Huế", true, null, null, null),
                    new Address(0, "Khánh Hòa", true, null, null, null),
                    new Address(0, "Bình Dương", true, null, null, null),
                    new Address(0, "Đồng Nai", true, null, null, null));
            this.addressRepository.saveAll(cities);
        }

        System.out.println(">>> END INIT DATABASE");
    }

}
