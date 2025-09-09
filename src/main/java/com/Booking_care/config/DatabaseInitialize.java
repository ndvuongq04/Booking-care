package com.Booking_care.config;

import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Booking_care.domain.Account;
import com.Booking_care.domain.Role;
import com.Booking_care.domain.enums.GenderEnum;
import com.Booking_care.repository.AccountRepository;
import com.Booking_care.repository.RoleRepository;

@Service
public class DatabaseInitialize implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitialize(RoleRepository roleRepository,
            AccountRepository accountRepository,
            PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
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

        System.out.println(">>> END INIT DATABASE");
    }

}
