package com.example.demo.unit.controllers;

import com.example.demo.AdminDashboardController;
import com.example.demo.persistent.model.AppStatistic;
import com.example.demo.persistent.repository.AppStatisticRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminDashboardControllerUnitTest {

    @Mock
    private AppStatisticRepository statsDao;

    @Mock
    private Model model;

    @InjectMocks
    private AdminDashboardController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAdminDashboard() {
        // Arrange: create a list of AppStatistic objects as test data.
        AppStatistic stat1 = new AppStatistic();
        stat1.setStatName("Users");
        stat1.setStatValue(10L);

        AppStatistic stat2 = new AppStatistic();
        stat2.setStatName("Posts");
        stat2.setStatValue(20L);

        List<AppStatistic> stats = Arrays.asList(stat1, stat2);

        // When statsDao.findAllStats() is called, return our test data.
        when(statsDao.findAll()).thenReturn(stats);

        // Act: call the adminDashboard method.
        String viewName = controller.adminDashboard(model);

        // Assert: verify the correct view is returned.
        assertEquals("adminDashboard", viewName);

        // Assert: verify that correct model attributes are added.
        verify(model).addAttribute(eq("statsLabels"), eq(Arrays.asList("Users", "Posts")));
        verify(model).addAttribute(eq("statsValues"), eq(Arrays.asList(10L, 20L)));
    }
}
