package com.joel.todo.config;

import com.joel.todo.model.Role;
import com.joel.todo.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    // Flag to track whether data setup has already been performed.
    private boolean alreadySetup = false;

    // RoleRepository instance to interact with the role database table.
    private final RoleRepository roleRepository;

    // Constructor to inject RoleRepository instance.
    @Autowired
    public SetupDataLoader(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // Method triggered when the application context is refreshed.
    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // Check if data setup has already been performed.
        if (alreadySetup) {
            return;
        }

        // Check if the "USER" role already exists in the database.
        if (this.roleRepository.findByAuthority("USER").isPresent()) {
            return;
        }

        // If not, create a new "USER" role and save it to the database.
        Role userRole = new Role();
        userRole.setAuthority("USER");
        this.roleRepository.save(userRole);

        // Mark data setup as completed.
        alreadySetup = true;
    }
}