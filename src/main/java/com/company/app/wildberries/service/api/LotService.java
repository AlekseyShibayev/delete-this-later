package com.company.app.wildberries.service.api;

import com.company.app.wildberries.dto.LotDto;
import com.company.app.wildberries.entity.Lot;

import java.util.List;

public interface LotService {

	Lot get(Long id);

	List<Lot> getAll();

	Long create(String name, String price, String discount);

	Long create(LotDto lotDto);

	Boolean delete(Long id);

	Boolean update(Long id, LotDto lotDto);
}
