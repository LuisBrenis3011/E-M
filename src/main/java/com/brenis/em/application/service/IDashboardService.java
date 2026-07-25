package com.brenis.em.application.service;

import com.brenis.em.application.dto.response.DashboardResponse;

public interface IDashboardService {

    DashboardResponse getResumen(Long proveedorId);
}
