package com.joel.todo.util;

import com.joel.todo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CleanupSchedulerUtil {

    private final UserService userService;

    @Autowired
    public CleanupSchedulerUtil(UserService userService) {
        this.userService = userService;
    }

    @Scheduled(cron = "0 0 0 * * *") // Cron expression for 12:00 AM every day
    public void deleteExpiredUsersJob() {
        userService.deleteExpiredUserEntities();
    }

}