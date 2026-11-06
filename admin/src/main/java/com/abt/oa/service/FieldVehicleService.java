package com.abt.oa.service;

import com.abt.oa.model.*;
import org.springframework.data.domain.Page;
import java.util.List;

/** 野外用车业务接口。 */
public interface FieldVehicleService {
    Page<FieldVehicleListVO> findPage(FieldVehicleRequestForm form);
    FieldVehicleDetailVO findById(String id);
    List<FieldVehicleCarOptionVO> findList(CarOptionRequestForm form);
    FieldVehicleDetailVO saveDraft(FieldVehicleSaveRequest form);
    void delete(FieldVehicleDeleteRequest form);
    FieldVehicleDetailVO save(FieldVehicleSaveRequest form);
    FieldVehicleDetailVO update(FieldVehicleUpdateRequest form);
    FieldVehicleDetailVO updateReturn(FieldVehicleReturnRequest form);
    FieldVehicleFuelVO saveFuel(FieldVehicleFuelSaveRequest form);
    FieldVehicleFuelVO updateFuel(FieldVehicleFuelUpdateRequest form);
    void deleteFuel(FieldVehicleFuelDeleteRequest form);
}
